package top.inept.blog.base

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

data class ApiResponse<T>(
    @Schema(description = "openapi.response.code")
    val code: Int = 0,

    @Schema(description = "openapi.response.msg")
    val msg: String? = null,

    @Schema(description = "openapi.response.data")
    val data: T? = null
) : Serializable {

    companion object {
        const val SUCCESS = 0
        const val FAILURE = 1

        /**
         * 只返回成功，不带数据
         */
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(code = SUCCESS, data = data, msg = "success")

        /**
         * 只返回错误信息
         */
        fun <T> error(msg: String): ApiResponse<T> = ApiResponse(code = FAILURE, msg = msg)

        fun <T> error(msg: String, data: T): ApiResponse<T> = ApiResponse(code = FAILURE, msg = msg, data = data)
    }
}