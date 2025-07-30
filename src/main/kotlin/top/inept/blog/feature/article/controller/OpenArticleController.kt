package top.inept.blog.feature.article.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.inept.blog.feature.article.service.ArticleService

@Tag(name = "公开文章接口")
@RestController
@RequestMapping("/open/articles")
@Validated
class OpenArticleController(
    private val articleService: ArticleService,
) {

}