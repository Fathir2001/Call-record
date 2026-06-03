package com.callrecord.app.ui.screens

import android.media.MediaPlayer
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callrecord.app.ui.viewmodel.DetailViewModel
import com.callrecord.app.utils.DateTimeFormatter
import com.callrecord.app.utils.DurationFormatter
import com.callrecord.app.utils.ShareUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit
) {
    val recording by viewModel.recording.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember(recording?.filePath) { mutableStateOf(false) }
    val player = remember(recording?.filePath) { MediaPlayer() }

    DisposableEffect(recording?.filePath) {
        val filePath = recording?.filePath
        if (!filePath.isNullOrBlank()) {
            runCatching {
                player.reset()
                player.setDataSource(filePath)
                player.prepare()
                isPrepared = true
            }.onFailure {
                isPrepared = false
            }
        } else {
            isPrepared = false
        }
        onDispose {
            runCatching {
                player.stop()
                player.release()
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Recording") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val filePath = recording?.filePath ?: return@IconButton
                        ShareUtils.shareRecording(context, File(filePath))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = {
                        viewModel.deleteRecording()
                        onBack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )

            val current = recording
            if (current == null) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = "Recording not found", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = current.contactName.ifBlank { "Unknown" },
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = current.phoneNumber.ifBlank { "Unknown" },
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = current.callType.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = DateTimeFormatter.formatReadable(current.startedAt),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Duration ${DurationFormatter.formatDuration(current.durationMs)}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = {
                            if (!isPrepared) {
                                return@Button
                            }
                            if (isPlaying) {
                                runCatching { player.pause() }
                                isPlaying = false
                            } else {
                                runCatching { player.start() }
                                isPlaying = true
                                player.setOnCompletionListener { isPlaying = false }
                            }
                        }) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            Text(text = if (isPlaying) "Pause" else "Play")
                        }
                    }
                }
            }
        }
    }
}
