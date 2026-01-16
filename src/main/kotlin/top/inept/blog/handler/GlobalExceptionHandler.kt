package top.inept.blog.handler

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import top.inept.blog.base.ValidationError
import top.inept.blog.exception.DbDuplicateException
import top.inept.blog.extensions.get
import top.inept.blog.extensions.log

@RestControllerAdvice
class GlobalExceptionHandler(
    private val messages: MessageSourceAccessor
) {

    @ExceptionHandler
    fun exceptionHandler(ex: Exception): ResponseEntity<String> {
        val status = ex::class.annotations
            .filterIsInstance<ResponseStatus>()
            .firstOrNull()
            ?.value
            ?: HttpStatus.INTERNAL_SERVER_ERROR

        log.error(ex.message, ex)

        return ResponseEntity.status(status).body(ex.message ?: messages["message.common.unknown_error"])
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

    /**
     * Validated的验证错误
     */
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
    }
}