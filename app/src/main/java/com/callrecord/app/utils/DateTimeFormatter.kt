package com.callrecord.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeFormatter {
    private val fileFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    private val readableFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US)

    fun formatForFile(timestamp: Long): String {
        return fileFormat.format(Date(timestamp))
    }

    fun formatReadable(timestamp: Long): String {
        return readableFormat.format(Date(timestamp))
    }
}
