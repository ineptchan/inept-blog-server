package top.inept.blog.handler

import org.springframework.context.support.MessageSourceAccessor
import org.springframework.http.*
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import top.inept.blog.exception.BusinessException
import top.inept.blog.extensions.get
import top.inept.blog.extensions.log

@RestControllerAdvice
class GlobalExceptionHandler(
    private val messages: MessageSourceAccessor,
) : ResponseEntityExceptionHandler() {
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ProblemDetail {
        log.error("服务器内部错误", ex)

        val problemDetail = buildProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            title = messages["message.common.server_unknown_error"],
            messages["message.common.server_unknown_error.detail"]
        )

        return problemDetail
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ProblemDetail {
        log.error("业务错误", ex)

        val problemDetail = buildProblemDetail(
            ex.errorCode.httpStatus,
            messages[ex.errorCode.messageKey],
            messages["${ex.errorCode.messageKey}.detail"]
        ).apply {
            // 放入业务数据 (balance, accounts 等)
            ex.extensions.forEach { (key, value) ->
                setProperty(key, value)
            }
        }

        return problemDetail
    }

    /**
     * 没有权限的验证错误
     */
    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(ex: AuthorizationDeniedException): ProblemDetail {
        log.error("权限错误", ex)

        val problemDetail = buildProblemDetail(
            HttpStatus.FORBIDDEN,
            messages["message.common.authorization_denied"],
            messages["message.common.authorization_denied.detail"]
        )

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
        log.error("validation 校验异常", ex)

        val errors = ex.bindingResult.fieldErrors.associate { fieldError ->
            fieldError.field to messages[fieldError.defaultMessage ?: "message.common.unknown_error"]
        }

        val problemDetail = buildProblemDetail(
            HttpStatus.BAD_REQUEST,
            messages["message.common.validation_failed_title"],
            messages["message.common.parameter_validation"],
        ).apply {
            setProperty("errors", errors)
        }

        return createResponseEntity(problemDetail, headers, status, request)
    }

    /**
     * 方法参数类型不匹配异常
     *
     * ?page=abc 解析成 Int 失败
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(ex: MethodArgumentTypeMismatchException): ProblemDetail {
        log.error("path 类型不匹配", ex)

        return buildProblemDetail(
            HttpStatus.BAD_REQUEST,
            messages["message.common.path_type_mismatch"],
            messages[
                "message.common.path_type_mismatch.detail",
                ex.name,
                ex.requiredType ?: messages["message.common.unknown_type"]
            ],
        )
    }

    /**
     * 请求参数缺失
     *
     * @param ex
     * @return
     */
    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        log.error("请求参数缺失", ex)

        val problemDetail = buildProblemDetail(
            HttpStatus.BAD_REQUEST,
            messages["message.common.missing_request_parameters"],
            messages["message.common.missing_request_parameters.detail", ex.parameterName],
        )

        return createResponseEntity(problemDetail, headers, HttpStatus.BAD_REQUEST, request)
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
        val problemDetail = buildProblemDetail(
            status,
            messages["message.common.bad_request_title"],
            messages["message.common.request_body_missing"]
        ).apply {
            setProperty("error", messages["message.common.json_format_error"])
        }

        return createResponseEntity(problemDetail, headers, status, request)
    }

    private fun buildProblemDetail(
        status: HttpStatusCode,
        title: String,
        detail: String,
        props: Map<String, Any?> = emptyMap(),
    ) = ProblemDetail.forStatusAndDetail(
        status,
        detail,
    ).apply {
        this.title = title
        setProperty("timestamp", java.time.OffsetDateTime.now().toString())
        props.forEach { (k, v) -> setProperty(k, v) }
    }
}