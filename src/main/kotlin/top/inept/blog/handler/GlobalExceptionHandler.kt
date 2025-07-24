package top.inept.blog.handler

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.slf4j.LoggerFactory
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import top.inept.blog.base.ApiResponse
import top.inept.blog.base.ValidationError
import top.inept.blog.extensions.get

@RestControllerAdvice
class GlobalExceptionHandler(
    private val messages: MessageSourceAccessor
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler
    fun exceptionHandler(ex: Exception): ApiResponse<String> {
        logger.error(ex.message, ex)

        return ApiResponse.error(ex.message ?: messages["message.common.unknown_error"])
    }

    @ExceptionHandler
    fun exceptionHandler(ex: HttpMessageNotReadableException): ApiResponse<String> {
        logger.error(ex.message, ex)

        //json解析时缺少字段报错
        if (ex.cause is MissingKotlinParameterException) {
            return ApiResponse.error(messages["message.common.missing_json_field", ex.message ?: "Null"])
        }

        return ApiResponse.error(ex.message ?: messages["message.common.unknown_error"])
    }

    /**
     * Validated的验证错误
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun exceptionHandler(ex: MethodArgumentNotValidException): ApiResponse<List<ValidationError>> {
        val errors = ex.bindingResult.fieldErrors.map { fe ->
            ValidationError(
                field = fe.field,
                message = messages[fe.defaultMessage ?: "message.common.illegal_parameters"],
            )
        }

        return ApiResponse.error(messages["message.common.illegal_parameters"], errors)
    }
}