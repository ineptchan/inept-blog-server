package top.inept.blog.utils

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.webp.WebpWriter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ScrimageUtil {
    fun imageToWebp(originalBytes: ByteArray, quality: Int, method: Int): ByteArray {
        ByteArrayInputStream(originalBytes).use {
            return imageToWebp(it, quality, method)
        }
    }

    fun imageToWebp(inputStream: InputStream, quality: Int, method: Int): ByteArray {
        // 加载图片
        val image = ImmutableImage.loader().fromStream(inputStream)
        val writer = WebpWriter()
            .withQ(quality)
            .withM(method)

        val outputStream = ByteArrayOutputStream()
        image.forWriter(writer).write(outputStream) // 将图片写入输出流
        val webpBytes = outputStream.toByteArray()

        return webpBytes
    }

    fun imageToWebpStream(inputStream: InputStream, quality: Int, method: Int): ByteArrayInputStream {
        // 加载图片
        val image = ImmutableImage.loader().fromStream(inputStream)
        val writer = WebpWriter()
            .withQ(quality)
            .withM(method)

        return image.forWriter(writer).stream()
    }
}