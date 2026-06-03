package com.callrecord.app.recorder

import android.media.AudioFormat
import java.io.File

data class RecorderConfig(
    val outputFile: File,
    val audioSource: Int,
    val sampleRate: Int = 16000,
    val channelConfig: Int = AudioFormat.CHANNEL_IN_MONO,
    val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
)
