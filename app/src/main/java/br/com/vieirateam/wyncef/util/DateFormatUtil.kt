package br.com.vieirateam.wyncef.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatUtil {

    fun format(date: Date?, format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(date as Date)
    }
}