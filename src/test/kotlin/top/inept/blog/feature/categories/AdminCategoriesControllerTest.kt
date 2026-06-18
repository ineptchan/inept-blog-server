package top.inept.blog.feature.categories

import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.web.servlet.client.expectBody
import top.inept.blog.IntegrationTestBase
import top.inept.blog.feature.categories.model.dto.CreateCategoriesDTO
import top.inept.blog.feature.categories.model.dto.UpdateCategoriesDTO
import top.inept.blog.feature.categories.model.vo.CategoriesVO
import java.util.*
import kotlin.test.junit5.JUnit5Asserter.fail

class AdminCategoriesControllerTest : IntegrationTestBase() {
    private fun getNameOrSlugString() = "c-${UUID.randomUUID().toString().replace("-", "").slice(0..9)}"

    @Test
    fun `创建类别测试`() {
        val str = getNameOrSlugString()

        val categoriesVO = httpClient.post().uri("/admin/categories")
            .header("Authorization", "Bearer $adminToken")
            .body(CreateCategoriesDTO(str, str))
            .exchange()
            .expectStatus().isOk
            .expectBody<CategoriesVO>()
            .returnResult().responseBody
            ?: fail("返回body为空")
    }

    @Test
    fun `更新类别测试`() {
        val crt_str = getNameOrSlugString()
        val categoriesVO = httpClient.post().uri("/admin/categories")
            .header("Authorization", "Bearer $adminToken")
            .body(CreateCategoriesDTO(crt_str, crt_str))
            .exchange()
            .expectStatus().isOk
            .expectBody<CategoriesVO>()
            .returnResult().responseBody
            ?: fail("返回body为空")

        val upd_str = getNameOrSlugString()
        httpClient.patch().uri("/admin/categories/${categoriesVO.id}")
            .header("Authorization", "Bearer $adminToken")
            .body(UpdateCategoriesDTO(upd_str, upd_str))
            .exchange()
            .expectStatus().isOk
            .expectBody<CategoriesVO>()
            .returnResult().responseBody
            ?: fail("返回body为空")
    }

    @Test
    fun `删除类别测试`() {
        val crt_str = getNameOrSlugString()

        val categoriesVO = httpClient.post().uri("/admin/categories")
            .header("Authorization", "Bearer $adminToken")
            .body(CreateCategoriesDTO(crt_str, crt_str))
            .exchange()
            .expectStatus().isOk
            .expectBody<CategoriesVO>()
            .returnResult().responseBody
            ?: fail("返回body为空")

        val isDelete = httpClient.delete().uri("/admin/categories/${categoriesVO.id}")
            .header("Authorization", "Bearer $adminToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<Boolean>()
            .returnResult().responseBody
            ?: fail("返回body为空")

        if (!isDelete) fail("类别删除失败")

        if (categoriesRepository.findByIdOrNull(categoriesVO.id) != null) fail("类别删除失败")
    }
}