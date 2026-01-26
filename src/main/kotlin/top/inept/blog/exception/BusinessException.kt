package top.inept.blog.exception

open class BusinessException(
    val errorCode: IErrorCode,
    val args: Array<out Any> = emptyArray(), // 用于 i18n 消息填充 (message formatting)
    val extensions: Map<String, Any> = emptyMap() // 用于 RFC 9457 的额外字段 (balance, accounts等)
) : RuntimeException(errorCode.messageKey){
    constructor(errorCode: IErrorCode, vararg args: Any) : this(
        errorCode = errorCode,
        args = args,
        extensions = emptyMap()
    )
}