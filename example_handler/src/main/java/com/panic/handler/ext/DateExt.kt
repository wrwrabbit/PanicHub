package com.panic.handler.ext

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val DISPLAY_DATE_FORMAT = "HH:mm, dd.MM.yy"

fun Date?.formatedBy(dateFormat: String = DISPLAY_DATE_FORMAT): String? {
    val date = this
    date ?: return null
    val writeFormat = SimpleDateFormat(dateFormat, Locale.getDefault()) // MM в HH:mm
    return writeFormat.format(date)
}