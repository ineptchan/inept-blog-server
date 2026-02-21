package top.inept.blog.utils

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.webp.WebpWriter
import org.apache.commons.codec.digest.DigestUtils.sha256Hex
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object ScrimmageUtil {
    fun imageToWebp(originalBytes: ByteArray, quality: Int, method: Int): WebpResult {
        ByteArrayInputStream(originalBytes).use { bis ->
            // 加载图片
            val image = ImmutableImage.loader().fromStream(bis)
            val writer = WebpWriter().withQ(quality).withM(method)

            val webpOutputStream = ByteArrayOutputStream()
            image.forWriter(writer).write(webpOutputStream) // 将图片写入输出流
            val webpBytes = webpOutputStream.toByteArray()

            val webpSha256 = sha256Hex(webpBytes)

            return WebpResult(
                webpBytes = webpBytes,
                webpSha256 = webpSha256,
            )
        }
    }

    class WebpResult(
        val webpBytes: ByteArray,
        val webpSha256: String
    )
}