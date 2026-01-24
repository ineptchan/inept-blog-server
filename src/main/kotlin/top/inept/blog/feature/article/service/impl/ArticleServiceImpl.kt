package top.inept.blog.feature.article.service.impl

//import top.inept.blog.feature.article.repository.ArticleSpecs
import com.querydsl.core.BooleanBuilder
import org.hibernate.exception.ConstraintViolationException
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import top.inept.blog.exception.DbDuplicateException
import top.inept.blog.exception.NotFoundException
import top.inept.blog.extensions.get
import top.inept.blog.extensions.toPageRequest
import top.inept.blog.feature.article.model.dto.CreateArticleDTO
import top.inept.blog.feature.article.model.dto.QueryArticleDTO
import top.inept.blog.feature.article.model.dto.UpdateArticleDTO
import top.inept.blog.feature.article.model.dto.UpdateArticleStatusDTO
import top.inept.blog.feature.article.model.entity.Article
import top.inept.blog.feature.article.model.entity.QArticle
import top.inept.blog.feature.article.model.entity.constraints.ArticleConstraints
import top.inept.blog.feature.article.repository.ArticleRepository
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
        val dbArticles = articleRepository.findByIdOrNull(id)
            ?: throw NotFoundException(messages["message.articles.not_found"])

        return dbArticles
    }

    override fun createArticle(dto: CreateArticleDTO): Article {
        //从上下文获取用户名
        val username = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw Exception("message.common.missing_user_context")

        //根据用户名获取用户
        val user = userService.getUserByUsername(username)

        //根据id查找分类
        val categories = categoriesService.getCategoriesById(dto.categoryId)

        //获取标签
        val tags = tagService.getTagsByIds(dto.tagIds)

        val dbArticle = Article(
            title = dto.title,
            slug = dto.slug,
            content = dto.content,
            author = user,
            category = categories,
            tags = tags.toMutableSet(),
        )

        try {
            articleRepository.saveAndFlush(dbArticle)
        } catch (e: DataIntegrityViolationException) {
            val violation = e.cause as? ConstraintViolationException
            when (violation?.constraintName) {
                ArticleConstraints.UNIQUE_SLUG -> throw DbDuplicateException(dbArticle.slug)
            }
        }

        return dbArticle
    }

    override fun updateArticle(id: Long, dto: UpdateArticleDTO): Article {
        //根据id查找文章
        val dbArticle = articleRepository.findByIdOrNull(id)
            ?: throw NotFoundException(messages["message.articles.not_found"])

        //从上下文获取用户名
        val username = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw Exception("message.common.missing_user_context")

        //根据用户名获取用户
        val user = userService.getUserByUsername(username)

        //TODO 复查功能？
        //判断所属用户
        if (dbArticle.author.id != user.id) throw Exception(messages["message.articles.permission_denied"])

        dbArticle.apply {
            dto.title?.let { title = it }
            dto.slug?.let { slug = it }
            dto.content?.let { content = it }
            dto.categoryId?.let {
                category = categoriesService.getCategoriesById(it)
            }
            dto.tagIds?.let {
                this.tags = tagService.getTagsByIds(it).toMutableSet()
            }
            dto.articleStatus?.let { articleStatus = it }
        }

        try {
            articleRepository.saveAndFlush(dbArticle)
        } catch (e: DataIntegrityViolationException) {
            val violation = e.cause as? ConstraintViolationException
            when (violation?.constraintName) {
                ArticleConstraints.UNIQUE_SLUG -> throw DbDuplicateException(dbArticle.slug)
            }
        }

        return dbArticle
    }

    override fun deleteArticle(id: Long) {
        //根据id判断文章是否存在
        if (!existsArticleById(id)) throw Exception(messages["message.articles.not_found"])

        //删除文章
        articleRepository.deleteById(id)
    }

    override fun updateArticleStatus(dto: UpdateArticleStatusDTO) {
        //批量更新文章状态
        articleRepository.updateStatusByIds(dto.articleStatus, dto.articleIds)
    }

    override fun getArticleTitleById(articleIds: List<Long>): List<ArticleTitleDTO> {
        return articleRepository.findTitleAllById(articleIds)
    }

    override fun getArticleTitleById(articleId: Long): ArticleTitleDTO {
        //按id查找文章
        val articleTitleDTO = articleRepository.findTitleByIdOrNull(articleId)

        //未找到文章
        if (articleTitleDTO == null) throw NotFoundException(messages["message.articles.not_found"])

        return articleTitleDTO
    }

    override fun existsArticleById(id: Long): Boolean {
        return articleRepository.existsById(id)
    }
    
    override fun getHomeArticles(dto: QueryArticleDTO): Page<Article> {
        val pageRequest = dto.toPageRequest()
        val a = QArticle.article

        val predicate = BooleanBuilder().apply {
            dto.category?.let { and(a.category.id.eq(it)) }
            dto.articleStatus?.let { and(a.articleStatus.eq(it)) }
            dto.tagIds?.takeIf { it.isNotEmpty() }?.let { and(a.tags.any().id.`in`(it)) }
            dto.keyword?.takeIf { it.isNotBlank() }?.let { kw ->
                and(
                    a.title.containsIgnoreCase(kw)
                        .or(a.content.containsIgnoreCase(kw))
                        .or(a.slug.containsIgnoreCase(kw))
                )
            }
        }

        return articleRepository.findAll(predicate, pageRequest)
    }
}