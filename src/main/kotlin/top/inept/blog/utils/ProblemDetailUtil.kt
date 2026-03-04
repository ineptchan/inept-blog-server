package top.inept.blog.utils

import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import java.time.OffsetDateTime

object ProblemDetailUtil {
    fun buildProblemDetail(
        status: HttpStatusCode,
        title: String,
        detail: String,
        props: Map<String, Any?> = emptyMap(),
    ): ProblemDetail = ProblemDetail.forStatusAndDetail(
        status,
        detail,
    ).apply {
        this.title = title
        setProperty("timestamp", OffsetDateTime.now().toString())
        props.forEach { (k, v) -> setProperty(k, v) }
    }
}