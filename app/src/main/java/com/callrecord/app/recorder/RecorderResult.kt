package com.callrecord.app.recorder

sealed class RecorderStartResult {
    data class Success(
        val startedAt: Long,
        val audioSource: Int,
        val sampleRate: Int
    ) : RecorderStartResult()

    data class Failure(
        val reason: String,
        val error: Throwable? = null
    ) : RecorderStartResult()
}

data class RecorderStopResult(
    val durationMs: Long,
    val bytesWritten: Long
)
