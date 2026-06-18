package top.inept.blog.feature.objectstorage.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.inept.blog.feature.objectstorage.model.dto.CompleteUploadDTO
import top.inept.blog.feature.objectstorage.model.dto.PresignUploadDTO
import top.inept.blog.feature.objectstorage.model.vo.CompleteUploadVO
import top.inept.blog.feature.objectstorage.model.vo.PresignUploadVO
import top.inept.blog.feature.objectstorage.service.ObjectStorageService

@Tag(name = "对象存储接口")
@RestController
@RequestMapping("/uploads")
@Validated
class UploadsController(
    private val objectStorageService: ObjectStorageService
) {
    @Operation(summary = "预签名上传")
    @PostMapping("/presign-upload")
    fun presignUpload(@Valid @RequestBody dto: PresignUploadDTO): ResponseEntity<PresignUploadVO> {
        return ResponseEntity.ok(objectStorageService.presignUpload(dto))
    }

    @Operation(summary = "完成对象上传")
    @PostMapping("/complete-upload")
    fun completeUpload(@Valid @RequestBody dto: CompleteUploadDTO): ResponseEntity<CompleteUploadVO> {
        return ResponseEntity.ok(objectStorageService.completeUpload(dto))
    }
}