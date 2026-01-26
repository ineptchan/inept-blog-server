package top.inept.blog.exception

import org.springframework.http.HttpStatus

interface IErrorCode {
    val code: String                    // 错误码 (如: 40001, "NOTE_001")，用于前端逻辑判断
    val messageKey: String              // i18n 资源文件中的 key
    val httpStatus: HttpStatus          // HTTP 状态码
    val typeUri: String                 //可以给前端当只当 ID 用，或者指向文档中心
        get() = "about:blank"
}

