package top.inept.blog.utils

import org.apache.tika.io.TikaInputStream
import org.apache.tika.metadata.Metadata
import org.apache.tika.metadata.TIFF
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.springframework.web.multipart.MultipartFile
import org.xml.sax.helpers.DefaultHandler
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.ObjectStorageErrorCode

object TikaUtil {
    fun parseImageLength(md: Metadata): Long {
        val length = md.get(TIFF.IMAGE_LENGTH).toLongOrNull()
        if (length != null && length != 0L) {
            return length
        }

        //700 pixels
        val split = md["Image Height"].split(" ")
        if (split.size == 2) {
            val height = split[0].toLongOrNull()
            if (height != null && height != 0L) {
                return height
            }
        }

        return -1
    }

    fun parseImageWidth(md: Metadata): Long {
        val width = md.get(TIFF.IMAGE_WIDTH).toLongOrNull()
        if (width != null && width != 0L) {
            return width
        }

        //700 pixels
        val split = md["Image Width"].split(" ")
        if (split.size == 2) {
            val width = split[0].toLongOrNull()
            if (width != null && width != 0L) {
                return width
            }
        }

        return -1
    }

    fun parserImage(file: MultipartFile): ParserImageResult {
        //使用tika解析图片
        val parser = AutoDetectParser()
        val metadata = Metadata()
        TikaInputStream.get(file.bytes).use {
            parser.parse(it, DefaultHandler(), metadata, ParseContext())
        }

        //解析文件格式
        val originalMime = metadata.get("Content-Type")
        //不是图片抛出错误
        if (!originalMime.startsWith("image/")) {
            throw BusinessException(ObjectStorageErrorCode.NOT_IMAGE_FILE, originalMime)
        }

        //解析分辨率
        val imageLength = parseImageLength(metadata)
        val imageWidth = parseImageWidth(metadata)

        return ParserImageResult(
            mime = originalMime,
            width = imageWidth,
            height = imageLength,
        )
    }

    data class ParserImageResult(
        val mime: String,
        val width: Long,
        val height: Long,
    )
}