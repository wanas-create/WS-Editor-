package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class VideoProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val aspectRatio: String = "16:9",
    val resolution: String = "4K 60FPS",
    val durationSec: Float = 16.0f,
    val videoUri: String? = null,
    val filterId: String = "cyber_neon",
    val playbackSpeed: Float = 1.0f,
    val volume: Float = 1.0f,
    val activeFxId: String = "none",
    val musicTrackName: String = "Cyberpunk Pulse 128BPM",
    val customFontCount: Int = 1,
    val clipsCount: Int = 4,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_fonts")
data class CustomFontEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fontId: String,
    val displayName: String,
    val fileName: String,
    val filePath: String,
    val fileSizeKb: Int,
    val isDeviceUpload: Boolean = true,
    val addedAt: Long = System.currentTimeMillis()
)

data class VideoClip(
    val id: String,
    val name: String,
    val startSec: Float,
    val endSec: Float,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val videoUri: String? = null
) {
    val durationSec: Float
        get() = (endSec - startSec).coerceAtLeast(0.5f)
}

data class TextOverlayItem(
    val id: String,
    val text: String,
    val fontId: String,
    val fontName: String,
    val customFontPath: String? = null,
    val colorHex: Long = 0xFF00D9FF,
    val fontSizeSp: Float = 26f,
    val startSec: Float = 0.5f,
    val endSec: Float = 8.5f,
    val offsetXPercent: Float = 0.5f,
    val offsetYPercent: Float = 0.72f,
    val hasGlow: Boolean = true,
    val hasBgBox: Boolean = true
) {
    val durationSec: Float
        get() = (endSec - startSec).coerceAtLeast(0.5f)
}

data class StickerFxClip(
    val id: String,
    val label: String,
    val badge: String,
    val startSec: Float,
    val endSec: Float,
    val isFx: Boolean = true
) {
    val durationSec: Float
        get() = (endSec - startSec).coerceAtLeast(0.5f)
}

data class AudioTrackClip(
    val id: String,
    val trackName: String,
    val artist: String,
    val startSec: Float,
    val endSec: Float,
    val volume: Float = 0.85f
) {
    val durationSec: Float
        get() = (endSec - startSec).coerceAtLeast(0.5f)
}

data class VideoFilterPreset(
    val id: String,
    val name: String,
    val category: String,
    val tintColorHex: Long,
    val contrast: Float = 1f,
    val saturation: Float = 1f,
    val brightness: Float = 0f
)

enum class MainTab {
    HOME,
    VIDEO,
    PHOTO,
    WANAS_AI
}

enum class VideoTool(val label: String) {
    NONE("Timeline"),
    SPLIT("Split"),
    SPEED("Speed"),
    VOLUME("Volume"),
    FILTER("Filter"),
    TEXT("Text"),
    STICKER("Sticker"),
    MUSIC("Music"),
    FX("FX")
}

enum class PhotoTool(val label: String) {
    FILTERS("Filters"),
    BEAUTY("Beauty"),
    BG_REMOVE("BG Remove"),
    ADJUST("Adjust")
}
