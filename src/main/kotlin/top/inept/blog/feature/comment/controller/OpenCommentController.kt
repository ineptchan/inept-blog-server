package top.inept.blog.feature.comment.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.inept.blog.base.ApiResponse
import top.inept.blog.feature.comment.pojo.dto.CreateCommentDTO
import top.inept.blog.feature.comment.pojo.vo.CommentReplyVO
import top.inept.blog.feature.comment.pojo.vo.CommentVO
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
    fun getTopComments(@PathVariable articleId: Long): ApiResponse<List<TopCommentVO>> {
        return ApiResponse.success(commentService.getTopComments(articleId))
    }

    //TODO 登录用户
    @Operation(summary = "创建评论")
    @PostMapping
    fun createComment(@Valid @RequestBody createCommentDTO: CreateCommentDTO): ApiResponse<CommentVO> {
        return ApiResponse.success(commentService.createComment(createCommentDTO))
    }

    @Operation(summary = "获得评论回复列表")
    @GetMapping("/{articleId}/replies")
    fun getCommentReplies(@PathVariable articleId: Long): ApiResponse<List<CommentReplyVO>> {
        return ApiResponse.success(commentService.getCommentReplies(articleId))
    }
}