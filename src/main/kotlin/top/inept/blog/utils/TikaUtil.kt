package top.inept.blog.utils

import org.apache.tika.metadata.Metadata
import org.apache.tika.metadata.TIFF

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
}