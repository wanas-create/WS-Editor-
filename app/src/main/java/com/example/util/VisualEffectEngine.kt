package com.example.util

import android.graphics.ColorMatrix
import androidx.compose.ui.graphics.Color
import com.example.model.VisualEffectPreset

/**
 * Visual effects color matrix generator and overlay styles.
 * Applies real chromatic, contrast, luminance, and matrix changes to compose/canvas rendering.
 */
object VisualEffectEngine {

    /**
     * Builds a ColorMatrix for an effect preset at the given intensity (0.0f to 1.0f).
     */
    fun buildEffectColorMatrix(effect: VisualEffectPreset, intensity: Float): ColorMatrix {
        val cm = ColorMatrix()
        val safeIntensity = intensity.coerceIn(0f, 1f)
        if (effect == VisualEffectPreset.NONE || safeIntensity <= 0.001f) {
            return cm
        }

        when (effect) {
            VisualEffectPreset.GLITCH -> {
                // High chromatic channel split + cyber contrast
                val rBoost = 1.0f + 0.45f * safeIntensity
                val gDrop = 1.0f - 0.20f * safeIntensity
                val bBoost = 1.0f + 0.55f * safeIntensity
                val shift = 25f * safeIntensity
                val glitchMat = ColorMatrix(floatArrayOf(
                    rBoost, 0f, 0f, 0f, shift,
                    0f, gDrop, 0.1f * safeIntensity, 0f, 0f,
                    0.2f * safeIntensity, 0f, bBoost, 0f, -shift,
                    0f, 0f, 0f, 1f, 0f
                ))
                cm.postConcat(glitchMat)
            }
            VisualEffectPreset.VHS_RETRO -> {
                // Warm-shifted vintage tape with lowered green and elevated red/magenta
                val vhsMat = ColorMatrix(floatArrayOf(
                    1.0f + 0.3f * safeIntensity, 0f, 0f, 0f, 18f * safeIntensity,
                    0f, 0.95f - 0.15f * safeIntensity, 0f, 0f, 4f * safeIntensity,
                    0f, 0f, 0.85f + 0.2f * safeIntensity, 0f, 12f * safeIntensity,
                    0f, 0f, 0f, 1f, 0f
                ))
                cm.postConcat(vhsMat)
                cm.setSaturation(1.0f - 0.25f * safeIntensity)
            }
            VisualEffectPreset.NEON_GLOW -> {
                // Electric cyan-magenta high vibrancy with lifted mids
                val neonMat = ColorMatrix(floatArrayOf(
                    1.1f + 0.35f * safeIntensity, 0f, 0.2f * safeIntensity, 0f, 15f * safeIntensity,
                    0f, 1.0f + 0.15f * safeIntensity, 0.1f * safeIntensity, 0f, 5f * safeIntensity,
                    0.2f * safeIntensity, 0f, 1.25f + 0.4f * safeIntensity, 0f, 25f * safeIntensity,
                    0f, 0f, 0f, 1f, 0f
                ))
                cm.postConcat(neonMat)
                cm.setSaturation(1.0f + 0.8f * safeIntensity)
            }
            VisualEffectPreset.RGB_SPLIT -> {
                // Strong prism cross-channel dispersion
                val prismMat = ColorMatrix(floatArrayOf(
                    1.2f * safeIntensity + (1f - safeIntensity), 0f, 0.25f * safeIntensity, 0f, 10f * safeIntensity,
                    0.1f * safeIntensity, 1.0f, 0f, 0f, 0f,
                    0.3f * safeIntensity, 0f, 1.3f * safeIntensity + (1f - safeIntensity), 0f, -10f * safeIntensity,
                    0f, 0f, 0f, 1f, 0f
                ))
                cm.postConcat(prismMat)
            }
            VisualEffectPreset.CINEMATIC_GRAIN -> {
                // Organic 35mm print contrast curve
                val c = 1.0f + 0.25f * safeIntensity
                val trans = (-0.5f * c + 0.5f) * 255f
                val grainMat = ColorMatrix(floatArrayOf(
                    c, 0f, 0f, 0f, trans + 6f * safeIntensity,
                    0f, c, 0f, 0f, trans + 4f * safeIntensity,
                    0f, 0f, c, 0f, trans - 2f * safeIntensity,
                    0f, 0f, 0f, 1f, 0f
                ))
                cm.postConcat(grainMat)
            }
            VisualEffectPreset.LIGHT_LEAK -> {
                // Golden amber anamorphic flare bleed
                val leakMat = ColorMatrix(floatArrayOf(
                    1.0f + 0.4f * safeIntensity, 0f, 0f, 0f, 35f * safeIntensity,
                    0f, 1.0f + 0.2f * safeIntensity, 0f, 0f, 15f * safeIntensity,
                    0f, 0f, 0.9f - 0.2f * safeIntensity, 0f, -10f * safeIntensity,
                    0f, 0f, 0f, 1f, 0f
                ))
                cm.postConcat(leakMat)
            }
            VisualEffectPreset.VIGNETTE -> {
                // Contrast boost enhancing vignette focal center
                val c = 1.0f + 0.35f * safeIntensity
                val trans = (-0.5f * c + 0.5f) * 255f
                val vigMat = ColorMatrix(floatArrayOf(
                    c, 0f, 0f, 0f, trans - 10f * safeIntensity,
                    0f, c, 0f, 0f, trans - 10f * safeIntensity,
                    0f, 0f, c, 0f, trans - 10f * safeIntensity,
                    0f, 0f, 0f, 1f, 0f
                ))
                cm.postConcat(vigMat)
            }
            VisualEffectPreset.DREAMY_BLUR -> {
                // Ethereal halation with lifted blacks and softened contrast
                val dreamMat = ColorMatrix(floatArrayOf(
                    0.95f, 0f, 0f, 0f, 25f * safeIntensity,
                    0f, 0.95f, 0f, 0f, 22f * safeIntensity,
                    0f, 0f, 1.05f, 0f, 28f * safeIntensity,
                    0f, 0f, 0f, 1f, 0f
                ))
                cm.postConcat(dreamMat)
                cm.setSaturation(1.0f - 0.15f * safeIntensity)
            }
            VisualEffectPreset.CYBER_GRID -> {
                // Deep ultraviolet & electric cyan matrix
                val cyberMat = ColorMatrix(floatArrayOf(
                    0.8f + 0.4f * safeIntensity, 0f, 0.3f * safeIntensity, 0f, 20f * safeIntensity,
                    0f, 1.0f, 0f, 0f, -5f * safeIntensity,
                    0.2f * safeIntensity, 0f, 1.4f * safeIntensity + (1f - safeIntensity), 0f, 30f * safeIntensity,
                    0f, 0f, 0f, 1f, 0f
                ))
                cm.postConcat(cyberMat)
                cm.setSaturation(1.0f + 0.5f * safeIntensity)
            }
            VisualEffectPreset.BW_CONTRAST -> {
                // Dramatic silver monochrome
                val mono = ColorMatrix()
                mono.setSaturation(1.0f - safeIntensity)
                cm.postConcat(mono)
                val c = 1.0f + 0.5f * safeIntensity
                val trans = (-0.5f * c + 0.5f) * 255f
                val contMat = ColorMatrix(floatArrayOf(
                    c, 0f, 0f, 0f, trans,
                    0f, c, 0f, 0f, trans,
                    0f, 0f, c, 0f, trans,
                    0f, 0f, 0f, 1f, 0f
                ))
                cm.postConcat(contMat)
            }
            VisualEffectPreset.NONE -> {}
        }

        return cm
    }
}
