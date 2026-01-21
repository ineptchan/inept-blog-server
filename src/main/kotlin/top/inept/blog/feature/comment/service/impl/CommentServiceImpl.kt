package top.inept.blog.feature.comment.service.impl

import jakarta.persistence.EntityManager
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import top.inept.blog.base.BaseQueryDTO
import top.inept.blog.base.PageResponse
import top.inept.blog.base.QueryBuilder
import top.inept.blog.exception.NotFoundException
import top.inept.blog.extensions.get
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
import top.inept.blog.feature.comment.model.vo.CommentReplyVO
import top.inept.blog.feature.comment.model.vo.CommentSummaryVO
import top.inept.blog.feature.comment.model.vo.CommentVO
import top.inept.blog.feature.comment.model.vo.TopCommentVO
import top.inept.blog.feature.comment.repository.CommentRepository
import top.inept.blog.feature.comment.repository.CommentSpecs
import top.inept.blog.feature.comment.service.CommentService
import top.inept.blog.feature.user.service.UserService
import top.inept.blog.utils.SecurityUtil

@Service
class CommentServiceImpl(
    private val commentRepository: CommentRepository,
    private val articleService: ArticleService,
    private val userService: UserService,
    private val entityManager: EntityManager,
    private val messages: MessageSourceAccessor,
) : CommentService {
    override fun getComments(dto: QueryCommentDTO): PageResponse<CommentVO> {
        val pageRequest = dto.toPageRequest()

        val specs = QueryBuilder<Comment>()
            .and(CommentSpecs.contentContains(dto.keyword))
            .buildSpec()

        //获取全部评论
        val comments = commentRepository.findAll(specs, pageRequest)

        //获取文章id列表
        val articleIds = comments.map { it.article.id }.distinct()

        // 获取文章标题信息并构建id -> 标题的映射
        val articleTitleMap = articleService.getArticleTitleById(articleIds).associateBy { it.id }

        //构建VO
        return comments.toPageResponseTransformNotNull { comment ->
            val articleTitle = articleTitleMap[comment.article.id]
            if (articleTitle != null) {
                comment.toCommentVO(articleTitle.toArticleTitleVO())

            } else {
                null
            }
        }
    }

    override fun getCommentById(id: Long): CommentVO {
        //根据id查找评论
        val comment = commentRepository.findCommentsById(id)
            ?: throw NotFoundException(messages["message.comment.comment_not_found"])

        //获得文章标题
        val articleTitle = articleService.getArticleTitleById(comment.article.id)

        return comment.toCommentVO(articleTitle.toArticleTitleVO())
    }

    override fun createComment(dto: CreateCommentDTO): CommentVO {
        //从上下文获取用户名
        val username = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw Exception("message.common.missing_user_context")

        //根据用户名获取用户
        val user = userService.getUserByUsername(username)

        //判断有没有父级评论
        val parentComment = dto.parentCommentId?.let {
            commentRepository.findCommentsById(it)
                ?: throw NotFoundException(messages["message.comment.parent_comment_not_found"])
        }

        //获得文章id按父级评论或者DTO的文章id
        val articleId = parentComment?.article?.id
            ?: dto.articleId
            ?: throw Exception(messages["message.comment.article_id_required"])

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
            ?: throw NotFoundException(messages["message.comment.comment_not_found"])

        dbComment.apply {
            dto.content?.let { content = it }
        }

        commentRepository.saveAndFlush(dbComment)

        return dbComment.toCommentSummaryVO()
    }

    override fun deleteComment(id: Long) {
        //根据id判断评论是否存在
        if (!commentRepository.existsById(id)) throw NotFoundException(messages["message.comment.comment_not_found"])

        //删除评论
        commentRepository.deleteById(id)
    }

    override fun getCommentReplies(commentId: Long, baseQueryDTO: BaseQueryDTO): PageResponse<CommentReplyVO> {
        //根据id查找评论
        val comment = commentRepository.findCommentsById(commentId)
            ?: throw NotFoundException(messages["message.comment.comment_not_found"])

        val pageRequest = baseQueryDTO.toPageRequest()

        val specs = QueryBuilder<Comment>()
            .and(CommentSpecs.byParentComment(comment))
            .buildSpec()

        return commentRepository.findAll(specs, pageRequest).toPageResponse { it.toCommentReplyVO() }
    }

    override fun getTopComments(articleId: Long, baseQueryDTO: BaseQueryDTO): PageResponse<TopCommentVO> {
        //根据id获得文章
        articleService.existsArticleById(articleId)

        //根据文章id获取顶级评论
        val pageRequest = baseQueryDTO.toPageRequest()

        val specs = QueryBuilder<Comment>()
            .and(CommentSpecs.byArticleId(articleId))
            .and(CommentSpecs.isRootComment())
            .buildSpec()

        val comments = commentRepository.findAll(specs, pageRequest)

        return comments.toPageResponse { it.toTopCommentVO() }
    }

    override fun createAnonymousComment(dto: CreateAnonymousCommentDTO): CommentVO {
        TODO("Not yet implemented")
    }
}