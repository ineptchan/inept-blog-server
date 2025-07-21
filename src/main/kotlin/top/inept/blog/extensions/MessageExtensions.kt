package top.inept.blog.extensions

import org.springframework.context.NoSuchMessageException
import org.springframework.context.support.MessageSourceAccessor

// 单参数，无 args
operator fun MessageSourceAccessor.get(code: String): String =
    try {
        this.getMessage(code)
    } catch (ex: NoSuchMessageException) {
        code
    }

// 多参数，可传 args
operator fun MessageSourceAccessor.get(code: String, vararg args: Any): String =
    try {
        this.getMessage(code, args)
    } catch (ex: NoSuchMessageException) {
        code
    }