package top.inept.blog.feature.objectstorage

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.expectBody
import top.inept.blog.IntegrationTestBase
import top.inept.blog.feature.article.model.dto.CreateArticleDTO
import top.inept.blog.feature.article.model.vo.ArticleVO
import top.inept.blog.feature.categories.model.dto.CreateCategoriesDTO
import top.inept.blog.feature.categories.model.vo.CategoriesVO
import top.inept.blog.feature.objectstorage.model.dto.CompleteUploadDTO
import top.inept.blog.feature.objectstorage.model.dto.PresignUploadDTO
import top.inept.blog.feature.objectstorage.model.entity.enums.Purpose
import top.inept.blog.feature.objectstorage.model.vo.CompleteUploadVO
import top.inept.blog.feature.objectstorage.model.vo.PresignUploadVO
import top.inept.blog.feature.user.model.vo.UserDetailVO
import java.net.URI
import java.util.*
import kotlin.test.junit5.JUnit5Asserter.fail

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UploadsControllerTest : IntegrationTestBase() {
    @Test
    @Order(1)
    fun `上传头像测试`() {
        val image = mediaResource("images/avatar-valid.png")
        val file = image.file
        val length = file.length()
        val mimeType = "image/png"

        val presignUploadBody = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.AVATAR,
                    contentType = mimeType,
                    fileSize = length,
                    fileName = file.name
                )
            )
            .exchange().expectStatus().isOk
            .expectBody<PresignUploadVO>()
            .returnResult()
            .responseBody
            ?: fail("上传头像测试返回body为空")

        //预签名上传对象存储
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.valueOf(mimeType))
            .contentLength(length)
            .body(image)
            .exchange()
            .expectStatus().isOk

        //完成上传
        val completeUploadBody = httpClient.post().uri("/uploads/complete-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(CompleteUploadDTO(presignUploadBody.id))
            .exchange()
            .expectStatus().isOk
            .expectBody<CompleteUploadVO>()
            .returnResult()
            .responseBody
            ?: fail("完成上传头像测试返回body为空")

        //判断头像是否可访问
        httpClient.get().uri(completeUploadBody.url).exchange().expectStatus().isOk

        //判断头像是否绑定成功
        val userDetailVO = (httpClient.get().uri("/user/user")
            .header("Authorization", "Bearer $adminToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<UserDetailVO>()
            .returnResult()
            .responseBody
            ?: fail("/user/user 接口回去body失败"))

        if (userDetailVO.avatar != completeUploadBody.url) {
            fail("头像与实际不符")
        }
    }

    @Test
    @Order(2)
    fun `超出尺寸头像图片上传测试`() {
        val image = mediaResource("images/avatar-too-large.png")
        val file = image.file
        val length = file.length()
        val mimeType = "image/png"

        val presignUploadBody = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.AVATAR,
                    contentType = mimeType,
                    fileSize = length,
                    fileName = file.name
                )
            )
            .exchange().expectStatus().isOk
            .expectBody<PresignUploadVO>()
            .returnResult()
            .responseBody
            ?: fail("上传头像测试返回body为空")

        //预签名上传对象存储
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.valueOf(mimeType))
            .contentLength(length)
            .body(image)
            .exchange()
            .expectStatus().isOk

        //完成上传
        httpClient.post().uri("/uploads/complete-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(CompleteUploadDTO(presignUploadBody.id))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT)
            .expectBody()
            .jsonPath("$.errorCode").isEqualTo("message.object_storage.avatar_resolution_invalid")
    }

    @Test
    @Order(3)
    fun `超小尺寸头像图片上传测试`() {
        val image = mediaResource("images/avatar-too-small.png")
        val file = image.file
        val length = file.length()
        val mimeType = "image/png"

        val presignUploadBody = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.AVATAR,
                    contentType = mimeType,
                    fileSize = length,
                    fileName = file.name
                )
            )
            .exchange().expectStatus().isOk
            .expectBody<PresignUploadVO>().returnResult()
            .responseBody
            ?: fail("上传头像测试返回body为空")

        //预签名上传对象存储
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.valueOf(mimeType))
            .contentLength(length)
            .body(image)
            .exchange()
            .expectStatus().isOk

        //完成上传
        httpClient.post().uri("/uploads/complete-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(CompleteUploadDTO(presignUploadBody.id))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT)
            .expectBody()
            .jsonPath("$.errorCode").isEqualTo("message.object_storage.avatar_resolution_invalid")
    }

    @Test
    @Order(4)
    fun `空头像图片上传测试`() {
        val image = mediaResource("images/empty.png")
        val file = image.file
        val length = file.length()
        val mimeType = "image/png"

        httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.AVATAR,
                    contentType = mimeType,
                    fileSize = length,
                    fileName = file.name
                )
            )
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT)
    }

    @Test
    @Order(5)
    fun `请求参数伪造空头像图片上传测试`() {
        val image = mediaResource("images/empty.png")
        val file = image.file
        val length = (3 * 1024 * 1024).toLong()
        val mimeType = "image/png"

        val presignUploadBody = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.AVATAR,
                    contentType = mimeType,
                    fileSize = length,
                    fileName = file.name
                )
            )
            .exchange().expectStatus().isOk
            .expectBody<PresignUploadVO>()
            .returnResult()
            .responseBody
            ?: fail("上传头像测试返回body为空")

        //预签名上传对象存储
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.valueOf(mimeType))
            .contentLength(length)
            .body(image)
            .exchange()
            .expectStatus().isOk
            .returnResult()

        //完成上传
        httpClient.post().uri("/uploads/complete-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(CompleteUploadDTO(presignUploadBody.id))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody()
            .jsonPath("$.errorCode").isEqualTo("message.object_storage.object_size_mismatch")
    }

    @Test
    @Order(6)
    fun `假头像图片上传测试`() {
        val image = mediaResource("images/fake-image.png")
        val file = image.file
        val length = image.file.length()
        val mimeType = "image/png"

        val presignUploadBody = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.AVATAR,
                    contentType = MediaType.IMAGE_PNG_VALUE,
                    fileSize = length,
                    fileName = file.name
                )
            )
            .exchange().expectStatus().isOk
            .expectBody<PresignUploadVO>()
            .returnResult()
            .responseBody
            ?: fail("上传头像测试返回body为空")

        //预签名上传对象存储
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.valueOf(mimeType))
            .contentLength(length)
            .body(image)
            .exchange()
            .expectStatus().isOk

        //完成上传
        httpClient.post().uri("/uploads/complete-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(CompleteUploadDTO(presignUploadBody.id))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .expectBody()
            .jsonPath("$.errorCode").isEqualTo("message.object_storage.not_image_file")
    }

    private var articleId: Long? = null

    @BeforeAll
    fun `创建文章给对象存储绑定`() {
        val categoriesBody = httpClient.post().uri("/admin/categories")
            .header("Authorization", "Bearer $adminToken")
            .body(
                CreateCategoriesDTO(
                    name = "test-${UUID.randomUUID().toString().slice(0..6)}",
                    slug = "test-${UUID.randomUUID().toString().slice(0..6)}"
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody<CategoriesVO>()
            .returnResult()
            .responseBody
            ?: fail("创建类别返回body为空")


        val articleBody = httpClient.post().uri("/admin/articles")
            .header("Authorization", "Bearer $adminToken")
            .body(
                CreateArticleDTO(
                    title = "object storage test",
                    slug = "this-is-object-storage-test",
                    content = "this is object storage test content",
                    categoryId = categoriesBody.id,
                    tagIds = listOf(),
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody<ArticleVO>()
            .returnResult()
            .responseBody
            ?: fail("创建文章返回body为空")

        articleId = articleBody.id
    }

    @Test
    fun `文章正文图片上传测试`() {
        articleId ?: fail("文章id为空 无法测试")

        val image = mediaResource("images/article-image-valid.jpg")
        val file = image.file
        val length = image.file.length()
        val mimeType = "image/jpeg"

        val presignUploadBody = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.ARTICLE_IMAGE,
                    contentType = mimeType,
                    fileSize = length,
                    fileName = file.name,
                    articleId = articleId
                )
            ).exchange().expectStatus().isOk
            .expectBody<PresignUploadVO>()
            .returnResult()
            .responseBody
            ?: fail("返回body为空")

        //预签名上传对象存储
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.valueOf(mimeType))
            .contentLength(length)
            .body(image)
            .exchange()
            .expectStatus().isOk

        //完成上传
        val completeUploadBody = httpClient.post().uri("/uploads/complete-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(CompleteUploadDTO(presignUploadBody.id))
            .exchange()
            .expectStatus().isOk.expectBody<CompleteUploadVO>().returnResult()
            .responseBody
            ?: fail("完成上传返回body为空")

        //判断图片是否可访问
        httpClient.get().uri(completeUploadBody.url).exchange().expectStatus().isOk

        //判断对象与文章是否绑定
        if (!articleObjectStorageRepository.existsByObjectStorage_Id_AndArticle_Id(
                completeUploadBody.id,
                articleId!!
            )
        ) fail("对象与文章实际未绑定")
    }

    @Test
    fun `文章封面图片上传测试`() {
        articleId ?: fail("文章id为空 无法测试")

        val image = mediaResource("images/article-featured-image-valid.jpg")
        val file = image.file
        val length = image.file.length()
        val mimeType = "image/jpeg"

        val presignUploadBody = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.ARTICLE_FEATURED_IMAGE,
                    contentType = mimeType,
                    fileSize = length,
                    fileName = file.name,
                    articleId = articleId
                )
            ).exchange().expectStatus().isOk
            .expectBody<PresignUploadVO>()
            .returnResult()
            .responseBody
            ?: fail("返回body为空")

        //预签名上传对象存储
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.valueOf(mimeType))
            .contentLength(length)
            .body(image)
            .exchange()
            .expectStatus().isOk

        //完成上传
        val completeUploadBody = httpClient.post().uri("/uploads/complete-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(CompleteUploadDTO(presignUploadBody.id))
            .exchange()
            .expectStatus().isOk
            .expectBody<CompleteUploadVO>()
            .returnResult()
            .responseBody
            ?: fail("完成上传返回body为空")

        //判断文章封面是否修改成功
        val article = articleService.getArticleById(articleId!!)
        if (article.featuredImage != completeUploadBody.url) fail("文章封面未修改")

        //封面是否可访问
        httpClient.get().uri(completeUploadBody.url).exchange().expectStatus().isOk

        //判断对象与文章是否绑定
        if (!articleObjectStorageRepository.existsByObjectStorage_Id_AndArticle_Id(
                completeUploadBody.id,
                articleId!!
            )
        ) fail("对象与文章实际未绑定")
    }

    @Test
    fun `上传文章视频测试`() {
        articleId ?: fail("文章id为空 无法测试")

        val video = mediaResource("videos/video-valid.mp4")
        val file = video.file
        val length = video.file.length()
        val mimeType = "video/mp4"

        val presignUploadBody = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.ARTICLE_VIDEO,
                    contentType = mimeType,
                    fileSize = length,
                    fileName = file.name,
                    articleId = articleId
                )
            )
            .exchange().expectStatus().isOk
            .expectBody<PresignUploadVO>().returnResult()
            .responseBody
            ?: fail("返回body为空")

        //预签名上传对象存储
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.valueOf(mimeType))
            .contentLength(length)
            .body(video)
            .exchange()
            .expectStatus().isOk

        //完成上传
        val completeUploadBody = httpClient.post().uri("/uploads/complete-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(CompleteUploadDTO(presignUploadBody.id))
            .exchange()
            .expectStatus().isOk
            .expectBody<CompleteUploadVO>().returnResult()
            .responseBody
            ?: fail("完成上传返回body为空")

        //视频是否可访问
        httpClient.get().uri(completeUploadBody.url).exchange().expectStatus().isOk

        //判断对象与文章是否绑定
        if (!articleObjectStorageRepository.existsByObjectStorage_Id_AndArticle_Id(
                completeUploadBody.id,
                articleId!!
            )
        ) fail("对象与文章实际未绑定")
    }

    @Test
    fun `上传文章附件测试`() {
        articleId ?: fail("文章id为空 无法测试")

        val attachment = attachmentResource("attachment-valid.zip")
        val file = attachment.file
        val length = attachment.file.length()
        val mimeType = "application/zip"

        val presignUploadBody = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.ARTICLE_ATTACHMENT,
                    contentType = mimeType,
                    fileSize = length,
                    fileName = file.name,
                    articleId = articleId
                )
            )
            .exchange().expectStatus().isOk
            .expectBody<PresignUploadVO>().returnResult()
            .responseBody
            ?: fail("返回body为空")

        //预签名上传对象存储
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.valueOf(mimeType))
            .contentLength(length)
            .body(attachment)
            .exchange()
            .expectStatus().isOk

        //完成上传
        val completeUploadBody = httpClient.post().uri("/uploads/complete-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(CompleteUploadDTO(presignUploadBody.id))
            .exchange()
            .expectStatus().isOk
            .expectBody<CompleteUploadVO>().returnResult()
            .responseBody
            ?: fail("完成上传返回body为空")

        //附件是否可访问
        httpClient.get().uri(completeUploadBody.url).exchange().expectStatus().isOk

        //判断对象与文章是否绑定
        if (!articleObjectStorageRepository.existsByObjectStorage_Id_AndArticle_Id(
                completeUploadBody.id,
                articleId!!
            )
        ) fail("对象与文章实际未绑定")
    }

    @Test
    fun `上传文章伪 PDF 附件测试`() {
        articleId ?: fail("文章id为空 无法测试")

        val attachment = attachmentResource("attachment-fake.pdf")
        val file = attachment.file
        val length = attachment.file.length()
        val mimeType = "application/pdf"

        val presignUploadBody = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.ARTICLE_ATTACHMENT,
                    contentType = mimeType,
                    fileSize = length,
                    fileName = file.name,
                    articleId = articleId
                )
            )
            .exchange().expectStatus().isOk
            .expectBody<PresignUploadVO>().returnResult()
            .responseBody
            ?: fail("返回body为空")

        //预签名上传对象存储
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.valueOf(mimeType))
            .contentLength(length)
            .body(attachment)
            .exchange()
            .expectStatus().isOk

        //完成上传
        httpClient.post().uri("/uploads/complete-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(CompleteUploadDTO(presignUploadBody.id))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    }

    @Test
    fun `上传文章多点文件名附件测试`() {
        articleId ?: fail("文章id为空 无法测试")

        val attachment = attachmentResource("attachment.multiple.dots.test.txt")
        val file = attachment.file
        val length = attachment.file.length()
        val mimeType = "text/plain"

        val presignUploadBody = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.ARTICLE_ATTACHMENT,
                    contentType = mimeType,
                    fileSize = length,
                    fileName = file.name,
                    articleId = articleId
                )
            )
            .exchange().expectStatus().isOk
            .expectBody<PresignUploadVO>().returnResult()
            .responseBody
            ?: fail("返回body为空")

        //预签名上传对象存储
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.valueOf(mimeType))
            .contentLength(length)
            .body(attachment)
            .exchange()
            .expectStatus().isOk

        //完成上传
        val completeUploadBody = httpClient.post().uri("/uploads/complete-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(CompleteUploadDTO(presignUploadBody.id))
            .exchange()
            .expectStatus().isOk
            .expectBody<CompleteUploadVO>().returnResult()
            .responseBody
            ?: fail("完成上传返回body为空")

        //附件是否可访问
        httpClient.get().uri(completeUploadBody.url).exchange().expectStatus().isOk

        //判断对象与文章是否绑定
        if (!articleObjectStorageRepository.existsByObjectStorage_Id_AndArticle_Id(
                completeUploadBody.id,
                articleId!!
            )
        ) fail("对象与文章实际未绑定")
    }

    @Test
    fun `上传文章中文及空格文件名附件测试`() {
        articleId ?: fail("文章id为空 无法测试")

        val attachment = attachmentResource("附件 中文 空格.txt")
        val file = attachment.file
        val length = attachment.file.length()
        val mimeType = "text/plain"

        val presignUploadBody = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.ARTICLE_ATTACHMENT,
                    contentType = mimeType,
                    fileSize = length,
                    fileName = file.name,
                    articleId = articleId
                )
            )
            .exchange().expectStatus().isOk
            .expectBody<PresignUploadVO>().returnResult()
            .responseBody
            ?: fail("返回body为空")

        //预签名上传对象存储
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.valueOf(mimeType))
            .contentLength(length)
            .body(attachment)
            .exchange()
            .expectStatus().isOk

        //完成上传
        val completeUploadBody = httpClient.post().uri("/uploads/complete-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(CompleteUploadDTO(presignUploadBody.id))
            .exchange()
            .expectStatus().isOk
            .expectBody<CompleteUploadVO>().returnResult()
            .responseBody
            ?: fail("完成上传返回body为空")

        //附件是否可访问
        httpClient.get().uri(completeUploadBody.url).exchange().expectStatus().isOk

        //判断对象与文章是否绑定
        if (!articleObjectStorageRepository.existsByObjectStorage_Id_AndArticle_Id(
                completeUploadBody.id,
                articleId!!
            )
        ) fail("对象与文章实际未绑定")
    }
}