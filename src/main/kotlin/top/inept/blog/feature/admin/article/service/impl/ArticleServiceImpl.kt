package top.inept.blog.feature.admin.article.service.impl

import org.springframework.context.support.MessageSourceAccessor
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import top.inept.blog.extensions.get
import top.inept.blog.feature.admin.article.pojo.dto.CreateArticleDTO
import top.inept.blog.feature.admin.article.pojo.dto.UpdateArticleDTO
import top.inept.blog.feature.admin.article.pojo.entity.Articles
import top.inept.blog.feature.admin.article.repository.ArticleRepository
import top.inept.blog.feature.admin.article.service.ArticleService
import top.inept.blog.feature.admin.categories.service.CategoriesService
import top.inept.blog.feature.admin.tag.service.TagService
import top.inept.blog.feature.admin.user.service.UserService

@Service
class ArticleServiceImpl(
    private val articleRepository: ArticleRepository,
    private val userService: UserService,
    private val categoriesService: CategoriesService,
    private val tagService: TagService,
    private val messages: MessageSourceAccessor,
) : ArticleService {
    override fun getArticles(): List<Articles> = articleRepository.findAll()

    override fun getArticleById(id: Long): Articles {
        //根据id查找文章
        val articles = articleRepository.findByIdOrNull(id)

        //判断文章是否存在
        if (articles == null) throw Exception(messages["message.articles.articles_not_found"])

        return articles
    }

    override fun createArticle(createArticleDTO: CreateArticleDTO): Articles {
        //判断文章slug是否重复
        if (articleRepository.existsBySlug(createArticleDTO.slug)) throw Exception(messages["message.articles.duplicate_slug"])

        //根据id查找用户
        val user = userService.getUserById(createArticleDTO.authorId)

        //根据id查找分类
        val categories = categoriesService.getCategoriesById(createArticleDTO.categoryId)

        //获取标签
        val tags = tagService.getTagsByIds(createArticleDTO.tagIds)

        return articleRepository.save(
            Articles(
                title = createArticleDTO.title,
                slug = createArticleDTO.slug,
                content = createArticleDTO.content,
                author = user,
                category = categories,
                tags = tags.toMutableSet(),
            )
        )
    }

    override fun updateArticle(updateArticleDTO: UpdateArticleDTO): Articles {
        //根据id查找文章
        val dbArticle = articleRepository.findByIdOrNull(updateArticleDTO.id)

        //判断文章是否存在
        if (dbArticle == null) throw Exception(messages["message.articles.articles_not_found"])

        //判断文章slug是否重复
        if (updateArticleDTO.slug != dbArticle.slug && articleRepository.existsBySlug(updateArticleDTO.slug))
            throw Exception(messages["message.articles.duplicate_slug"])

        //根据id查找用户
        val user = userService.getUserById(updateArticleDTO.authorId)

        //根据id查找分类
        val categories = categoriesService.getCategoriesById(updateArticleDTO.categoryId)

        //获取标签
        val tags = tagService.getTagsByIds(updateArticleDTO.tagIds)

        return articleRepository.save(
            Articles(
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
        if (articleRepository.existsById(id)) throw Exception(messages["message.articles.articles_not_found"])

        //删除文章
        articleRepository.deleteById(id)
    }

}