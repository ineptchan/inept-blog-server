package top.inept.blog.handler

import org.springframework.context.support.MessageSourceAccessor
import org.springframework.http.*
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import top.inept.blog.exception.BusinessException
import top.inept.blog.extensions.get


@RestControllerAdvice
class GlobalExceptionHandler(
    private val messages: MessageSourceAccessor,
) : ResponseEntityExceptionHandler() {
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            e.errorCode.httpStatus,
            messages["${e.errorCode.messageKey}.detail"],
        ).apply {
            this.title = messages[e.errorCode.messageKey]
        }

        // 放入业务数据 (balance, accounts 等)
        e.extensions.forEach { (key, value) ->
            problemDetail.setProperty(key, value)
        }

        return problemDetail
    }

    /**
     * 没有权限的验证错误
     */
    @ExceptionHandler
    fun exceptionHandler(e: AuthorizationDeniedException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.FORBIDDEN,
            messages["message.common.authorization_denied.detail"]
        ).apply {
            title = messages["message.common.authorization_denied"]
        }

        return problemDetail
    }

    /**
     * Validation 校验异常
     *
     */
    @Override
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val errors = ex.bindingResult.fieldErrors.associate { fieldError ->
            fieldError.field to messages[fieldError.defaultMessage ?: "message.common.unknown_error"]
        }

        return createValidationResponse(
            status = HttpStatus.BAD_REQUEST,
            detailKey = "message.common.parameter_validation",
            errors = errors,
            headers = HttpHeaders(),
            request = request
        )
    }

    /**
     * 请求体不可读 (JSON 格式错误)
     */
    @Override
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            status,
            messages["message.common.request_body_missing"]
        ).apply {
            title = messages["message.common.bad_request_title"]
            setProperty("error", messages["message.common.json_format_error"])
        }

        return createResponseEntity(problemDetail, headers, status, request)
    }

    /**
     * 统一构建包含 errors 字段的 Validation 响应
     */
    private fun createValidationResponse(
        status: HttpStatusCode,
        detailKey: String,
        errors: Map<String, String>,
        headers: HttpHeaders,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            status,
            messages[detailKey],
        ).apply {
            title = messages["message.common.validation_failed_title"]
            setProperty("errors", errors)
        }

        return createResponseEntity(problemDetail, headers, status, request)
    }
}