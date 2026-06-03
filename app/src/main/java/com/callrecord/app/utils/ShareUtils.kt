package com.callrecord.app.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object ShareUtils {

    fun shareRecording(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            context.packageName + ".fileprovider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "audio/wav"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share recording"))
    }
}
