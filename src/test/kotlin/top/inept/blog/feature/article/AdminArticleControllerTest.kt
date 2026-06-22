package top.inept.blog.feature.article

import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.client.expectBody
import top.inept.blog.IntegrationTestBase
import top.inept.blog.base.PageResponse
import top.inept.blog.feature.article.model.dto.CreateArticleDTO
import top.inept.blog.feature.article.model.dto.UpdateArticleDTO
import top.inept.blog.feature.article.model.dto.UpdateArticleStatusDTO
import top.inept.blog.feature.article.model.entity.enums.ArticleStatus
import top.inept.blog.feature.article.model.vo.ArticleVO
import top.inept.blog.feature.article.model.vo.HomeArticleVO
import top.inept.blog.feature.article.model.vo.LikeArticleVO
import top.inept.blog.feature.categories.model.dto.CreateCategoriesDTO
import top.inept.blog.feature.categories.model.vo.CategoriesVO
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.junit5.JUnit5Asserter.fail

class AdminArticleControllerTest : IntegrationTestBase() {
    private fun randomString(prefix: String) = "$prefix-${UUID.randomUUID().toString().replace("-", "").slice(0..9)}"

    private fun createCategory(): CategoriesVO {
        val str = randomString("c")

        return httpClient.post().uri("/admin/categories")
            .header("Authorization", "Bearer $adminToken")
            .body(CreateCategoriesDTO(str, str))
            .exchange()
            .expectStatus().isOk
            .expectBody<CategoriesVO>()
            .returnResult().responseBody
            ?: fail("返回响应体为空")
    }

    private fun createArticle(status: ArticleStatus = ArticleStatus.DRAFT): ArticleVO {
        val str = randomString("article")
        val category = createCategory()

        return httpClient.post().uri("/admin/articles")
            .header("Authorization", "Bearer $adminToken")
            .body(
                CreateArticleDTO(
                    title = str,
                    slug = str,
                    content = "content-$str",
                    categoryId = category.id,
                    tagIds = emptyList(),
                    status = status,
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody<ArticleVO>()
            .returnResult().responseBody
            ?: fail("返回响应体为空")
    }

    @Test
    fun `创建文章测试`() {
        val articleVO = createArticle()

        assertTrue(articleVO.id > 0)
        assertEquals(ArticleStatus.DRAFT, articleVO.articleStatus)
    }

    @Test
    fun `获取文章列表测试`() {
        val articleVO = createArticle()

        val articlePage = httpClient.get().uri("/admin/articles?keyword=${articleVO.slug}")
            .header("Authorization", "Bearer $adminToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<PageResponse<HomeArticleVO>>()
            .returnResult().responseBody
            ?: fail("返回响应体为空")

        assertTrue(articlePage.content.any { it.id == articleVO.id })
    }

    @Test
    fun `根据id获取文章测试`() {
        val articleVO = createArticle()

        val result = httpClient.get().uri("/admin/articles/${articleVO.id}")
            .header("Authorization", "Bearer $adminToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<ArticleVO>()
            .returnResult().responseBody
            ?: fail("返回响应体为空")

        assertEquals(articleVO.id, result.id)
        assertEquals(articleVO.slug, result.slug)
    }

    @Test
    fun `更新文章测试`() {
        val articleVO = createArticle()
        val category = createCategory()
        val updStr = randomString("article")

        val result = httpClient.patch().uri("/admin/articles/${articleVO.id}")
            .header("Authorization", "Bearer $adminToken")
            .body(
                UpdateArticleDTO(
                    title = updStr,
                    slug = updStr,
                    content = "content-$updStr",
                    categoryId = category.id,
                    tagIds = emptyList(),
                    articleStatus = ArticleStatus.PUBLISHED,
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody<ArticleVO>()
            .returnResult().responseBody
            ?: fail("返回响应体为空")

        assertEquals(updStr, result.title)
        assertEquals(updStr, result.slug)
        assertEquals("content-$updStr", result.content)
        assertEquals(category.id, result.category.id)
        assertEquals(ArticleStatus.PUBLISHED, result.articleStatus)
    }

    @Test
    fun `批量更新文章状态测试`() {
        val articleVO = createArticle()

        val isUpdate = httpClient.put().uri("/admin/articles/status")
            .header("Authorization", "Bearer $adminToken")
            .body(UpdateArticleStatusDTO(listOf(articleVO.id), ArticleStatus.ARCHIVED))
            .exchange()
            .expectStatus().isOk
            .expectBody<Boolean>()
            .returnResult().responseBody
            ?: fail("返回响应体为空")

        if (!isUpdate) fail("文章状态更新失败")

        assertEquals(ArticleStatus.ARCHIVED, articleRepository.findByIdOrNull(articleVO.id)?.articleStatus)
    }

    @Test
    fun `删除文章测试`() {
        val articleVO = createArticle()

        val isDelete = httpClient.delete().uri("/admin/articles/${articleVO.id}")
            .header("Authorization", "Bearer $adminToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<Boolean>()
            .returnResult().responseBody
            ?: fail("返回响应体为空")

        if (!isDelete) fail("文章删除失败")

        if (articleRepository.findByIdOrNull(articleVO.id) != null) fail("文章删除失败")
    }

    @Test
    fun `公开文章获取测试`() {
        //测试未公开的文章
        val draftArticleVO = createArticle(ArticleStatus.DRAFT)

        httpClient.get().uri("/public/articles/${draftArticleVO.id}")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
            .expectBody()
            .jsonPath("$.errorCode").isEqualTo("message.articles.id_not_found_or_not_public")

        //测试公开的文章
        val publishedArticleVO = createArticle(ArticleStatus.PUBLISHED)

        httpClient.get().uri("/public/articles/${publishedArticleVO.id}")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `点赞文章测试`() {
        val article = createArticle()

        val likeArticleVO = (httpClient.post().uri("/user/articles/${article.id}/like")
            .header("Authorization", "Bearer $adminToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<LikeArticleVO>()
            .returnResult().responseBody
            ?: fail("返回响应体为空"))

        assertEquals(
            article.likeCount + 1,
            likeArticleVO.likeCount,
            "点赞数应在原基础上加 1",
        )

        //取消点赞
        val unlikeArticleVO = (httpClient.delete().uri("/user/articles/${article.id}/like")
            .header("Authorization", "Bearer $adminToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<LikeArticleVO>()
            .returnResult().responseBody
            ?: fail("返回响应体为空"))

        if (unlikeArticleVO.likeCount != article.likeCount) {
            fail("点赞与预期不符合")
        }
    }

    @Test
    fun `未点赞进行点赞错误测试`() {
        val article = createArticle()

        //取消点赞
        httpClient.delete().uri("/user/articles/${article.id}/like")
            .header("Authorization", "Bearer $adminToken")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.errorCode").isEqualTo("message.articles.like_not_found")
    }
}
