package com.callrecord.app.ui.screens

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.callrecord.app.utils.PermissionsHelper

@Composable
fun PermissionsScreen(onRequestPermissions: () -> Unit) {
    val permissions = PermissionsHelper.requiredPermissions()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Permissions required",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Grant the permissions below to enable call recording.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(permissions) { permission ->
                Text(
                    text = "• ${permissionLabel(permission)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Button(onClick = onRequestPermissions) {
            Text(text = "Grant permissions")
        }
    }
}

private fun permissionLabel(permission: String): String {
    return when (permission) {
        Manifest.permission.RECORD_AUDIO -> "Record audio"
        Manifest.permission.READ_PHONE_STATE -> "Phone state"
        Manifest.permission.READ_CONTACTS -> "Contacts (optional)"
        Manifest.permission.POST_NOTIFICATIONS -> "Notifications"
        else -> permission
    }
}
