package com.example.media

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.LruCache
import com.example.model.VideoClip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object VideoFrameProvider {

    // In-memory LRU cache holding decoded thumbnail bitmaps
    private val frameCache = object : LruCache<String, Bitmap>(300) {}

    /**
     * Extracts the real duration in milliseconds from a media URI or path.
     * Never hard-limits to 5 seconds.
     */
    fun getVideoDurationMs(context: Context, uriString: String?): Long {
        if (uriString.isNullOrBlank()) return 0L
        var retriever: MediaMetadataRetriever? = null
        try {
            retriever = MediaMetadataRetriever()
            setDataSourceSafe(context, retriever, uriString)
            val durStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val dur = durStr?.toLongOrNull() ?: 0L
            if (dur > 0L) return dur
        } catch (_: Throwable) {
            // MediaMetadataRetriever may fail on mock URIs or non-media files
        } finally {
            try {
                retriever?.release()
            } catch (_: Throwable) {}
        }
        return 0L
    }

    /**
     * Extracts a frame bitmap from the video at [timeMs].
     * Uses OPTION_CLOSEST and scaled frame extraction for continuous frame accuracy.
     */
    suspend fun getFrameAtTime(
        context: Context,
        uriString: String?,
        timeMs: Long,
        targetWidth: Int = 160,
        targetHeight: Int = 100
    ): Bitmap? = withContext(Dispatchers.IO) {
        if (uriString.isNullOrBlank()) return@withContext null

        val cacheKey = "$uriString-$timeMs-$targetWidth-$targetHeight"
        frameCache.get(cacheKey)?.let { return@withContext it }

        var retriever: MediaMetadataRetriever? = null
        try {
            retriever = MediaMetadataRetriever()
            setDataSourceSafe(context, retriever, uriString)
            val timeUs = (timeMs * 1000L).coerceAtLeast(0L)

            val bitmap: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 && targetWidth > 0 && targetHeight > 0) {
                try {
                    retriever.getScaledFrameAtTime(
                        timeUs,
                        MediaMetadataRetriever.OPTION_CLOSEST,
                        targetWidth,
                        targetHeight
                    )
                } catch (_: Throwable) { null }
                    ?: try {
                        retriever.getScaledFrameAtTime(
                            timeUs,
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                            targetWidth,
                            targetHeight
                        )
                    } catch (_: Throwable) { null }
                    ?: try {
                        retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST)
                    } catch (_: Throwable) { null }
            } else {
                try {
                    retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST)
                } catch (_: Throwable) { null }
                    ?: try {
                        retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    } catch (_: Throwable) { null }
            }

            if (bitmap != null) {
                frameCache.put(cacheKey, bitmap)
                return@withContext bitmap
            }
        } catch (_: Throwable) {
            // Ignore extraction errors and fallback to image decoding
        } finally {
            try {
                retriever?.release()
            } catch (_: Throwable) {}
        }

        // Fallback: Check if uriString is a readable image file (e.g. AI motion keyframe or photo)
        try {
            val file = if (uriString.startsWith("file://")) File(Uri.parse(uriString).path ?: "") else File(uriString)
            if (file.exists() && file.isFile) {
                val bmp = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                if (bmp != null) {
                    val scaled = if (targetWidth > 0 && targetHeight > 0) {
                        Bitmap.createScaledBitmap(bmp, targetWidth, targetHeight, true)
                    } else bmp
                    frameCache.put(cacheKey, scaled)
                    return@withContext scaled
                }
            }
        } catch (_: Throwable) {}

        return@withContext null
    }

    /**
     * Calculates timestamps for a continuous thumbnail sequence across the clip's trimmed range.
     * Samples across the duration so every tile represents a distinct point in time.
     */
    fun getClipThumbnailTimes(clip: VideoClip, count: Int): List<Long> {
        val safeCount = count.coerceAtLeast(1)
        val startMs = clip.trimStartMs.coerceAtLeast(0L)
        val endMs = clip.trimEndMs.coerceAtLeast(startMs + 50L)
        val range = (endMs - startMs).toFloat()

        return (0 until safeCount).map { i ->
            (startMs + ((i.toFloat() + 0.5f) / safeCount.toFloat() * range)).toLong().coerceIn(startMs, endMs)
        }
    }

    private fun Long.coerceAtAtLeast(min: Long): Long = if (this < min) min else this

    private fun setDataSourceSafe(context: Context, retriever: MediaMetadataRetriever, uriString: String) {
        if (uriString.startsWith("content://") || uriString.startsWith("android.resource://")) {
            retriever.setDataSource(context, Uri.parse(uriString))
        } else if (uriString.startsWith("file://")) {
            val uri = Uri.parse(uriString)
            val path = uri.path
            if (path != null && File(path).exists()) {
                retriever.setDataSource(path)
            } else {
                retriever.setDataSource(context, uri)
            }
        } else if (File(uriString).exists()) {
            retriever.setDataSource(uriString)
        } else {
            retriever.setDataSource(context, Uri.parse(uriString))
        }
    }
}
