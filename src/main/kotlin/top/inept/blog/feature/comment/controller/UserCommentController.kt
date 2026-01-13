package top.inept.blog.feature.comment.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.inept.blog.feature.comment.model.dto.CreateCommentDTO
import top.inept.blog.feature.comment.model.vo.CommentVO
import top.inept.blog.feature.comment.service.CommentService

@Tag(name = "用户评论接口")
@RestController
@RequestMapping("/user/comment")
@Validated
class UserCommentController(
    private val commentService: CommentService
) {
    @Operation(summary = "创建评论")
    @PostMapping
    fun createComment(@Valid @RequestBody createCommentDTO: CreateCommentDTO): ResponseEntity<CommentVO> {
        return ResponseEntity.ok(commentService.createComment(createCommentDTO))
    }
}