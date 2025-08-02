package top.inept.blog.feature.comment.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.inept.blog.base.ApiResponse
import top.inept.blog.base.BaseQueryDTO
import top.inept.blog.base.PageResponse
import top.inept.blog.extensions.toApiResponse
import top.inept.blog.feature.comment.pojo.vo.CommentReplyVO
import top.inept.blog.feature.comment.pojo.vo.TopCommentVO
import top.inept.blog.feature.comment.service.CommentService

@Tag(name = "公开评论接口")
@RestController
@RequestMapping("/open/comment")
@Validated
class OpenCommentController(
    private val commentService: CommentService
) {
    @Operation(summary = "获取顶级评论列表")
    @GetMapping("/{articleId}")
    fun getTopComments(
        @PathVariable articleId: Long,
        @Valid baseQueryDTO: BaseQueryDTO
    ): ApiResponse<PageResponse<TopCommentVO>> {
        return ApiResponse.success(commentService.getTopComments(articleId, baseQueryDTO))
    }

    /*    @Operation(summary = "创建匿名评论")
        @PostMapping
        fun createAnonymousComment(@Valid @RequestBody createAnonymousCommentDTO: CreateAnonymousCommentDTO): ApiResponse<CommentVO> {
            return ApiResponse.success(commentService.createAnonymousComment(createAnonymousCommentDTO))
        }*/

    @Operation(summary = "获得评论回复列表")
    @GetMapping("/{commentId}/replies")
    fun getCommentReplies(
        @PathVariable commentId: Long,
        @Valid baseQueryDTO: BaseQueryDTO
    ): ApiResponse<PageResponse<CommentReplyVO>> {
        return commentService.getCommentReplies(commentId, baseQueryDTO).toApiResponse()
    }
}