package top.inept.blog.feature.article.service.impl

import com.querydsl.core.BooleanBuilder
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.ArticleErrorCode
import top.inept.blog.exception.error.CommonErrorCode
import top.inept.blog.exception.error.UserErrorCode
import top.inept.blog.extensions.toPageRequest
import top.inept.blog.feature.article.model.dto.*
import top.inept.blog.feature.article.model.entity.Article
import top.inept.blog.feature.article.model.entity.QArticle
import top.inept.blog.feature.article.model.entity.constraints.ArticleConstraints
import top.inept.blog.feature.article.repository.ArticleRepository
import top.inept.blog.feature.article.repository.model.ArticleTitleDTO
import top.inept.blog.feature.article.service.ArticleService
import top.inept.blog.feature.categories.service.CategoriesService
import top.inept.blog.feature.objectstorage.service.ObjectStorageService
import top.inept.blog.feature.tag.service.TagService
import top.inept.blog.feature.user.service.UserService
import top.inept.blog.utils.SecurityUtil

@Service
class ArticleServiceImpl(
    private val articleRepository: ArticleRepository,
    private val userService: UserService,
    private val categoriesService: CategoriesService,
    private val tagService: TagService,
    private val objectStorageService: ObjectStorageService
) : ArticleService {
    override fun getArticles(): List<Article> = articleRepository.findAll()

    override fun getArticleById(id: Long): Article {
        //根据id查找文章
        return articleRepository.findByIdOrNull(id)
            ?: throw BusinessException(ArticleErrorCode.ID_NOT_FOUND, id)
    }

    override fun createArticle(dto: CreateArticleDTO): Article {
        //从上下文获取用户名
        val username = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw BusinessException(UserErrorCode.USERNAME_MISSING_CONTEXT)

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

        saveAndFlushArticleOrThrow(dbArticle)

        return dbArticle
    }

    override fun updateArticle(id: Long, dto: UpdateArticleDTO): Article {
        //根据id查找文章
        val dbArticle = getArticleById(id)

        //从上下文获取用户名
        val username = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw BusinessException(UserErrorCode.USERNAME_MISSING_CONTEXT)

        //根据用户名获取用户
        userService.getUserByUsername(username)

        //TODO 复查功能？
        //判断所属用户
        // if (dbArticle.author.id != user.id) throw Exception(messages["message.articles.permission_denied"])

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

        saveAndFlushArticleOrThrow(dbArticle)

        return dbArticle
    }

    @Transactional
    override fun deleteArticle(id: Long) {
        //根据id判断文章是否存在
        if (!existsArticleById(id)) throw BusinessException(ArticleErrorCode.ID_NOT_FOUND, id)

        //返回影响数量
        objectStorageService.deleteByOwnerArticleId(id)

        //删除文章
        articleRepository.deleteById(id)
    }

    override fun updateArticleStatus(dto: UpdateArticleStatusDTO) {
        //批量更新文章状态
        articleRepository.updateStatusByIds(dto.articleStatus, dto.articleIds)
    }

    override fun getArticleTitleById(id: List<Long>): List<ArticleTitleDTO> {
        return articleRepository.findTitleAllById(id)
    }

    override fun getArticleTitleById(id: Long): ArticleTitleDTO {
        //按id查找文章
        return articleRepository.findTitleByIdOrNull(id)
            ?: throw BusinessException(ArticleErrorCode.ID_NOT_FOUND, id)
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

    override fun uploadImage(id: Long, dto: UploadArticleImageDTO): String {
        //从上下文获取用户名
        val username = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw BusinessException(UserErrorCode.USERNAME_MISSING_CONTEXT)

        //根据用户名获取用户
        val user = userService.getUserByUsername(username)

        val article = getArticleById(id)

        return objectStorageService.uploadArticleImage(user.id, article, dto)
    }

    override fun uploadFeaturedImage(id: Long, dto: UploadArticleFeaturedImageDTO): String {
        //从上下文获取用户名
        val username = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw BusinessException(UserErrorCode.USERNAME_MISSING_CONTEXT)

        //根据用户名获取用户
        val user = userService.getUserByUsername(username)

        val article = getArticleById(id)

        //上传对象存储并返回封面url
        val featuredImageUrl = objectStorageService.uploadFeaturedImage(user.id, article, dto)

        //保存封面url
        article.featuredImage = featuredImageUrl

        //保存文章
        saveAndFlushArticleOrThrow(article)

        return featuredImageUrl
    }

    override fun uploadVideo(id: Long, dto: UploadArticleVideoDTO): String {
        //从上下文获取用户名
        val username = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw BusinessException(UserErrorCode.USERNAME_MISSING_CONTEXT)

        //根据用户名获取用户
        val user = userService.getUserByUsername(username)

        val article = getArticleById(id)

        return objectStorageService.uploadVideo(user.id, article, dto)
    }

    override fun uploadAttachment(id: Long, dto: UploadArticleAttachmentDTO): String {
        //从上下文获取用户名
        val username = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw BusinessException(UserErrorCode.USERNAME_MISSING_CONTEXT)

        //根据用户名获取用户
        val user = userService.getUserByUsername(username)

        val article = getArticleById(id)

        return objectStorageService.uploadAttachment(user.id, article, dto)
    }

    private fun saveAndFlushArticleOrThrow(dbArticle: Article): Article {
        return try {
            articleRepository.saveAndFlush(dbArticle)
        } catch (e: DataIntegrityViolationException) {
            val violation = e.cause as? ConstraintViolationException
            when (violation?.constraintName) {
                ArticleConstraints.UNIQUE_SLUG ->
                    throw BusinessException(ArticleErrorCode.SLUG_DB_DUPLICATE, dbArticle.slug)

                else -> throw BusinessException(CommonErrorCode.UNKNOWN)
            }
        }
    }
}