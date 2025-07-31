package top.inept.blog.feature.article.service.impl

import org.springframework.context.support.MessageSourceAccessor
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import top.inept.blog.base.QueryBuilder
import top.inept.blog.exception.NotFoundException
import top.inept.blog.extensions.get
import top.inept.blog.extensions.toPageResponse
import top.inept.blog.feature.article.pojo.convert.toHomeArticleVO
import top.inept.blog.feature.article.pojo.dto.ArticleQueryDTO
import top.inept.blog.feature.article.pojo.dto.CreateArticleDTO
import top.inept.blog.feature.article.pojo.dto.UpdateArticleDTO
import top.inept.blog.feature.article.pojo.dto.UpdateArticleStatusDTO
import top.inept.blog.feature.article.pojo.entity.Article
import top.inept.blog.feature.article.pojo.vo.HomeArticleVO
import top.inept.blog.feature.article.repository.ArticleRepository
import top.inept.blog.feature.article.repository.ArticleSpecs
import top.inept.blog.feature.article.repository.model.ArticleTitleDTO
import top.inept.blog.feature.article.service.ArticleService
import top.inept.blog.feature.categories.service.CategoriesService
import top.inept.blog.feature.tag.service.TagService
import top.inept.blog.feature.user.service.UserService
import top.inept.blog.utils.SecurityUtil

@Service
class ArticleServiceImpl(
    private val articleRepository: ArticleRepository,
    private val userService: UserService,
    private val categoriesService: CategoriesService,
    private val tagService: TagService,
    private val messages: MessageSourceAccessor,
) : ArticleService {
    override fun getArticles(): List<Article> = articleRepository.findAll()

    override fun getArticleById(id: Long): Article {
        //根据id查找文章
        val articles = articleRepository.findByIdOrNull(id)

        //判断文章是否存在
        if (articles == null) throw NotFoundException(messages["message.articles.articles_not_found"])

        return articles
    }

    override fun createArticle(createArticleDTO: CreateArticleDTO): Article {
        //判断文章slug是否重复
        if (articleRepository.existsBySlug(createArticleDTO.slug)) throw Exception(messages["message.articles.duplicate_slug"])

        //从上下文获取用户名
        val username = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw Exception("message.common.missing_user_context")

        //根据用户名获取用户
        val user = userService.getUserByUsername(username)

        //根据id查找分类
        val categories = categoriesService.getCategoriesById(createArticleDTO.categoryId)

        //获取标签
        val tags = tagService.getTagsByIds(createArticleDTO.tagIds)

        return articleRepository.save(
            Article(
                title = createArticleDTO.title,
                slug = createArticleDTO.slug,
                content = createArticleDTO.content,
                author = user,
                category = categories,
                tags = tags.toMutableSet(),
            )
        )
    }

    //TODO 考虑是否要更新author
    override fun updateArticle(updateArticleDTO: UpdateArticleDTO): Article {
        //根据id查找文章
        val dbArticle = articleRepository.findByIdOrNull(updateArticleDTO.id)

        //判断文章是否存在
        if (dbArticle == null) throw NotFoundException(messages["message.articles.articles_not_found"])

        //判断文章slug是否重复
        if (updateArticleDTO.slug != dbArticle.slug && articleRepository.existsBySlug(updateArticleDTO.slug))
            throw Exception(messages["message.articles.duplicate_slug"])

        //从上下文获取用户名
        val username = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw Exception("message.common.missing_user_context")

        //根据用户名获取用户
        val user = userService.getUserByUsername(username)

        //根据id查找分类
        val categories = categoriesService.getCategoriesById(updateArticleDTO.categoryId)

        //获取标签
        val tags = tagService.getTagsByIds(updateArticleDTO.tagIds)

        return articleRepository.save(
            Article(
                id = updateArticleDTO.id,
                title = updateArticleDTO.title,
                slug = updateArticleDTO.slug,
                content = updateArticleDTO.content,
                author = user,
                category = categories,
                tags = tags.toMutableSet(),
            )
        )
    }

    override fun deleteArticle(id: Long) {
        //根据id判断文章是否存在
        existsArticleById(id)

        //删除文章
        articleRepository.deleteById(id)
    }

    override fun updateArticleStatus(updateArticleStatusDTO: UpdateArticleStatusDTO): Article {
        //根据id查找文章
        val dbArticle = articleRepository.findByIdOrNull(updateArticleStatusDTO.id)

        //判断文章是否存在
        if (dbArticle == null) throw NotFoundException(messages["message.articles.articles_not_found"])

        dbArticle.articleStatus = updateArticleStatusDTO.articleStatus

        return articleRepository.save(dbArticle)
    }

    override fun getArticleTitleById(articleIds: List<Long>): List<ArticleTitleDTO> {
        return articleRepository.findTitleAllById(articleIds)
    }

    override fun getArticleTitleById(articleId: Long): ArticleTitleDTO {
        //按id查找文章
        val articleTitleDTO = articleRepository.findTitleByIdOrNull(articleId)

        //未找到文章
        if (articleTitleDTO == null) throw NotFoundException(messages["message.articles.articles_not_found"])

        return articleTitleDTO
    }

    override fun existsArticleById(id: Long): Boolean {
        if (!articleRepository.existsById(id)) throw NotFoundException(messages["message.articles.articles_not_found"])
        return true
    }

    override fun getHomeArticles(articleQueryDTO: ArticleQueryDTO): Page<Article> {
        val pageRequest = PageRequest.of(articleQueryDTO.page - 1, articleQueryDTO.size)

        val specs = QueryBuilder<Article>()
            .and(ArticleSpecs.hasCategory(articleQueryDTO.category))
            .and(ArticleSpecs.hasTags(articleQueryDTO.tagIds))
            .and(ArticleSpecs.titleOrContentContains(articleQueryDTO.title, articleQueryDTO.content))
            .and(ArticleSpecs.hasArticleStatus(articleQueryDTO.articleStatus))
            .buildSpec()

        return articleRepository.findAll(specs, pageRequest)
    }
}