package top.inept.blog.handler

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import top.inept.blog.exception.BusinessException
import top.inept.blog.extensions.get
import top.inept.blog.extensions.log

@RestControllerAdvice
class GlobalExceptionHandler(
    private val messages: MessageSourceAccessor,
    private val messageSource: MessageSource
) {
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ProblemDetail {
        val locale = LocaleContextHolder.getLocale()

        val title = try {
            messageSource.getMessage(e.errorCode.messageKey, null, locale)
        } catch (_: Exception) {
            e.errorCode.messageKey
        }

        val detail = try {
            messageSource.getMessage("${e.errorCode.messageKey}.detail", e.args, locale)
        } catch (_: Exception) {
            // 用 title 兜底
            "$title: ${e.args.joinToString()}"
        }


        val problemDetail = ProblemDetail.forStatusAndDetail(
            e.errorCode.httpStatus,
            detail,
        ).apply {
            this.title = title
        }

        // 放入业务数据 (balance, accounts 等)
        e.extensions.forEach { (key, value) ->
            problemDetail.setProperty(key, value)
        }

        return problemDetail
    }

    @ExceptionHandler
    fun exceptionHandler(ex: HttpMessageNotReadableException): ResponseEntity<String> {
        log.error(ex.message, ex)

        val cause = ex.mostSpecificCause
        val message = when (cause) {
//            is MissingKotlinParameterException -> {
//                // 拿到缺失的那个参数名
//                val paramName = cause.parameter.name ?: "UNKNOWN"
//                messages["message.common.missing_json_field", arrayOf(paramName)]
//            }

            is MismatchedInputException -> {
                // 类型不匹配，比如 String 传到 Int
                val path = cause.path.joinToString(".") { it.fieldName ?: "?" }
                messages["message.common.mismatched_json_field_type", arrayOf(path)]
            }

            else -> messages["message.common.unknown_error"]
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message)
    }

    /**
     * 没有权限的验证错误
     */
    @ExceptionHandler
    fun exceptionHandler(ex: AuthorizationDeniedException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null)
    }

    /*    */
    /**
     * Validated的验证错误
     *//*
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun exceptionHandler(ex: MethodArgumentNotValidException): ResponseEntity<List<ValidationError>> {
        val errors = ex.bindingResult.fieldErrors.map { fe ->
            ValidationError(
                field = fe.field,
                message = messages[fe.defaultMessage ?: "message.common.illegal_parameters"],
            )
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(errors)
    }*/


}