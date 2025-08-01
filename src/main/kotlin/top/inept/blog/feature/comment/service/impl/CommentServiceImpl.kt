package top.inept.blog.feature.comment.service.impl

import jakarta.persistence.EntityManager
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.data.repository.findByIdOrNull
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
import top.inept.blog.feature.article.pojo.convert.toArticleTitleVO
import top.inept.blog.feature.article.pojo.entity.Article
import top.inept.blog.feature.article.service.ArticleService
import top.inept.blog.feature.comment.pojo.convert.toCommentReplyVO
import top.inept.blog.feature.comment.pojo.convert.toCommentSummaryVO
import top.inept.blog.feature.comment.pojo.convert.toCommentVO
import top.inept.blog.feature.comment.pojo.convert.toTopCommentVO
import top.inept.blog.feature.comment.pojo.dto.CreateAnonymousCommentDTO
import top.inept.blog.feature.comment.pojo.dto.CreateCommentDTO
import top.inept.blog.feature.comment.pojo.dto.QueryCommentDTO
import top.inept.blog.feature.comment.pojo.dto.UpdateCommentDTO
import top.inept.blog.feature.comment.pojo.entity.Comment
import top.inept.blog.feature.comment.pojo.vo.CommentReplyVO
import top.inept.blog.feature.comment.pojo.vo.CommentSummaryVO
import top.inept.blog.feature.comment.pojo.vo.CommentVO
import top.inept.blog.feature.comment.pojo.vo.TopCommentVO
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
    override fun getComments(queryCommentDTO: QueryCommentDTO): PageResponse<CommentVO> {
        val pageRequest = queryCommentDTO.toPageRequest()

        val specs = QueryBuilder<Comment>()
            .and(CommentSpecs.contentContains(queryCommentDTO.keyword))
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
        val comment = commentRepository.findByIdOrNull(id)

        //判断评论是否存在
        if (comment == null) throw NotFoundException(messages["message.comment.comment_not_found"])

        //获得文章标题
        val articleTitle = articleService.getArticleTitleById(comment.article.id)

        return comment.toCommentVO(articleTitle.toArticleTitleVO())
    }

    override fun createComment(createCommentDTO: CreateCommentDTO): CommentVO {
        //从上下文获取用户名
        val username = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw Exception("message.common.missing_user_context")

        //根据用户名获取用户
        val user = userService.getUserByUsername(username)

        //判断有没有父级评论
        val parentComment = createCommentDTO.parentCommentId?.let {
            commentRepository.findByIdOrNull(it)
                ?: throw NotFoundException(messages["message.comment.parent_comment_not_found"])
        }

        //获得文章id按父级评论或者DTO的文章id
        val articleId = parentComment?.article?.id
            ?: createCommentDTO.articleId
            ?: throw Exception(messages["message.comment.article_id_required"])

        //获取文章标题
        val articleTitle = articleService.getArticleTitleById(articleId)

        //创建文章代理对象
        val article = entityManager.getReference(Article::class.java, articleTitle.id)

        val comment = Comment(
            content = createCommentDTO.content,
            article = article,
            user = user,
            parentComment = parentComment
        )

        return commentRepository.save(comment).toCommentVO(articleTitle.toArticleTitleVO())
    }

    override fun updateComment(updateCommentDTO: UpdateCommentDTO): CommentSummaryVO {
        //根据id查找评论
        val dbComment = commentRepository.findByIdOrNull(updateCommentDTO.id)

        //判断评论是否存在
        if (dbComment == null) throw NotFoundException(messages["message.comment.comment_not_found"])

        dbComment.apply {
            this.content = updateCommentDTO.content
        }

        return commentRepository.save(dbComment).toCommentSummaryVO()
    }

    override fun deleteComment(id: Long) {
        //根据id判断评论是否存在
        if (!commentRepository.existsById(id)) throw NotFoundException(messages["message.comment.comment_not_found"])

        //删除评论
        commentRepository.deleteById(id)
    }

    override fun getCommentReplies(commentId: Long, baseQueryDTO: BaseQueryDTO): PageResponse<CommentReplyVO> {
        //根据id查找评论
        val comment = commentRepository.findByIdOrNull(commentId)

        //判断评论是否存在
        if (comment == null) throw NotFoundException(messages["message.comment.comment_not_found"])

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

        return comments.toPageResponse {
            //获得子评论数
            it.toTopCommentVO(commentRepository.countByParentCommentId(it.id))
        }
    }

    override fun createAnonymousComment(createAnonymousCommentDTO: CreateAnonymousCommentDTO): CommentVO {
        TODO("Not yet implemented")
    }
}