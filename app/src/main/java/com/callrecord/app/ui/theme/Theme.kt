package com.callrecord.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Accent,
    secondary = Accent,
    background = PrimaryBackground,
    surface = SecondarySurface,
    error = Error,
    onPrimary = PrimaryBackground,
    onSecondary = PrimaryBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onError = Color.White
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = AppTypography,
        content = content
    )
}
