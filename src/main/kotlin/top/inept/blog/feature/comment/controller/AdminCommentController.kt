package top.inept.blog.feature.comment.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.base.BaseQueryDTO
import top.inept.blog.base.PageResponse
import top.inept.blog.feature.comment.model.dto.CreateCommentDTO
import top.inept.blog.feature.comment.model.dto.QueryCommentDTO
import top.inept.blog.feature.comment.model.dto.UpdateCommentDTO
import top.inept.blog.feature.comment.model.vo.CommentReplyVO
import top.inept.blog.feature.comment.model.vo.CommentSummaryVO
import top.inept.blog.feature.comment.model.vo.CommentVO
import top.inept.blog.feature.comment.service.CommentService

@Tag(name = "管理员评论接口")
@RestController
@RequestMapping("/admin/comment")
@Validated
class AdminCommentController(
    private val commentService: CommentService
) {
    @PreAuthorize("hasAuthority('admin:comment:read')")
    @Operation(summary = "获取评论列表")
    @GetMapping
    fun getComments(@Valid queryCommentDTO: QueryCommentDTO): ResponseEntity<PageResponse<CommentVO>> {
        return ResponseEntity.ok(commentService.getComments(queryCommentDTO))
    }

    @PreAuthorize("hasAuthority('admin:comment:read')")
    @Operation(summary = "根据id获取评论")
    @GetMapping("/{id}")
    fun getCommentById(@PathVariable id: Long): ResponseEntity<CommentVO> {
        return ResponseEntity.ok(commentService.getCommentById(id))
    }

    @PreAuthorize("hasAuthority('admin:comment:write')")
    @Operation(summary = "创建评论")
    @PostMapping
    fun createComment(@Valid @RequestBody createCommentDTO: CreateCommentDTO): ResponseEntity<CommentVO> {
        return ResponseEntity.ok(commentService.createComment(createCommentDTO))
    }

    @PreAuthorize("hasAuthority('admin:comment:modify')")
    @Operation(summary = "更新评论")
    @PatchMapping("/{id}")
    fun updateComment(
        @Valid @RequestBody updateCommentDTO: UpdateCommentDTO,
        @PathVariable id: Long
    ): ResponseEntity<CommentSummaryVO> {
        return ResponseEntity.ok(commentService.updateComment(id, updateCommentDTO))
    }

    @PreAuthorize("hasAuthority('admin:comment:delete')")
    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    fun deleteComment(@PathVariable id: Long): ResponseEntity<Boolean> {
        commentService.deleteComment(id)
        return ResponseEntity.ok(true)
    }

    @PreAuthorize("hasAuthority('admin:comment:read')")
    @Operation(summary = "获得评论回复列表")
    @GetMapping("/{commentId}/replies")
    fun getCommentReplies(
        @PathVariable commentId: Long,
        @Valid baseQueryDTO: BaseQueryDTO
    ): ResponseEntity<PageResponse<CommentReplyVO>> {
        return ResponseEntity.ok(commentService.getCommentReplies(commentId, baseQueryDTO))
    }
}