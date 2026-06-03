package com.callrecord.app.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionsHelper {

    fun requiredPermissions(): List<String> {
        return mandatoryPermissions() + optionalPermissions()
    }

    fun mandatoryPermissions(): List<String> {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        return permissions
    }

    fun optionalPermissions(): List<String> {
        return listOf(Manifest.permission.READ_CONTACTS)
    }

    fun hasAllPermissions(context: Context): Boolean {
        return mandatoryPermissions().all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
}
