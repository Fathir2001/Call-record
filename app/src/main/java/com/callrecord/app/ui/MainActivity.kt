package com.callrecord.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.callrecord.app.service.RecorderService
import com.callrecord.app.ui.navigation.AppNavHost
import com.callrecord.app.ui.screens.PermissionsScreen
import com.callrecord.app.ui.theme.AppTheme
import com.callrecord.app.utils.PermissionsHelper
import com.callrecord.app.utils.SettingsStore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val context = LocalContext.current
                var hasPermissions by remember {
                    mutableStateOf(PermissionsHelper.hasAllPermissions(context))
                }
                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { result ->
                    hasPermissions = result.values.all { it }
                }

                if (hasPermissions) {
                    LaunchedEffect(hasPermissions) {
                        if (SettingsStore(context).isAutoRecordEnabled()) {
                            RecorderService.enable(context)
                        }
                    }
                    AppNavHost()
                } else {
                    PermissionsScreen(
                        onRequestPermissions = {
                            launcher.launch(PermissionsHelper.requiredPermissions().toTypedArray())
                        }
                    )
                }
            }
        }
    }
}
