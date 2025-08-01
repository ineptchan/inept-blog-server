注意open接口的VO
解决Specs字段问题

```kotlin
    @Operation(summary = "获取顶级评论列表")
    @GetMapping("/{articleId}")
    fun getComments(@PathVariable articleId: Long): ApiResponse<*> {
        return ApiResponse.success(null)
    }

    TODO 在公开接口制作
    @Operation(summary = "获取评论回复列表")
    @GetMapping("/{articleId}/replies")
    fun getCommentReplies(@PathVariable articleId: Long): ApiResponse<*> {
        return ApiResponse.success(null)
    }
```