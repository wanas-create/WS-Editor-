package com.example

import com.example.media.VideoFrameProvider
import com.example.model.VideoClip
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VideoTimelineDurationTest {

    @Test
    fun test10SecondVideoDurationPreserved() {
        val clip10s = VideoClip(
            uriString = "content://media/external/video/media/1",
            name = "10s Clip",
            originalDurationMs = 10000L,
            trimStartMs = 0L,
            trimEndMs = 10000L
        )
        assertEquals(10000L, clip10s.originalDurationMs)
        assertEquals(10000L, clip10s.trimEndMs)
        assertEquals(10000L, clip10s.effectiveDurationMs)
        assertTrue(clip10s.effectiveDurationMs > 5000L)
    }

    @Test
    fun test30SecondVideoDurationPreserved() {
        val clip30s = VideoClip(
            uriString = "content://media/external/video/media/2",
            name = "30s Clip",
            originalDurationMs = 30000L,
            trimStartMs = 0L,
            trimEndMs = 30000L
        )
        assertEquals(30000L, clip30s.originalDurationMs)
        assertEquals(30000L, clip30s.trimEndMs)
        assertEquals(30000L, clip30s.effectiveDurationMs)
        assertTrue(clip30s.effectiveDurationMs > 5000L)
    }

    @Test
    fun test2MinuteVideoDurationPreserved() {
        val clip2m = VideoClip(
            uriString = "content://media/external/video/media/3",
            name = "2min Clip",
            originalDurationMs = 120000L,
            trimStartMs = 0L,
            trimEndMs = 120000L
        )
        assertEquals(120000L, clip2m.originalDurationMs)
        assertEquals(120000L, clip2m.trimEndMs)
        assertEquals(120000L, clip2m.effectiveDurationMs)
        assertTrue(clip2m.effectiveDurationMs > 5000L)
    }

    @Test
    fun testMultiClipTotalDurationAndExactEnd() {
        val clips = listOf(
            VideoClip(uriString = "uri1", originalDurationMs = 10000L, trimEndMs = 10000L),
            VideoClip(uriString = "uri2", originalDurationMs = 30000L, trimEndMs = 30000L),
            VideoClip(uriString = "uri3", originalDurationMs = 120000L, trimEndMs = 120000L)
        )
        val totalDurationMs = clips.sumOf { it.effectiveDurationMs }
        // 10s + 30s + 120s = 160s (160,000ms)
        assertEquals(160000L, totalDurationMs)

        // Timeline exact beginning and end
        val startMs = 0L
        val endMs = totalDurationMs
        assertEquals(0L, startMs)
        assertEquals(160000L, endMs)
    }

    @Test
    fun testSpeedAndTrimCalculations() {
        // 10s clip at 2.0x speed
        val fastClip = VideoClip(
            uriString = "uri_fast",
            originalDurationMs = 10000L,
            trimStartMs = 0L,
            trimEndMs = 10000L,
            speed = 2.0f
        )
        assertEquals(5000L, fastClip.effectiveDurationMs)

        // 10s clip at 0.5x speed
        val slowClip = VideoClip(
            uriString = "uri_slow",
            originalDurationMs = 10000L,
            trimStartMs = 0L,
            trimEndMs = 10000L,
            speed = 0.5f
        )
        assertEquals(20000L, slowClip.effectiveDurationMs)

        // 120s clip trimmed to [10s, 90s]
        val trimmedClip = VideoClip(
            uriString = "uri_trimmed",
            originalDurationMs = 120000L,
            trimStartMs = 10000L,
            trimEndMs = 90000L
        )
        assertEquals(80000L, trimmedClip.effectiveDurationMs)
    }

    @Test
    fun testContinuousThumbnailTimesSpansFullDuration() {
        // 30s clip with 6 thumbnail tiles
        val clip = VideoClip(
            uriString = "uri_thumb",
            originalDurationMs = 30000L,
            trimStartMs = 0L,
            trimEndMs = 30000L
        )
        val times = VideoFrameProvider.getClipThumbnailTimes(clip, 6)
        assertEquals(6, times.size)
        assertEquals(0L, times[0])
        assertEquals(5000L, times[1])
        assertEquals(10000L, times[2])
        assertEquals(15000L, times[3])
        assertEquals(20000L, times[4])
        assertEquals(25000L, times[5])

        // Verify all timestamps are strictly within [0, 30000]
        times.forEach { t ->
            assertTrue(t in 0L..30000L)
        }
    }
}
