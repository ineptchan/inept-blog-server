package top.inept.blog.feature.comment.service.impl

import com.querydsl.core.BooleanBuilder
import jakarta.persistence.EntityManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import top.inept.blog.base.BaseQueryDTO
import top.inept.blog.base.PageResponse
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.ArticleErrorCode
import top.inept.blog.exception.error.CommentErrorCode
import top.inept.blog.exception.error.UserErrorCode
import top.inept.blog.extensions.toPageRequest
import top.inept.blog.extensions.toPageResponse
import top.inept.blog.extensions.toPageResponseTransformNotNull
import top.inept.blog.feature.article.model.convert.toArticleTitleVO
import top.inept.blog.feature.article.model.entity.Article
import top.inept.blog.feature.article.service.ArticleService
import top.inept.blog.feature.comment.model.convert.toCommentReplyVO
import top.inept.blog.feature.comment.model.convert.toCommentSummaryVO
import top.inept.blog.feature.comment.model.convert.toCommentVO
import top.inept.blog.feature.comment.model.convert.toTopCommentVO
import top.inept.blog.feature.comment.model.dto.CreateAnonymousCommentDTO
import top.inept.blog.feature.comment.model.dto.CreateCommentDTO
import top.inept.blog.feature.comment.model.dto.QueryCommentDTO
import top.inept.blog.feature.comment.model.dto.UpdateCommentDTO
import top.inept.blog.feature.comment.model.entity.Comment
import top.inept.blog.feature.comment.model.entity.QComment
import top.inept.blog.feature.comment.model.vo.CommentReplyVO
import top.inept.blog.feature.comment.model.vo.CommentSummaryVO
import top.inept.blog.feature.comment.model.vo.CommentVO
import top.inept.blog.feature.comment.model.vo.TopCommentVO
import top.inept.blog.feature.comment.repository.CommentRepository
import top.inept.blog.feature.comment.service.CommentService
import top.inept.blog.feature.user.service.UserService
import top.inept.blog.utils.SecurityUtil

@Service
class CommentServiceImpl(
    private val commentRepository: CommentRepository,
    private val articleService: ArticleService,
    private val userService: UserService,
    private val entityManager: EntityManager,
) : CommentService {
    override fun getComments(dto: QueryCommentDTO): PageResponse<CommentVO> {
        val pageRequest = dto.toPageRequest()
        val c = QComment.comment

        val builder = BooleanBuilder().apply {
            dto.keyword?.takeIf { it.isNotBlank() }?.let { kw ->
                and(c.content.containsIgnoreCase(kw))
            }
        }

        //获取全部评论
        val comments = commentRepository.findAll(builder, pageRequest)

        //获取文章id列表
        val articleIds = comments.map { it.article.id }.distinct()

        // 获取文章标题信息并构建id -> 标题的映射
        val articleTitleMap = articleService.getArticleTitleById(articleIds).associateBy { it.id }

        //构建VO
        return comments.toPageResponseTransformNotNull { comment ->
            val articleTitle = articleTitleMap[comment.article.id]
            articleTitle?.let { comment.toCommentVO(it.toArticleTitleVO()) }
        }
    }

    override fun getCommentById(id: Long): CommentVO {
        //根据id查找评论
        val comment = commentRepository.findCommentsById(id)
            ?: throw BusinessException(CommentErrorCode.ID_NOT_FOUND)

        //获得文章标题
        val articleTitle = articleService.getArticleTitleById(comment.article.id)

        return comment.toCommentVO(articleTitle.toArticleTitleVO())
    }

    override fun createComment(dto: CreateCommentDTO): CommentVO {
        //从上下文获取用户名
        val username = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw BusinessException(UserErrorCode.USERNAME_MISSING_CONTEXT)

        //根据用户名获取用户
        val user = userService.getUserByUsername(username)

        //判断有没有父级评论
        val parentComment = dto.parentCommentId?.let {
            commentRepository.findCommentsById(it)
                ?: throw BusinessException(CommentErrorCode.PARENT_COMMENT_ID_NOT_FOUND, it)
        }

        //获得文章id按父级评论或者DTO的文章id
        val articleId = parentComment?.article?.id ?: dto.articleId

        //获取文章标题
        val articleTitle = articleService.getArticleTitleById(articleId)

        //创建文章代理对象
        val article = entityManager.getReference(Article::class.java, articleTitle.id)

        val comment = Comment(
            content = dto.content,
            article = article,
            user = user,
            parentComment = parentComment
        )

        return commentRepository.save(comment).toCommentVO(articleTitle.toArticleTitleVO())
    }

    override fun updateComment(id: Long, dto: UpdateCommentDTO): CommentSummaryVO {
        //根据id查找评论
        val dbComment = commentRepository.findCommentsById(id)
            ?: throw BusinessException(CommentErrorCode.ID_NOT_FOUND)

        dbComment.apply {
            dto.content?.let { content = it }
        }

        commentRepository.saveAndFlush(dbComment)

        return dbComment.toCommentSummaryVO()
    }

    override fun deleteComment(id: Long) {
        //根据id判断评论是否存在
        if (!commentRepository.existsById(id)) throw BusinessException(CommentErrorCode.ID_NOT_FOUND)

        //删除评论
        commentRepository.deleteById(id)
    }

    override fun getCommentReplies(commentId: Long, dto: BaseQueryDTO): PageResponse<CommentReplyVO> {
        if (!commentRepository.existsById(commentId))
            throw BusinessException(CommentErrorCode.ID_NOT_FOUND)

        val pageRequest = dto.toPageRequest()
        val c = QComment.comment

        val builder = BooleanBuilder()
            .and(c.parentComment.id.eq(commentId))

        return commentRepository.findAll(builder, pageRequest).toPageResponse { it.toCommentReplyVO() }
    }

    override fun getTopComments(articleId: Long, dto: BaseQueryDTO): PageResponse<TopCommentVO> {
        //判断文章是否存在
        if (!articleService.existsArticleById(articleId))
            throw BusinessException(ArticleErrorCode.ID_NOT_FOUND, articleId)

        val pageRequest = dto.toPageRequest()
        val c = QComment.comment

        //根据文章id获取顶级评论
        val builder = BooleanBuilder()
            .and(c.article.id.eq(articleId))
            .and(c.parentComment.isNull)

        return commentRepository.findAll(builder, pageRequest).toPageResponse { it.toTopCommentVO() }
    }

    override fun createAnonymousComment(dto: CreateAnonymousCommentDTO): CommentVO {
        TODO("Not yet implemented")
    }
}