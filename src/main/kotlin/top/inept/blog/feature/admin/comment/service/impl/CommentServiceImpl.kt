package top.inept.blog.feature.admin.comment.service.impl

import jakarta.persistence.EntityManager
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import top.inept.blog.exception.NotFoundException
import top.inept.blog.extensions.get
import top.inept.blog.feature.admin.article.pojo.convert.toArticleTitleVO
import top.inept.blog.feature.admin.article.pojo.entity.Article
import top.inept.blog.feature.admin.article.service.ArticleService
import top.inept.blog.feature.admin.comment.pojo.convert.toCommentReplyVO
import top.inept.blog.feature.admin.comment.pojo.convert.toCommentSummaryVO
import top.inept.blog.feature.admin.comment.pojo.convert.toCommentVO
import top.inept.blog.feature.admin.comment.pojo.dto.CreateCommentDTO
import top.inept.blog.feature.admin.comment.pojo.dto.UpdateCommentDTO
import top.inept.blog.feature.admin.comment.pojo.entity.Comment
import top.inept.blog.feature.admin.comment.pojo.vo.CommentReplyVO
import top.inept.blog.feature.admin.comment.pojo.vo.CommentSummaryVO
import top.inept.blog.feature.admin.comment.pojo.vo.CommentVO
import top.inept.blog.feature.admin.comment.repository.CommentRepository
import top.inept.blog.feature.admin.comment.service.CommentService
import top.inept.blog.feature.admin.user.service.UserService
import top.inept.blog.utils.SecurityUtil

@Service
class CommentServiceImpl(
    private val commentRepository: CommentRepository,
    private val articleService: ArticleService,
    private val userService: UserService,
    private val entityManager: EntityManager,
    private val messages: MessageSourceAccessor,
) : CommentService {
    override fun getComments(): List<CommentVO> {
        //获取全部评论
        val comments = commentRepository.findAll()

        //获取文章id列表
        val articleIds = comments.map { it.article.id }.distinct()

        // 获取文章标题信息并构建id -> 标题的映射
        val articleTitleMap = articleService
            .getArticleTitleById(articleIds)
            .associateBy { it.id }

        //构建VO
        return comments.mapNotNull { comment ->
            val articleTitle = articleTitleMap[comment.article.id]
            if (articleTitle != null) {
                comment.toCommentVO(articleTitle.toArticleTitleVO())
            } else {
                null // 跳过找不到文章标题的评论
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
        //获得文章标题
        val articleTitle = articleService.getArticleTitleById(createCommentDTO.articleId)

        val article = entityManager.getReference(Article::class.java, articleTitle.id)

        //从上下文获取用户名
        val username = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw Exception("message.common.unknown_user")

        //根据用户名获取用户
        val user = userService.getUserByUsername(username)

        //TODO 考虑是否抛出错误还是成为顶级评论
        val parentComment = createCommentDTO.parentCommentId?.let { commentRepository.findByIdOrNull(it) }

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

    override fun getCommentReplies(id: Long): List<CommentReplyVO> {
        //根据id查找评论
        val comment = commentRepository.findByIdOrNull(id)

        //判断评论是否存在
        if (comment == null) throw NotFoundException(messages["message.comment.comment_not_found"])

        //从数据库获取回复
        val replies = comment.replies

        //构建VO
        return replies.map { it.toCommentReplyVO() }
    }
}