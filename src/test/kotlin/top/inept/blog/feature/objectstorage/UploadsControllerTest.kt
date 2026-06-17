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
import top.inept.blog.feature.objectstorage.model.vo.PresignUploadVO
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

        val presignUploadResult = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.AVATAR,
                    contentType = MediaType.IMAGE_PNG_VALUE,
                    fileSize = length,
                    fileName = file.name
                )
            )
            .exchange().expectStatus().isOk.expectBody<PresignUploadVO>().returnResult()

        val presignUploadBody = presignUploadResult.responseBody
            ?: fail("上传头像测试返回body为空")

        //对象存储的预签名
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.IMAGE_PNG)
            .contentLength(length)
            .body(image)
            .exchange()
            .expectStatus().isOk
            .returnResult()

        //完成上传
        val completeUploadResult = httpClient.post().uri("/uploads/complete-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(CompleteUploadDTO(presignUploadBody.id))
            .exchange()
            .expectStatus().isOk
            .expectBody<String>()
            .returnResult()

        val completeUploadBody = completeUploadResult.responseBody
            ?: fail("完成上传头像测试返回body为空")

        httpClient.get().uri(completeUploadBody).exchange().expectStatus().isOk
    }

    @Test
    @Order(2)
    fun `超出尺寸头像图片上传测试`() {
        val image = mediaResource("images/avatar-too-large.png")
        val file = image.file
        val length = file.length()

        val presignUploadResult = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.AVATAR,
                    contentType = MediaType.IMAGE_PNG_VALUE,
                    fileSize = length,
                    fileName = file.name
                )
            )
            .exchange().expectStatus().isOk.expectBody<PresignUploadVO>().returnResult()

        val presignUploadBody = presignUploadResult.responseBody
            ?: fail("上传头像测试返回body为空")

        //对象存储的预签名
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.IMAGE_PNG)
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

        val presignUploadResult = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.AVATAR,
                    contentType = MediaType.IMAGE_PNG_VALUE,
                    fileSize = length,
                    fileName = file.name
                )
            )
            .exchange().expectStatus().isOk.expectBody<PresignUploadVO>().returnResult()

        val presignUploadBody = presignUploadResult.responseBody
            ?: fail("上传头像测试返回body为空")

        //对象存储的预签名
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.IMAGE_PNG)
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

        httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.AVATAR,
                    contentType = MediaType.IMAGE_PNG_VALUE,
                    fileSize = length,
                    fileName = file.name
                )
            )
            .exchange().expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT)
            .expectBody()
    }

    @Test
    @Order(5)
    fun `请求参数伪造空头像图片上传测试`() {
        val image = mediaResource("images/empty.png")
        val file = image.file
        val length = (3 * 1024 * 1024).toLong()

        val presignUploadResult = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.AVATAR,
                    contentType = MediaType.IMAGE_PNG_VALUE,
                    fileSize = length,
                    fileName = file.name
                )
            )
            .exchange().expectStatus().isOk.expectBody<PresignUploadVO>().returnResult()

        val presignUploadBody = presignUploadResult.responseBody
            ?: fail("上传头像测试返回body为空")

        //对象存储的预签名
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.IMAGE_PNG)
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

        val presignUploadResult = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.AVATAR,
                    contentType = MediaType.IMAGE_PNG_VALUE,
                    fileSize = length,
                    fileName = file.name
                )
            )
            .exchange().expectStatus().isOk.expectBody<PresignUploadVO>().returnResult()

        val presignUploadBody = presignUploadResult.responseBody
            ?: fail("上传头像测试返回body为空")

        //对象存储的预签名
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.IMAGE_PNG)
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
            .expectStatus().isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .expectBody()
            .jsonPath("$.errorCode").isEqualTo("message.object_storage.not_image_file")
    }

    private var articleId: Long? = null

    @BeforeAll
    fun `创建文章给对象存储绑定`() {
        val categoriesResult = httpClient.post().uri("/admin/categories")
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

        val categoriesBody = categoriesResult.responseBody
            ?: fail("创建类别返回body为空")


        val articleResult = httpClient.post().uri("/admin/articles")
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

        val articleBody = articleResult.responseBody
            ?: fail("创建文章返回body为空")

        articleId = articleBody.id
    }

    @Test
    fun `文章正文图片上传测试`() {
        if (articleId == null) {
            fail("文章id为空 无法测试")
        }

        val image = mediaResource("images/article-image-valid.jpg")
        val file = image.file
        val length = image.file.length()

        val presignUploadResult = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.ARTICLE_IMAGE,
                    contentType = MediaType.IMAGE_JPEG_VALUE,
                    fileSize = length,
                    fileName = file.name,
                    articleId = articleId
                )
            ).exchange().expectStatus().isOk.expectBody<PresignUploadVO>().returnResult()

        val presignUploadBody = presignUploadResult.responseBody
            ?: fail("返回body为空")

        //对象存储的预签名
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.IMAGE_JPEG)
            .contentLength(length)
            .body(image)
            .exchange()
            .expectStatus().isOk
            .returnResult()

        //完成上传
        val completeUploadResult = httpClient.post().uri("/uploads/complete-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(CompleteUploadDTO(presignUploadBody.id))
            .exchange()
            .expectStatus().isOk.expectBody<String>().returnResult()

        val completeUploadBody = completeUploadResult.responseBody
            ?: fail("完成上传返回body为空")

        httpClient.get().uri(completeUploadBody).exchange().expectStatus().isOk
    }

    @Test
    fun `文章封面图片上传测试`() {
        if (articleId == null) {
            fail("文章id为空 无法测试")
        }

        val image = mediaResource("images/article-featured-image-valid.jpg")
        val file = image.file
        val length = image.file.length()

        val presignUploadResult = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.ARTICLE_FEATURED_IMAGE,
                    contentType = MediaType.IMAGE_JPEG_VALUE,
                    fileSize = length,
                    fileName = file.name,
                    articleId = articleId
                )
            ).exchange().expectStatus().isOk.expectBody<PresignUploadVO>().returnResult()

        val presignUploadBody = presignUploadResult.responseBody
            ?: fail("返回body为空")

        //对象存储的预签名
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.IMAGE_JPEG)
            .contentLength(length)
            .body(image)
            .exchange()
            .expectStatus().isOk
            .returnResult()

        //完成上传
        val completeUploadResult = httpClient.post().uri("/uploads/complete-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(CompleteUploadDTO(presignUploadBody.id))
            .exchange()
            .expectStatus().isOk.expectBody<String>().returnResult()

        val completeUploadBody = completeUploadResult.responseBody
            ?: fail("完成上传返回body为空")

        //判断文章封面是否修改成功
        val article = articleService.getArticleById(articleId!!)
        if (article.featuredImage != completeUploadBody) {
            fail("文章封面未修改")
        }

        //封面是否可访问
        httpClient.get().uri(completeUploadBody).exchange().expectStatus().isOk
    }

    @Test
    fun `上传文章视频测试`() {
        if (articleId == null) {
            fail("文章id为空 无法测试")
        }

        val video = mediaResource("videos/video-valid.mp4")
        val file = video.file
        val length = video.file.length()

        val presignUploadResult = httpClient.post().uri("/uploads/presign-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(
                PresignUploadDTO(
                    purpose = Purpose.ARTICLE_VIDEO,
                    contentType = "video/mp4",
                    fileSize = length,
                    fileName = file.name,
                    articleId = articleId
                )
            ).exchange().expectStatus().isOk.expectBody<PresignUploadVO>().returnResult()

        val presignUploadBody = presignUploadResult.responseBody
            ?: fail("返回body为空")

        //对象存储的预签名
        httpClient.put().uri(URI.create(presignUploadBody.url))
            .contentType(MediaType.valueOf("video/mp4"))
            .contentLength(length)
            .body(video)
            .exchange()
            .expectStatus().isOk
            .returnResult()

        //完成上传
        val completeUploadResult = httpClient.post().uri("/uploads/complete-upload")
            .header("Authorization", "Bearer $adminToken")
            .body(CompleteUploadDTO(presignUploadBody.id))
            .exchange()
            .expectStatus().isOk.expectBody<String>().returnResult()

        val completeUploadBody = completeUploadResult.responseBody
            ?: fail("完成上传返回body为空")

        //是否可访问
        httpClient.get().uri(completeUploadBody).exchange().expectStatus().isOk
    }
}