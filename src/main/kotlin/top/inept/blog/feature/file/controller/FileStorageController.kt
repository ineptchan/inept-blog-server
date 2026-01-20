package top.inept.blog.feature.file.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import top.inept.blog.feature.file.service.FileStorageService

@Tag(name = "文件接口")
@RestController
@RequestMapping("/file")
@Validated
class FileStorageController(
    private val fileStorageService: FileStorageService
) {
    @PreAuthorize("hasAuthority('admin:file:write')")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(@RequestPart("file") file: MultipartFile): ResponseEntity<String> {
        val url = fileStorageService.upload(file)
        return ResponseEntity.ok(url)
    }

    @GetMapping("/{id}")
    fun presignedGetUrl(@PathVariable id: Long): ResponseEntity<String> {
        val url = fileStorageService.presignedGetUrl(id)
        return ResponseEntity.ok(url)
    }

    @GetMapping("/object/{objectName}")
    fun presignedGetUrl(@PathVariable objectName: String): ResponseEntity<String> {
        val url = fileStorageService.presignedGetUrl(objectName)
        return ResponseEntity.ok(url)
    }

    @PreAuthorize("hasAuthority('admin:file:delete')")
    @DeleteMapping("/{id}")
    fun deletedFile(@PathVariable id: Long) {
        fileStorageService.delete(id)
    }
}