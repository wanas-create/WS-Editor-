package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// WS Professional AI-Editor Dark Scheme
private val WsDarkColorScheme = darkColorScheme(
    primary = WsElectricBlue,
    onPrimary = WsTextPrimary,
    primaryContainer = WsSurfaceCardElevated,
    onPrimaryContainer = WsTextPrimary,
    secondary = WsElectricCyan,
    onSecondary = WsTextInverse,
    background = WsBackground,
    onBackground = WsTextPrimary,
    surface = WsSurfaceCard,
    onSurface = WsTextPrimary,
    surfaceVariant = WsSurfaceCardElevated,
    onSurfaceVariant = WsTextSecondary,
    outline = WsBorder,
    outlineVariant = WsBorderSubtle,
    error = WsAccentRed,
    onError = WsTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Premium modern dark AI-editor theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WsDarkColorScheme,
        typography = Typography,
        content = content
    )
}
