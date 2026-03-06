package top.inept.blog.feature.comment.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.feature.comment.model.dto.CreateCommentDTO
import top.inept.blog.feature.comment.model.vo.CommentVO
import top.inept.blog.feature.comment.service.CommentService

@Tag(name = "评论接口")
@SecurityRequirement(name = "accessToken")
@RestController
@RequestMapping("/user/comment")
@Validated
class UserCommentController(
    private val commentService: CommentService
) {
    @PreAuthorize("hasAuthority('user:comment:create')")
    @Operation(summary = "创建评论")
    @PostMapping("/{id}")
    fun createComment(
        @Parameter(description = "openapi.comment.article_id", required = true)
        @PathVariable
        id: Long,
        @Valid @RequestBody dto: CreateCommentDTO
    ): ResponseEntity<CommentVO> {
        return ResponseEntity.ok(commentService.createComment(id, dto))
    }
}