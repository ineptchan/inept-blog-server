package top.inept.blog.utils

import java.time.Duration
import java.util.Date

object DateUtil {
    fun addHoursToDate(date: Date, hours: Long): Date {
        val instant = date.toInstant()
        val newInstant = instant.plus(Duration.ofHours(hours))
        return Date.from(newInstant)
    }
}