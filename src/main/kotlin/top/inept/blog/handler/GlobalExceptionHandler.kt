package top.inept.blog.handler

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.slf4j.LoggerFactory
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import top.inept.blog.base.Result
import top.inept.blog.base.ValidationError
import top.inept.blog.extensions.get

@RestControllerAdvice
class GlobalExceptionHandler(
    private val messages: MessageSourceAccessor
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler
    fun exceptionHandler(ex: Exception): Result<String> {

        logger.error(ex.message, ex)
        return Result.error(ex.message ?: messages["common.unknown_error"])
    }

    @ExceptionHandler
    fun exceptionHandler(ex: HttpMessageNotReadableException): Result<String> {
        logger.error(ex.message, ex)

        if (ex.cause is MissingKotlinParameterException) {
            return Result.error(messages["common.missing_json_field"])
        }

        return Result.error(messages["common.unknown_error"])
    }

    /**
     * Validated的验证错误
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun exceptionHandler(ex: MethodArgumentNotValidException): Result<List<ValidationError>> {
        val errors = ex.bindingResult.fieldErrors.map { fe ->
            ValidationError(
                field = fe.field,
                message = fe.defaultMessage ?: messages["common.illegal_parameters"],
            )
        }

        return Result.error(messages["common.illegal_parameters"], errors)
    }
}