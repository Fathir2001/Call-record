package com.callrecord.app.utils

import com.callrecord.app.domain.model.CallType

class FileNameFormatter {
    private val invalidChars = Regex("[^a-zA-Z0-9._-]")

    fun buildFileName(callType: CallType, number: String, contactName: String, timestamp: Long): String {
        val safeType = sanitize(callType.name)
        val safeNumber = sanitize(number.ifBlank { "Unknown" })
        val safeContact = sanitize(contactName.ifBlank { "Unknown" })
        val timeStamp = DateTimeFormatter.formatForFile(timestamp)
        return "${safeType}_${safeNumber}_${safeContact}_${timeStamp}.wav"
    }

    private fun sanitize(value: String): String {
        return value.replace(invalidChars, "_").take(48)
    }
}
