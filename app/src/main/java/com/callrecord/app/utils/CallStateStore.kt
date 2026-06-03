package com.callrecord.app.utils

import android.content.Context
import android.telephony.TelephonyManager

class CallStateStore(context: Context) {
    private val prefs = context.getSharedPreferences("call_state", Context.MODE_PRIVATE)

    var lastState: Int
        get() = prefs.getInt(KEY_LAST_STATE, TelephonyManager.CALL_STATE_IDLE)
        set(value) {
            prefs.edit().putInt(KEY_LAST_STATE, value).apply()
        }

    var lastNumber: String
        get() = prefs.getString(KEY_LAST_NUMBER, "") ?: ""
        set(value) {
            prefs.edit().putString(KEY_LAST_NUMBER, value).apply()
        }

    var isRecording: Boolean
        get() = prefs.getBoolean(KEY_IS_RECORDING, false)
        set(value) {
            prefs.edit().putBoolean(KEY_IS_RECORDING, value).apply()
        }

    var callStartTime: Long
        get() = prefs.getLong(KEY_START_TIME, 0L)
        set(value) {
            prefs.edit().putLong(KEY_START_TIME, value).apply()
        }

    var lastCallType: String
        get() = prefs.getString(KEY_CALL_TYPE, "") ?: ""
        set(value) {
            prefs.edit().putString(KEY_CALL_TYPE, value).apply()
        }

    companion object {
        private const val KEY_LAST_STATE = "last_state"
        private const val KEY_LAST_NUMBER = "last_number"
        private const val KEY_IS_RECORDING = "is_recording"
        private const val KEY_START_TIME = "start_time"
        private const val KEY_CALL_TYPE = "call_type"
    }
}
