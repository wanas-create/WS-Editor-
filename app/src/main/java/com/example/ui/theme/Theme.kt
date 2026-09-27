package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WsDarkColorScheme = darkColorScheme(
    primary = WsCyan,
    onPrimary = Color(0xFF001F26),
    primaryContainer = Color(0xFF003642),
    onPrimaryContainer = WsCyan,
    secondary = WsPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2D124D),
    onSecondaryContainer = Color(0xFFE9D5FF),
    tertiary = TrackOrange,
    onTertiary = Color.Black,
    background = WsBackground,
    onBackground = WsTextPrimary,
    surface = WsSurface,
    onSurface = WsTextPrimary,
    surfaceVariant = WsSurfaceElevated,
    onSurfaceVariant = WsTextSecondary,
    outline = WsBorder,
    error = WsDanger
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WsDarkColorScheme,
        typography = Typography,
        content = content
    )
}
