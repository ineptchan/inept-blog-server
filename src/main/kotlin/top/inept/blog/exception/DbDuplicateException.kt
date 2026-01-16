package top.inept.blog.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class DbDuplicateException(
    fieldName: String?
) : RuntimeException("字段重复=${fieldName}")