package com.callrecord.app.ui.screens

import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callrecord.app.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val storagePath = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        ?.resolve("recordings")
        ?.absolutePath
        ?: "Internal storage"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Auto record calls", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Record calls automatically when answered",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Switch(
                        checked = state.autoRecordEnabled,
                        onCheckedChange = viewModel::setAutoRecordEnabled
                    )
                }

                Column {
                    Text(text = "Storage location", style = MaterialTheme.typography.titleMedium)
                    Text(text = storagePath, style = MaterialTheme.typography.bodyMedium)
                }

                Column {
                    Text(text = "Battery optimization", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Disable optimizations to prevent background kills.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                        context.startActivity(intent)
                    }) {
                        Text(text = "Open settings")
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Text(
                        text = "Some devices restrict call recording. The app will fall back to microphone audio when needed.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
