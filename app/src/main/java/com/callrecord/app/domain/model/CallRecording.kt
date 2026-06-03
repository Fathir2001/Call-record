package com.callrecord.app.domain.model

data class CallRecording(
    val id: Long = 0,
    val phoneNumber: String,
    val contactName: String,
    val callType: CallType,
    val startedAt: Long,
    val durationMs: Long,
    val filePath: String,
    val audioSource: Int
)
