package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ====================================================================
// WS PROFESSIONAL AI-EDITOR DARK THEME PALETTE
// ====================================================================

// Backgrounds: Deep black / dark charcoal
val WsBackground = Color(0xFF090A0E)
val WsBackgroundSecondary = Color(0xFF0E1017)
val WsBackgroundElevated = Color(0xFF141722)

// Premium Glass-like Dark Cards & Surfaces
val WsSurfaceCard = Color(0xFF131620)
val WsSurfaceCardElevated = Color(0xFF1A1F2C)
val WsSurfaceCardHover = Color(0xFF222838)
val WsSurfaceGlass = Color(0xD9131620)
val WsSurfaceGlassHighlight = Color(0x260070F3)
val WsSurfaceLight = Color(0xFF242A3B)

// Borders & AI Glow
val WsBorder = Color(0xFF222738)
val WsBorderSubtle = Color(0xFF181B26)
val WsBorderGlow = Color(0x660070F3)       // Electric blue ambient border glow
val WsBorderCyanGlow = Color(0x6600D2FF)   // Electric cyan ambient border glow

// Typography Colors
val WsTextPrimary = Color(0xFFFFFFFF)      // Pure white primary text
val WsTextSecondary = Color(0xFF94A3B8)    // Soft slate-gray secondary text
val WsTextMuted = Color(0xFF64748B)        // Muted gray for metadata / labels
val WsTextInverse = Color(0xFF090A0E)      // Dark text when placed on bright highlights

// Electric Blue & AI Accents
val WsElectricBlue = Color(0xFF0070F3)     // Vivid electric blue core
val WsElectricCyan = Color(0xFF00D2FF)     // Glowing electric cyan
val WsElectricBlueDark = Color(0xFF0056B3) // Deep blue pressed state
val WsBlueGlow = Color(0x330070F3)         // Soft ambient blue aura
val WsCyanGlow = Color(0x2600D2FF)         // Soft ambient cyan aura

// Premium AI Action Gradients
val WsAiGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF0070F3), Color(0xFF00D2FF))
)
val WsGlassGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF1C2130), Color(0xFF121520))
)
val WsDarkCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF171A25), Color(0xFF0E1017))
)

// Buttons & Controls
val WsButtonDark = Color(0xFF161924)
val WsButtonDarkBorder = Color(0xFF262C3E)
val WsButtonActive = Color(0xFF0070F3)

// Accent States
val WsAccentGold = Color(0xFFF5A623)       // Gold for PRO badges
val WsAccentRed = Color(0xFFFF453A)        // Soft neon red for errors/recording
val WsAccentGreen = Color(0xFF10B981)      // Green for ready states

// Pro Timeline Colors
val WsTimelineBg = Color(0xFF090A0E)
val WsTimelineTrack = Color(0xFF131622)
val WsTimelinePlayhead = Color(0xFF00D2FF) // Electric cyan playhead
val WsTimelineText = Color(0xFFCBD5E1)

// Backward-compatibility aliases (pointing to dark theme tokens)
val WsPureWhite = WsTextPrimary
val WsOffWhite = WsSurfaceCard
val WsBlack = WsElectricBlue
val WsDarkGray = WsSurfaceCardElevated
val WsMediumGray = WsTextSecondary
val WsLightGray = WsTextMuted
val WsDivider = WsBorder
val WsAccentBlue = WsElectricBlue
