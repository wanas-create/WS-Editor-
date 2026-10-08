package com.example.model

/**
 * Creative visual effects for Video & Photo editing.
 * Each effect provides a human-friendly name, category, preview badge/icon, and description.
 */
enum class VisualEffectPreset(
    val id: String,
    val displayName: String,
    val category: String,
    val badge: String,
    val description: String
) {
    NONE(
        id = "none",
        displayName = "None",
        category = "Basic",
        badge = "Ø",
        description = "No visual effect applied"
    ),
    GLITCH(
        id = "glitch",
        displayName = "RGB Glitch",
        category = "Retro & Cyber",
        badge = "⚡",
        description = "Chromatic RGB split and scanline cyber distortion"
    ),
    VHS_RETRO(
        id = "vhs",
        displayName = "VHS Tape",
        category = "Retro & Cyber",
        badge = "📼",
        description = "Vintage 80s tape texture, scanlines and timestamp look"
    ),
    NEON_GLOW(
        id = "neon_glow",
        displayName = "Neon Glow",
        category = "Glow & Light",
        badge = "✨",
        description = "Electrifying cyan and magenta neon ambient radiance"
    ),
    RGB_SPLIT(
        id = "rgb_split",
        displayName = "Prism Split",
        category = "Light & Color",
        badge = "🌈",
        description = "Spectral prism color dispersion with chromatic fringing"
    ),
    CINEMATIC_GRAIN(
        id = "film_grain",
        displayName = "Film Grain",
        category = "Cinema",
        badge = "🎞️",
        description = "Organic 35mm motion picture grain and cinematic texture"
    ),
    LIGHT_LEAK(
        id = "light_leak",
        displayName = "Light Leak",
        category = "Glow & Light",
        badge = "☀️",
        description = "Warm anamorphic lens flare and golden light bleed"
    ),
    VIGNETTE(
        id = "vignette",
        displayName = "Vignette Dark",
        category = "Focus",
        badge = "🎯",
        description = "Soft edge shading focusing attention to center"
    ),
    DREAMY_BLUR(
        id = "dreamy_blur",
        displayName = "Dreamy Halation",
        category = "Mood",
        badge = "☁️",
        description = "Soft ethereal glow and romantic cinematic diffusion"
    ),
    CYBER_GRID(
        id = "cyber_grid",
        displayName = "Cyberpunk",
        category = "Retro & Cyber",
        badge = "🌐",
        description = "High-contrast electric cyan and ultraviolet matrix"
    ),
    BW_CONTRAST(
        id = "bw_noir",
        displayName = "Noir Silver",
        category = "Cinema",
        badge = "🎬",
        description = "High-key dramatic monochrome with silver highlight curve"
    );

    companion object {
        fun fromId(id: String?): VisualEffectPreset {
            if (id.isNullOrBlank()) return NONE
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) || it.name.equals(id, ignoreCase = true) } ?: NONE
        }
    }
}
