package com.callrecord.app.utils

import android.content.Context

class SettingsStore(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun isAutoRecordEnabled(): Boolean {
        return prefs.getBoolean(KEY_AUTO_RECORD, true)
    }

    fun setAutoRecordEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_RECORD, enabled).apply()
    }

    companion object {
        private const val KEY_AUTO_RECORD = "auto_record"
    }
}
