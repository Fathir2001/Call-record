package com.callrecord.app.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.callrecord.app.domain.model.CallRecording
import com.callrecord.app.domain.model.CallType
import com.callrecord.app.domain.repository.RecordingRepository
import com.callrecord.app.recorder.CallRecorder
import com.callrecord.app.recorder.RecorderStartResult
import com.callrecord.app.utils.CallStateStore
import com.callrecord.app.utils.FileNameFormatter
import com.callrecord.app.utils.NotificationHelper
import com.callrecord.app.utils.SettingsStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class RecorderService : Service() {

    @Inject lateinit var repository: RecordingRepository
    @Inject lateinit var fileNameFormatter: FileNameFormatter
    @Inject lateinit var callStateStore: CallStateStore
    @Inject lateinit var settingsStore: SettingsStore

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val callRecorder = CallRecorder()

    private var currentFile: File? = null
    private var currentCallType: CallType = CallType.UNKNOWN
    private var currentNumber: String = "Unknown"
    private var currentContact: String = "Unknown"
    private var currentAudioSource: Int = -1
    private var startedAt: Long = 0

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> handleStart(intent)
            ACTION_STOP -> handleStop()
        }
        return START_STICKY
    }

    private fun handleStart(intent: Intent) {
        if (callStateStore.isRecording || !settingsStore.isAutoRecordEnabled()) {
            return
        }
        NotificationHelper.ensureChannel(this)
        startForeground(NOTIFICATION_ID, NotificationHelper.buildRecordingNotification(this))

        currentNumber = intent.getStringExtra(EXTRA_NUMBER) ?: "Unknown"
        currentContact = intent.getStringExtra(EXTRA_CONTACT) ?: "Unknown"
        val typeName = intent.getStringExtra(EXTRA_CALL_TYPE) ?: CallType.UNKNOWN.name
        currentCallType = runCatching { CallType.valueOf(typeName) }.getOrDefault(CallType.UNKNOWN)

        val timestamp = System.currentTimeMillis()
        val fileName = fileNameFormatter.buildFileName(currentCallType, currentNumber, currentContact, timestamp)
        val outputDir = getExternalFilesDir(android.os.Environment.DIRECTORY_MUSIC) ?: filesDir
        val outputFile = File(outputDir, "recordings/$fileName")
        currentFile = outputFile

        when (val result = callRecorder.start(outputFile)) {
            is RecorderStartResult.Success -> {
                startedAt = System.currentTimeMillis()
                currentAudioSource = result.audioSource
                callStateStore.isRecording = true
                callStateStore.callStartTime = startedAt
                callStateStore.lastCallType = currentCallType.name
            }
            is RecorderStartResult.Failure -> {
                Log.w(TAG, "Recording failed: ${result.reason}", result.error)
                stopForegroundCompat()
                stopSelf()
            }
        }
    }

    private fun handleStop() {
        if (!callStateStore.isRecording) {
            stopForegroundCompat()
            stopSelf()
            return
        }

        val stopResult = callRecorder.stop()
        val durationMs = stopResult?.durationMs ?: 0L
        val file = currentFile
        if (file != null && file.exists() && durationMs > 0) {
            val recording = CallRecording(
                phoneNumber = currentNumber,
                contactName = currentContact,
                callType = currentCallType,
                startedAt = startedAt,
                durationMs = durationMs,
                filePath = file.absolutePath,
                audioSource = currentAudioSource
            )
            serviceScope.launch {
                repository.insertRecording(recording)
            }
        }

        callStateStore.isRecording = false
        callStateStore.lastCallType = ""
        callStateStore.callStartTime = 0L
        currentFile = null
        stopForegroundCompat()
        stopSelf()
    }

    private fun stopForegroundCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
    }

    override fun onDestroy() {
        callRecorder.stop()
        serviceScope.launch {
            callStateStore.isRecording = false
        }
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.callrecord.app.action.START"
        const val ACTION_STOP = "com.callrecord.app.action.STOP"
        const val EXTRA_NUMBER = "extra_number"
        const val EXTRA_CONTACT = "extra_contact"
        const val EXTRA_CALL_TYPE = "extra_call_type"
        private const val NOTIFICATION_ID = 1201
        private const val TAG = "RecorderService"

        fun start(
            context: Context,
            callType: CallType,
            number: String,
            contactName: String
        ) {
            val intent = Intent(context, RecorderService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_CALL_TYPE, callType.name)
                putExtra(EXTRA_NUMBER, number)
                putExtra(EXTRA_CONTACT, contactName)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, RecorderService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
