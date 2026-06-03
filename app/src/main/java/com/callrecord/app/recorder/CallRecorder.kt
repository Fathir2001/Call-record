package com.callrecord.app.recorder

import android.media.MediaRecorder
import java.io.File

class CallRecorder {
    private val wavRecorder = WavRecorder()

    fun start(outputFile: File): RecorderStartResult {
        outputFile.parentFile?.mkdirs()
        val sources = listOf(
            MediaRecorder.AudioSource.VOICE_CALL,
            MediaRecorder.AudioSource.VOICE_COMMUNICATION,
            MediaRecorder.AudioSource.MIC
        )

        var lastFailure: RecorderStartResult.Failure? = null
        for (source in sources) {
            if (outputFile.exists()) {
                outputFile.delete()
            }
            val result = wavRecorder.start(
                RecorderConfig(
                    outputFile = outputFile,
                    audioSource = source
                )
            )
            when (result) {
                is RecorderStartResult.Success -> return result
                is RecorderStartResult.Failure -> lastFailure = result
            }
        }
        return lastFailure ?: RecorderStartResult.Failure("All audio sources failed")
    }

    fun stop(): RecorderStopResult? {
        return wavRecorder.stop()
    }
}
