package com.example.model

import androidx.compose.ui.graphics.Color

enum class AspectRatioOption(val label: String, val ratio: Float, val widthWeight: Int, val heightWeight: Int) {
    FREE("Free", 0f, 0, 0),
    RATIO_1_1("1:1", 1f, 1, 1),
    RATIO_4_5("4:5", 4f / 5f, 4, 5),
    RATIO_9_16("9:16", 9f / 16f, 9, 16),
    RATIO_16_9("16:9", 16f / 9f, 16, 9),
    RATIO_3_4("3:4", 3f / 4f, 3, 4),
    RATIO_21_9("21:9", 21f / 9f, 21, 9)
}

enum class FilterPreset(val displayName: String) {
    NONE("Original"),
    CINEMATIC("Cinematic"),
    MONO("B&W Noir"),
    CYBER("Cyberpunk"),
    WARM("Golden Warm"),
    TEAL_ORANGE("Teal & Orange"),
    VINTAGE("Vintage 70s"),
    DRAMATIC("Dramatic"),
    VIVID("Vivid Boost"),
    CLEAN("Clean Studio")
}

enum class TransitionType(val displayName: String) {
    NONE("None"),
    DISSOLVE("Dissolve"),
    FADE_BLACK("Fade Black"),
    SLIDE_LEFT("Slide Left"),
    SLIDE_RIGHT("Slide Right"),
    ZOOM_IN("Zoom In"),
    FLASH("Flash White"),
    GLITCH("Glitch")
}

data class VideoClip(
    val id: String = java.util.UUID.randomUUID().toString(),
    val uriString: String,
    val name: String = "Clip",
    val originalDurationMs: Long = 5000L,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = 5000L,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val isMuted: Boolean = false,
    val rotationDegrees: Int = 0,
    val isFlippedH: Boolean = false,
    val isFlippedV: Boolean = false,
    val filter: FilterPreset = FilterPreset.NONE,
    val brightness: Float = 0f, // -1f to 1f
    val contrast: Float = 1f,   // 0.5f to 2f
    val saturation: Float = 1f, // 0f to 2f
    val sharpness: Float = 0f,  // 0f to 1f
    val blur: Float = 0f,       // 0f to 1f
    val isReversed: Boolean = false,
    val isChromaKeyEnabled: Boolean = false,
    val chromaKeyColor: Long = 0xFF00FF00, // Green
    val chromaKeyTolerance: Float = 0.3f,
    val isPip: Boolean = false,
    val pipScale: Float = 0.4f,
    val pipOffsetX: Float = 0.25f,
    val pipOffsetY: Float = 0.25f,
    val transition: TransitionType = TransitionType.NONE
) {
    val effectiveDurationMs: Long
        get() = (((trimEndMs - trimStartMs).coerceAtLeast(100L)) / speed).toLong()
}

data class AudioTrackItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val uriString: String? = null,
    val startTimelineMs: Long = 0L,
    val durationMs: Long = 5000L,
    val volume: Float = 1.0f,
    val fadeInMs: Long = 0L,
    val fadeOutMs: Long = 0L,
    val isVoiceRecording: Boolean = false
)

data class SubtitleItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val startTimelineMs: Long = 0L,
    val endTimelineMs: Long = 3000L,
    val posXRatio: Float = 0.5f,
    val posYRatio: Float = 0.82f,
    val fontSizeSp: Float = 18f,
    val textColor: Long = 0xFFFFFFFF,
    val bgColor: Long = 0xAA000000,
    val isBold: Boolean = true,
    val animation: String = "Fade"
)

data class DrawingPoint(val x: Float, val y: Float)

data class DrawingPath(
    val points: List<DrawingPoint>,
    val color: Long,
    val strokeWidth: Float,
    val opacity: Float = 1.0f,
    val isEraser: Boolean = false
)

data class PhotoTextOverlay(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String = "Your Text",
    val fontSizeSp: Float = 24f,
    val color: Long = 0xFFFFFFFF,
    val fontFamily: String = "Default", // Default, Sans, Serif, Monospace, Cursive
    val alignment: String = "Center", // Left, Center, Right
    val isBold: Boolean = true,
    val isItalic: Boolean = false,
    val hasBackgroundBox: Boolean = false,
    val backgroundColor: Long = 0xAA000000,
    val posXRatio: Float = 0.5f,
    val posYRatio: Float = 0.5f
)

data class PhotoEditState(
    val uriString: String,
    val originalUriString: String = uriString,
    val cutoutUriString: String? = null,
    val rotationDegrees: Int = 0,
    val isFlippedH: Boolean = false,
    val isFlippedV: Boolean = false,
    val aspectRatio: AspectRatioOption = AspectRatioOption.RATIO_1_1,
    val filter: FilterPreset = FilterPreset.NONE,
    val brightness: Float = 0f,
    val contrast: Float = 1f,
    val saturation: Float = 1f,
    val sharpness: Float = 0f,
    val exposure: Float = 0f,
    val warmth: Float = 0f,
    val blur: Float = 0f,
    val vignette: Float = 0f,
    val frameStyle: String = "None", // "None", "Polaroid", "Minimal White", "Cinema Black", "Cyberpunk", "Film Border", "Classic Gold", "Neon Violet"
    val isBgRemoved: Boolean = false,
    val backgroundReplacement: String? = null, // Color hex or style
    val isAiEnhanced: Boolean = false,
    val textOverlays: List<PhotoTextOverlay> = emptyList()
)

data class ExportConfig(
    val resolutionLabel: String = "1080p",
    val width: Int = 1920,
    val height: Int = 1080,
    val fps: Int = 30,
    val format: String = "MP4", // MP4, JPG, PNG
    val quality: String = "High"
)
