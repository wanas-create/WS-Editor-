package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val WsBackground = Color(0xFF0A0A0A)
val WsSurface = Color(0xFF141418)
val WsSurfaceElevated = Color(0xFF1C1C24)
val WsSurfaceHigh = Color(0xFF252530)
val WsBorder = Color(0xFF2C2C3A)

val WsCyan = Color(0xFF00D9FF)
val WsCyanDim = Color(0xFF007A99)
val WsPurple = Color(0xFF7B00FF)
val WsPurpleLight = Color(0xFFA855F7)

// 4 Multi-layer timeline track left-border & accent colors
val TrackCyan = Color(0xFF00D9FF)
val TrackPurple = Color(0xFF7B00FF)
val TrackOrange = Color(0xFFFF8A00)
val TrackGreen = Color(0xFF00E676)

val WsTextPrimary = Color(0xFFF8FAFC)
val WsTextSecondary = Color(0xFF94A3B8)
val WsTextMuted = Color(0xFF64748B)
val WsDanger = Color(0xFFFF4D6D)
val WsWarning = Color(0xFFFFB800)

val WsPrimaryGradient = Brush.linearGradient(
    colors = listOf(WsCyan, WsPurple)
)

val WsCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF00D9FF), Color(0xFF4A00E0), Color(0xFF7B00FF))
)

val WsSubtleCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF18222D), Color(0xFF1C1429))
)
