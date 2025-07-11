package top.inept.blog.base

import java.io.Serializable

data class Result<T>(
    var code: Int = 0,      // 编码：0 成功，1 和其它数字为失败
    var msg: String? = null, // 错误信息
    var data: T? = null     // 数据
) : Serializable {

    companion object {
        const val SUCCESS = 0
        const val FAILURE = 1

        /**
         * 只返回成功，不带数据
         */
        fun <T> success(data: T): Result<T> = Result(code = SUCCESS, data = data)

        /**
         * 只返回错误信息
         */
        fun <T> error(msg: String): Result<T> = Result(code = FAILURE, msg = msg)

        fun <T> error(msg: String, data: T): Result<T> = Result(code = FAILURE, msg = msg, data = data)
    }
}