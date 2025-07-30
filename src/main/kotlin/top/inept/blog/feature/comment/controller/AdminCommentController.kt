package top.inept.blog.feature.comment.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.base.ApiResponse
import top.inept.blog.feature.comment.pojo.dto.CreateCommentDTO
import top.inept.blog.feature.comment.pojo.dto.UpdateCommentDTO
import top.inept.blog.feature.comment.pojo.vo.CommentReplyVO
import top.inept.blog.feature.comment.pojo.vo.CommentSummaryVO
import top.inept.blog.feature.comment.pojo.vo.CommentVO
import top.inept.blog.feature.comment.service.CommentService

@Tag(name = "管理员评论接口")
@RestController
@RequestMapping("/admin/comment")
@Validated
class AdminCommentController(
    private val commentService: CommentService
) {
    @Operation(summary = "获取评论列表")
    @GetMapping
    fun getComments(): ApiResponse<List<CommentVO>> {
        return ApiResponse.success(commentService.getComments())
    }

    @Operation(summary = "根据id获取评论")
    @GetMapping("/{id}")
    fun getCommentById(@PathVariable id: Long): ApiResponse<CommentVO> {
        return ApiResponse.success(commentService.getCommentById(id))
    }

    @Operation(summary = "创建评论")
    @PostMapping
    fun createComment(@Valid @RequestBody createCommentDTO: CreateCommentDTO): ApiResponse<CommentVO> {
        return ApiResponse.success(commentService.createComment(createCommentDTO))
    }

    @Operation(summary = "更新评论")
    @PutMapping
    fun updateComment(@Valid @RequestBody updateCommentDTO: UpdateCommentDTO): ApiResponse<CommentSummaryVO> {
        return ApiResponse.success(commentService.updateComment(updateCommentDTO))
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    fun deleteComment(@PathVariable id: Long): ApiResponse<Boolean> {
        commentService.deleteComment(id)
        return ApiResponse.success(true)
    }

    @Operation(summary = "获得评论回复列表")
    @GetMapping("/{id}/replies")
    fun getCommentReplies(@PathVariable id: Long): ApiResponse<List<CommentReplyVO>> {
        return ApiResponse.success(commentService.getCommentReplies(id))
    }
}