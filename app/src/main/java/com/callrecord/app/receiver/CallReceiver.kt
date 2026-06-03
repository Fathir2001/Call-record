package com.callrecord.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.callrecord.app.domain.model.CallType
import com.callrecord.app.service.RecorderService
import com.callrecord.app.utils.CallStateStore
import com.callrecord.app.utils.ContactResolver
import com.callrecord.app.utils.SettingsStore

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED &&
            intent.action != "android.intent.action.PHONE_STATE") {
            return
        }

        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val stateStr = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

        val state = when (stateStr) {
            TelephonyManager.EXTRA_STATE_RINGING -> TelephonyManager.CALL_STATE_RINGING
            TelephonyManager.EXTRA_STATE_OFFHOOK -> TelephonyManager.CALL_STATE_OFFHOOK
            TelephonyManager.EXTRA_STATE_IDLE -> TelephonyManager.CALL_STATE_IDLE
            else -> telephonyManager.callState
        }

        val store = CallStateStore(context)
        val settings = SettingsStore(context)
        val resolver = ContactResolver(context)

        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                store.lastState = TelephonyManager.CALL_STATE_RINGING
                store.lastNumber = incomingNumber ?: ""
                Log.d(TAG, "Incoming call ringing: ${store.lastNumber}")
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                val wasRinging = store.lastState == TelephonyManager.CALL_STATE_RINGING
                val callType = if (wasRinging) CallType.INCOMING else CallType.OUTGOING
                val number = if (wasRinging) {
                    store.lastNumber
                } else {
                    if (store.lastNumber.isNotBlank()) store.lastNumber else (incomingNumber ?: "Unknown")
                }

                if (settings.isAutoRecordEnabled() && !store.isRecording) {
                    val contactName = resolver.resolveContactName(number)
                    RecorderService.start(context, callType, number.ifBlank { "Unknown" }, contactName)
                }

                store.lastState = TelephonyManager.CALL_STATE_OFFHOOK
                store.lastNumber = number
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                if (store.isRecording) {
                    RecorderService.stop(context)
                }
                store.lastState = TelephonyManager.CALL_STATE_IDLE
                store.lastNumber = ""
                store.isRecording = false
                store.callStartTime = 0L
                store.lastCallType = ""
                Log.d(TAG, "Call ended")
            }
        }
    }

    companion object {
        private const val TAG = "CallReceiver"
    }
}
