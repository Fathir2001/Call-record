package com.callrecord.app.recorder

import android.media.AudioRecord
import android.os.SystemClock
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.util.concurrent.atomic.AtomicBoolean

class WavRecorder {
    private val isRecording = AtomicBoolean(false)
    private var audioRecord: AudioRecord? = null
    private var outputFile: File? = null
    private var outputStream: FileOutputStream? = null
    private var recordingThread: Thread? = null
    private var totalBytes: Long = 0
    private var startedAt: Long = 0
    private var config: RecorderConfig? = null

    fun start(config: RecorderConfig): RecorderStartResult {
        if (isRecording.get()) {
            return RecorderStartResult.Failure("Recorder already running")
        }

        val minBufferSize = AudioRecord.getMinBufferSize(
            config.sampleRate,
            config.channelConfig,
            config.audioFormat
        )
        if (minBufferSize <= 0) {
            return RecorderStartResult.Failure("Invalid buffer size: $minBufferSize")
        }

        val recorder = AudioRecord(
            config.audioSource,
            config.sampleRate,
            config.channelConfig,
            config.audioFormat,
            minBufferSize * 2
        )
        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            recorder.release()
            return RecorderStartResult.Failure("AudioRecord initialization failed")
        }

        try {
            outputFile = config.outputFile
            outputStream = FileOutputStream(config.outputFile)
            writeWavHeader(outputStream, config.sampleRate, 1, 16, 0)
            totalBytes = 0
            startedAt = SystemClock.elapsedRealtime()
            this.config = config

            recorder.startRecording()
            audioRecord = recorder
            isRecording.set(true)

            recordingThread = Thread {
                val buffer = ByteArray(minBufferSize)
                while (isRecording.get()) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        try {
                            outputStream?.write(buffer, 0, read)
                            totalBytes += read
                        } catch (_: IOException) {
                            isRecording.set(false)
                        }
                    }
                }
            }.apply { start() }

            return RecorderStartResult.Success(startedAt, config.audioSource, config.sampleRate)
        } catch (ex: Exception) {
            recorder.release()
            outputStream?.close()
            outputFile?.delete()
            return RecorderStartResult.Failure("Failed to start recorder", ex)
        }
    }

    fun stop(): RecorderStopResult? {
        if (!isRecording.get()) {
            return null
        }
        isRecording.set(false)
        try {
            recordingThread?.join(1200)
        } catch (_: InterruptedException) {
            // Ignore
        }

        try {
            audioRecord?.stop()
        } catch (_: IllegalStateException) {
            // Ignore
        }
        audioRecord?.release()
        audioRecord = null

        try {
            outputStream?.flush()
            outputStream?.close()
        } catch (_: IOException) {
            // Ignore
        }

        val elapsedMs = SystemClock.elapsedRealtime() - startedAt
        val file = outputFile
        val activeConfig = config
        if (file != null && activeConfig != null) {
            updateWavHeader(file, totalBytes, activeConfig.sampleRate, 1, 16)
        }

        outputFile = null
        outputStream = null
        config = null

        return RecorderStopResult(elapsedMs, totalBytes)
    }

    private fun writeWavHeader(
        stream: FileOutputStream?,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int,
        dataLength: Long
    ) {
        if (stream == null) return
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8
        val totalDataLen = dataLength + 36

        val header = ByteArray(44)
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        writeInt(header, 4, totalDataLen.toInt())
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        writeInt(header, 16, 16)
        writeShort(header, 20, 1.toShort())
        writeShort(header, 22, channels.toShort())
        writeInt(header, 24, sampleRate)
        writeInt(header, 28, byteRate)
        writeShort(header, 32, blockAlign.toShort())
        writeShort(header, 34, bitsPerSample.toShort())
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        writeInt(header, 40, dataLength.toInt())
        stream.write(header, 0, 44)
    }

    private fun updateWavHeader(
        file: File,
        dataLength: Long,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int
    ) {
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8
        val totalDataLen = dataLength + 36
        try {
            RandomAccessFile(file, "rw").use { raf ->
                raf.seek(4)
                raf.write(intToByteArray(totalDataLen.toInt()))
                raf.seek(22)
                raf.write(shortToByteArray(channels.toShort()))
                raf.seek(24)
                raf.write(intToByteArray(sampleRate))
                raf.seek(28)
                raf.write(intToByteArray(byteRate))
                raf.seek(32)
                raf.write(shortToByteArray(blockAlign.toShort()))
                raf.seek(34)
                raf.write(shortToByteArray(bitsPerSample.toShort()))
                raf.seek(40)
                raf.write(intToByteArray(dataLength.toInt()))
            }
        } catch (_: IOException) {
            // Ignore header update errors
        }
    }

    private fun writeInt(buffer: ByteArray, offset: Int, value: Int) {
        buffer[offset] = (value and 0xff).toByte()
        buffer[offset + 1] = (value shr 8 and 0xff).toByte()
        buffer[offset + 2] = (value shr 16 and 0xff).toByte()
        buffer[offset + 3] = (value shr 24 and 0xff).toByte()
    }

    private fun writeShort(buffer: ByteArray, offset: Int, value: Short) {
        buffer[offset] = (value.toInt() and 0xff).toByte()
        buffer[offset + 1] = (value.toInt() shr 8 and 0xff).toByte()
    }

    private fun intToByteArray(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xff).toByte(),
            (value shr 8 and 0xff).toByte(),
            (value shr 16 and 0xff).toByte(),
            (value shr 24 and 0xff).toByte()
        )
    }

    private fun shortToByteArray(value: Short): ByteArray {
        return byteArrayOf(
            (value.toInt() and 0xff).toByte(),
            (value.toInt() shr 8 and 0xff).toByte()
        )
    }
}
