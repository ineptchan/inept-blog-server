package top.inept.blog.feature.file.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import top.inept.blog.feature.file.model.vo.FileStorageVO
import top.inept.blog.feature.file.service.FileStorageService

@Tag(name = "文件接口")
@SecurityRequirement(name = "accessToken")
@RestController
@RequestMapping("/file")
@Validated
class AdminFileStorageController(
    private val fileStorageService: FileStorageService
) {
    @PreAuthorize("hasAuthority('admin:file:write')")
    @Operation(summary = "上传文件")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(@RequestPart("file") file: MultipartFile): ResponseEntity<FileStorageVO> {
        return ResponseEntity.ok(fileStorageService.upload(file))
    }

    @Operation(summary = "按照id获取文件")
    @GetMapping("/{id}")
    fun presignedGetUrl(@PathVariable id: Long): ResponseEntity<FileStorageVO> {
        return ResponseEntity.ok(fileStorageService.presignedGetUrl(id))
    }

    @Operation(summary = "按照object获取文件")
    @GetMapping("/object/{objectName}")
    fun presignedGetUrl(@PathVariable objectName: String): ResponseEntity<FileStorageVO> {
        return ResponseEntity.ok(fileStorageService.presignedGetUrl(objectName))
    }

    @Operation(summary = "按照id删除文件")
    @PreAuthorize("hasAuthority('admin:file:delete')")
    @DeleteMapping("/{id}")
    fun deletedFile(@PathVariable id: Long) {
        fileStorageService.delete(id)
    }
}