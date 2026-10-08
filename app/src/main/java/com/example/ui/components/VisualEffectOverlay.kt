package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.model.VisualEffectPreset

/**
 * Procedural visual overlay composable rendered over the viewport for effects that include
 * animated scanlines, film grain particles, light leaks, vignette, and neon borders.
 */
@Composable
fun VisualEffectOverlay(
    effect: VisualEffectPreset,
    intensity: Float,
    modifier: Modifier = Modifier
) {
    if (effect == VisualEffectPreset.NONE || intensity <= 0.001f) return

    val infiniteTransition = rememberInfiniteTransition(label = "effect_anim")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "anim_phase"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        val safeIntensity = intensity.coerceIn(0f, 1f)

        when (effect) {
            VisualEffectPreset.GLITCH -> {
                // Horizontal scanlines and cyan/red glitch flashes
                val scanlineSpacing = 10f
                var y = 0f
                val scanlineAlpha = (0.12f * safeIntensity).coerceIn(0f, 0.4f)
                while (y < h) {
                    drawLine(
                        color = Color.Black.copy(alpha = scanlineAlpha),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 2f
                    )
                    y += scanlineSpacing
                }

                // Random glitch horizontal bands
                val bandY = (animatedOffset * h * 1.8f) % h
                val bandHeight = 18f * safeIntensity
                drawRect(
                    color = Color(0x3300FFFF).copy(alpha = 0.25f * safeIntensity),
                    topLeft = Offset(0f, bandY),
                    size = androidx.compose.ui.geometry.Size(w, bandHeight)
                )
                drawRect(
                    color = Color(0x33FF0055).copy(alpha = 0.20f * safeIntensity),
                    topLeft = Offset(0f, (bandY + 28f) % h),
                    size = androidx.compose.ui.geometry.Size(w, bandHeight * 0.7f)
                )
            }

            VisualEffectPreset.VHS_RETRO -> {
                // VHS retro tape noise + horizontal tracking line + timestamp corner bar
                val lineY = (animatedOffset * h)
                drawLine(
                    color = Color.White.copy(alpha = 0.28f * safeIntensity),
                    start = Offset(0f, lineY),
                    end = Offset(w, lineY),
                    strokeWidth = 3f * safeIntensity
                )
                // Bottom tape static bar
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0x44CCCCCC).copy(alpha = 0.35f * safeIntensity))
                    ),
                    topLeft = Offset(0f, h - 36f * safeIntensity),
                    size = androidx.compose.ui.geometry.Size(w, 36f * safeIntensity)
                )
                // Subtle scanlines
                var sy = 0f
                while (sy < h) {
                    drawLine(
                        color = Color.Black.copy(alpha = 0.08f * safeIntensity),
                        start = Offset(0f, sy),
                        end = Offset(w, sy),
                        strokeWidth = 1.5f
                    )
                    sy += 8f
                }
            }

            VisualEffectPreset.NEON_GLOW -> {
                // Radiant outer neon border in electric cyan & magenta
                val borderW = (12f * safeIntensity).coerceAtLeast(3f)
                drawRect(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF00E5FF).copy(alpha = 0.45f * safeIntensity),
                            Color(0xFFFF007F).copy(alpha = 0.45f * safeIntensity),
                            Color(0xFF7000FF).copy(alpha = 0.45f * safeIntensity),
                            Color(0xFF00E5FF).copy(alpha = 0.45f * safeIntensity)
                        )
                    ),
                    style = Stroke(width = borderW)
                )
            }

            VisualEffectPreset.RGB_SPLIT -> {
                // Subtle dual fringe borders
                drawRect(
                    color = Color(0xFFFF0033).copy(alpha = 0.15f * safeIntensity),
                    topLeft = Offset(-4f * safeIntensity, 0f),
                    size = androidx.compose.ui.geometry.Size(8f * safeIntensity, h)
                )
                drawRect(
                    color = Color(0xFF00E5FF).copy(alpha = 0.15f * safeIntensity),
                    topLeft = Offset(w - 4f * safeIntensity, 0f),
                    size = androidx.compose.ui.geometry.Size(8f * safeIntensity, h)
                )
            }

            VisualEffectPreset.CINEMATIC_GRAIN -> {
                // Procedural micro-dust simulation with fine dots
                val step = 28f
                var gx = 10f
                while (gx < w) {
                    var gy = 10f
                    while (gy < h) {
                        val pseudoSeed = ((gx * 37f + gy * 73f + animatedOffset * 1000f) % 17f)
                        if (pseudoSeed < 3.5f) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.15f * safeIntensity),
                                radius = 1.2f,
                                center = Offset(gx, gy)
                            )
                        } else if (pseudoSeed > 14f) {
                            drawCircle(
                                color = Color.Black.copy(alpha = 0.18f * safeIntensity),
                                radius = 1.0f,
                                center = Offset(gx + 3f, gy + 3f)
                            )
                        }
                        gy += step
                    }
                    gx += step
                }
            }

            VisualEffectPreset.LIGHT_LEAK -> {
                // Anamorphic sunburst radiant leak at top-right
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFC107).copy(alpha = 0.42f * safeIntensity),
                            Color(0xFFFF5722).copy(alpha = 0.25f * safeIntensity),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.9f, h * 0.1f),
                        radius = (w * 0.75f).coerceAtLeast(100f)
                    ),
                    center = Offset(w * 0.9f, h * 0.1f),
                    radius = w * 0.75f
                )
            }

            VisualEffectPreset.VIGNETTE -> {
                // Deep radial shadow around edges
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.35f * safeIntensity),
                            Color.Black.copy(alpha = 0.75f * safeIntensity)
                        ),
                        center = Offset(w / 2f, h / 2f),
                        radius = (Math.max(w, h) * 0.65f)
                    )
                )
            }

            VisualEffectPreset.DREAMY_BLUR -> {
                // Soft white misty ambient halation
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.12f * safeIntensity),
                            Color.Transparent
                        ),
                        center = Offset(w / 2f, h / 2f),
                        radius = (Math.max(w, h) * 0.55f)
                    )
                )
            }

            VisualEffectPreset.CYBER_GRID -> {
                // Perspective grid lines at bottom
                val gridBottomAlpha = 0.30f * safeIntensity
                val numLines = 8
                val startY = h * 0.7f
                for (i in 0..numLines) {
                    val lineX = w * (i.toFloat() / numLines.toFloat())
                    drawLine(
                        color = Color(0xFF00E5FF).copy(alpha = gridBottomAlpha),
                        start = Offset(w / 2f, startY),
                        end = Offset(lineX, h),
                        strokeWidth = 1.5f
                    )
                }
                var horizY = startY
                while (horizY <= h) {
                    drawLine(
                        color = Color(0xFFFF007F).copy(alpha = gridBottomAlpha * 0.8f),
                        start = Offset(0f, horizY),
                        end = Offset(w, horizY),
                        strokeWidth = 1.2f
                    )
                    horizY += 18f
                }
            }

            VisualEffectPreset.BW_CONTRAST -> {
                // Film border letterbox / framing
                val letterboxH = 10f * safeIntensity
                drawRect(
                    color = Color.Black.copy(alpha = 0.9f * safeIntensity),
                    topLeft = Offset.Zero,
                    size = androidx.compose.ui.geometry.Size(w, letterboxH)
                )
                drawRect(
                    color = Color.Black.copy(alpha = 0.9f * safeIntensity),
                    topLeft = Offset(0f, h - letterboxH),
                    size = androidx.compose.ui.geometry.Size(w, letterboxH)
                )
            }

            VisualEffectPreset.NONE -> {}
        }
    }
}
