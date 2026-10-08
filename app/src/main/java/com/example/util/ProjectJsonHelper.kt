package com.example.util

import com.example.model.AspectRatioOption
import com.example.model.AudioTrackItem
import com.example.model.DrawingPath
import com.example.model.DrawingPoint
import com.example.model.FilterPreset
import com.example.model.PhotoEditState
import com.example.model.PhotoTextOverlay
import com.example.model.SubtitleItem
import com.example.model.TransitionType
import com.example.model.VideoClip
import com.example.model.VisualEffectPreset
import org.json.JSONArray
import org.json.JSONObject

/**
 * Robust JSON serializer and deserializer for Video and Photo project metadata.
 * Saves and restores complete timeline data, audio tracks, subtitles, text overlays,
 * drawing strokes, and creative visual effects with intensity.
 */
object ProjectJsonHelper {

    fun serializeVideoProject(
        clips: List<VideoClip>,
        audioTracks: List<AudioTrackItem>,
        subtitles: List<SubtitleItem>,
        aspectRatio: AspectRatioOption,
        playheadMs: Long
    ): String {
        val root = JSONObject()
        root.put("version", 2)
        root.put("aspectRatio", aspectRatio.name)
        root.put("playheadMs", playheadMs)

        // Clips array
        val clipsArray = JSONArray()
        for (clip in clips) {
            val cObj = JSONObject()
            cObj.put("id", clip.id)
            cObj.put("uriString", clip.uriString)
            cObj.put("name", clip.name)
            cObj.put("originalDurationMs", clip.originalDurationMs)
            cObj.put("trimStartMs", clip.trimStartMs)
            cObj.put("trimEndMs", clip.trimEndMs)
            cObj.put("speed", clip.speed.toDouble())
            cObj.put("volume", clip.volume.toDouble())
            cObj.put("isMuted", clip.isMuted)
            cObj.put("rotationDegrees", clip.rotationDegrees)
            cObj.put("isFlippedH", clip.isFlippedH)
            cObj.put("isFlippedV", clip.isFlippedV)
            cObj.put("filter", clip.filter.name)
            cObj.put("brightness", clip.brightness.toDouble())
            cObj.put("contrast", clip.contrast.toDouble())
            cObj.put("saturation", clip.saturation.toDouble())
            cObj.put("sharpness", clip.sharpness.toDouble())
            cObj.put("blur", clip.blur.toDouble())
            cObj.put("isReversed", clip.isReversed)
            cObj.put("isChromaKeyEnabled", clip.isChromaKeyEnabled)
            cObj.put("chromaKeyColor", clip.chromaKeyColor)
            cObj.put("chromaKeyTolerance", clip.chromaKeyTolerance.toDouble())
            cObj.put("isPip", clip.isPip)
            cObj.put("pipScale", clip.pipScale.toDouble())
            cObj.put("pipOffsetX", clip.pipOffsetX.toDouble())
            cObj.put("pipOffsetY", clip.pipOffsetY.toDouble())
            cObj.put("transition", clip.transition.name)
            cObj.put("effect", clip.effect.name)
            cObj.put("effectIntensity", clip.effectIntensity.toDouble())
            clipsArray.put(cObj)
        }
        root.put("clips", clipsArray)

        // Audio Tracks
        val audioArray = JSONArray()
        for (track in audioTracks) {
            val aObj = JSONObject()
            aObj.put("id", track.id)
            aObj.put("title", track.title)
            aObj.put("uriString", track.uriString ?: "")
            aObj.put("startTimelineMs", track.startTimelineMs)
            aObj.put("durationMs", track.durationMs)
            aObj.put("volume", track.volume.toDouble())
            aObj.put("fadeInMs", track.fadeInMs)
            aObj.put("fadeOutMs", track.fadeOutMs)
            aObj.put("isVoiceRecording", track.isVoiceRecording)
            audioArray.put(aObj)
        }
        root.put("audioTracks", audioArray)

        // Subtitles
        val subArray = JSONArray()
        for (sub in subtitles) {
            val sObj = JSONObject()
            sObj.put("id", sub.id)
            sObj.put("text", sub.text)
            sObj.put("startTimelineMs", sub.startTimelineMs)
            sObj.put("endTimelineMs", sub.endTimelineMs)
            sObj.put("posXRatio", sub.posXRatio.toDouble())
            sObj.put("posYRatio", sub.posYRatio.toDouble())
            sObj.put("fontSizeSp", sub.fontSizeSp.toDouble())
            sObj.put("textColor", sub.textColor)
            sObj.put("bgColor", sub.bgColor)
            sObj.put("isBold", sub.isBold)
            sObj.put("animation", sub.animation)
            subArray.put(sObj)
        }
        root.put("subtitles", subArray)

        return root.toString()
    }

    data class ParsedVideoProject(
        val clips: List<VideoClip>,
        val audioTracks: List<AudioTrackItem>,
        val subtitles: List<SubtitleItem>,
        val aspectRatio: AspectRatioOption,
        val playheadMs: Long
    )

    fun deserializeVideoProject(json: String?): ParsedVideoProject? {
        if (json.isNullOrBlank() || json == "{}") return null
        return try {
            val root = JSONObject(json)
            val aspect = try {
                AspectRatioOption.valueOf(root.optString("aspectRatio", "RATIO_16_9"))
            } catch (_: Throwable) {
                AspectRatioOption.RATIO_16_9
            }
            val playheadMs = root.optLong("playheadMs", 0L)

            val clipsList = mutableListOf<VideoClip>()
            val clipsArray = root.optJSONArray("clips")
            if (clipsArray != null) {
                for (i in 0 until clipsArray.length()) {
                    val obj = clipsArray.getJSONObject(i)
                    val filter = try {
                        FilterPreset.valueOf(obj.optString("filter", FilterPreset.NONE.name))
                    } catch (_: Throwable) {
                        FilterPreset.NONE
                    }
                    val transition = try {
                        TransitionType.valueOf(obj.optString("transition", TransitionType.NONE.name))
                    } catch (_: Throwable) {
                        TransitionType.NONE
                    }
                    val effect = try {
                        VisualEffectPreset.valueOf(obj.optString("effect", VisualEffectPreset.NONE.name))
                    } catch (_: Throwable) {
                        VisualEffectPreset.NONE
                    }

                    clipsList.add(
                        VideoClip(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            uriString = obj.optString("uriString", ""),
                            name = obj.optString("name", "Clip"),
                            originalDurationMs = obj.optLong("originalDurationMs", 10000L),
                            trimStartMs = obj.optLong("trimStartMs", 0L),
                            trimEndMs = obj.optLong("trimEndMs", obj.optLong("originalDurationMs", 10000L)),
                            speed = obj.optDouble("speed", 1.0).toFloat(),
                            volume = obj.optDouble("volume", 1.0).toFloat(),
                            isMuted = obj.optBoolean("isMuted", false),
                            rotationDegrees = obj.optInt("rotationDegrees", 0),
                            isFlippedH = obj.optBoolean("isFlippedH", false),
                            isFlippedV = obj.optBoolean("isFlippedV", false),
                            filter = filter,
                            brightness = obj.optDouble("brightness", 0.0).toFloat(),
                            contrast = obj.optDouble("contrast", 1.0).toFloat(),
                            saturation = obj.optDouble("saturation", 1.0).toFloat(),
                            sharpness = obj.optDouble("sharpness", 0.0).toFloat(),
                            blur = obj.optDouble("blur", 0.0).toFloat(),
                            isReversed = obj.optBoolean("isReversed", false),
                            isChromaKeyEnabled = obj.optBoolean("isChromaKeyEnabled", false),
                            chromaKeyColor = obj.optLong("chromaKeyColor", 0xFF00FF00),
                            chromaKeyTolerance = obj.optDouble("chromaKeyTolerance", 0.3).toFloat(),
                            isPip = obj.optBoolean("isPip", false),
                            pipScale = obj.optDouble("pipScale", 0.4).toFloat(),
                            pipOffsetX = obj.optDouble("pipOffsetX", 0.25).toFloat(),
                            pipOffsetY = obj.optDouble("pipOffsetY", 0.25).toFloat(),
                            transition = transition,
                            effect = effect,
                            effectIntensity = obj.optDouble("effectIntensity", 1.0).toFloat()
                        )
                    )
                }
            }

            val audioList = mutableListOf<AudioTrackItem>()
            val audioArray = root.optJSONArray("audioTracks")
            if (audioArray != null) {
                for (i in 0 until audioArray.length()) {
                    val obj = audioArray.getJSONObject(i)
                    audioList.add(
                        AudioTrackItem(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            title = obj.optString("title", "Audio"),
                            uriString = obj.optString("uriString").takeIf { it.isNotEmpty() },
                            startTimelineMs = obj.optLong("startTimelineMs", 0L),
                            durationMs = obj.optLong("durationMs", 5000L),
                            volume = obj.optDouble("volume", 1.0).toFloat(),
                            fadeInMs = obj.optLong("fadeInMs", 0L),
                            fadeOutMs = obj.optLong("fadeOutMs", 0L),
                            isVoiceRecording = obj.optBoolean("isVoiceRecording", false)
                        )
                    )
                }
            }

            val subList = mutableListOf<SubtitleItem>()
            val subArray = root.optJSONArray("subtitles")
            if (subArray != null) {
                for (i in 0 until subArray.length()) {
                    val obj = subArray.getJSONObject(i)
                    subList.add(
                        SubtitleItem(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            text = obj.optString("text", ""),
                            startTimelineMs = obj.optLong("startTimelineMs", 0L),
                            endTimelineMs = obj.optLong("endTimelineMs", 3000L),
                            posXRatio = obj.optDouble("posXRatio", 0.5).toFloat(),
                            posYRatio = obj.optDouble("posYRatio", 0.82).toFloat(),
                            fontSizeSp = obj.optDouble("fontSizeSp", 18.0).toFloat(),
                            textColor = obj.optLong("textColor", 0xFFFFFFFF),
                            bgColor = obj.optLong("bgColor", 0xAA000000),
                            isBold = obj.optBoolean("isBold", true),
                            animation = obj.optString("animation", "Fade")
                        )
                    )
                }
            }

            ParsedVideoProject(
                clips = clipsList,
                audioTracks = audioList,
                subtitles = subList,
                aspectRatio = aspect,
                playheadMs = playheadMs
            )
        } catch (_: Throwable) {
            null
        }
    }

    fun serializePhotoProject(
        state: PhotoEditState,
        drawingPaths: List<DrawingPath>
    ): String {
        val root = JSONObject()
        root.put("version", 2)
        root.put("uriString", state.uriString)
        root.put("originalUriString", state.originalUriString)
        root.put("cutoutUriString", state.cutoutUriString ?: "")
        root.put("rotationDegrees", state.rotationDegrees)
        root.put("isFlippedH", state.isFlippedH)
        root.put("isFlippedV", state.isFlippedV)
        root.put("aspectRatio", state.aspectRatio.name)
        root.put("filter", state.filter.name)
        root.put("brightness", state.brightness.toDouble())
        root.put("contrast", state.contrast.toDouble())
        root.put("saturation", state.saturation.toDouble())
        root.put("sharpness", state.sharpness.toDouble())
        root.put("exposure", state.exposure.toDouble())
        root.put("warmth", state.warmth.toDouble())
        root.put("blur", state.blur.toDouble())
        root.put("vignette", state.vignette.toDouble())
        root.put("frameStyle", state.frameStyle)
        root.put("isBgRemoved", state.isBgRemoved)
        root.put("backgroundReplacement", state.backgroundReplacement ?: "")
        root.put("isAiEnhanced", state.isAiEnhanced)
        root.put("effect", state.effect.name)
        root.put("effectIntensity", state.effectIntensity.toDouble())

        // Text overlays
        val textArray = JSONArray()
        for (overlay in state.textOverlays) {
            val tObj = JSONObject()
            tObj.put("id", overlay.id)
            tObj.put("text", overlay.text)
            tObj.put("fontSizeSp", overlay.fontSizeSp.toDouble())
            tObj.put("color", overlay.color)
            tObj.put("fontFamily", overlay.fontFamily)
            tObj.put("alignment", overlay.alignment)
            tObj.put("isBold", overlay.isBold)
            tObj.put("isItalic", overlay.isItalic)
            tObj.put("hasBackgroundBox", overlay.hasBackgroundBox)
            tObj.put("backgroundColor", overlay.backgroundColor)
            tObj.put("posXRatio", overlay.posXRatio.toDouble())
            tObj.put("posYRatio", overlay.posYRatio.toDouble())
            textArray.put(tObj)
        }
        root.put("textOverlays", textArray)

        // Drawings
        val drawingsArray = JSONArray()
        for (dp in drawingPaths) {
            val dObj = JSONObject()
            dObj.put("color", dp.color)
            dObj.put("strokeWidth", dp.strokeWidth.toDouble())
            dObj.put("opacity", dp.opacity.toDouble())
            dObj.put("isEraser", dp.isEraser)
            val ptsArray = JSONArray()
            for (pt in dp.points) {
                val ptObj = JSONObject()
                ptObj.put("x", pt.x.toDouble())
                ptObj.put("y", pt.y.toDouble())
                ptsArray.put(ptObj)
            }
            dObj.put("points", ptsArray)
            drawingsArray.put(dObj)
        }
        root.put("drawings", drawingsArray)

        return root.toString()
    }

    data class ParsedPhotoProject(
        val state: PhotoEditState,
        val drawingPaths: List<DrawingPath>
    )

    fun deserializePhotoProject(json: String?): ParsedPhotoProject? {
        if (json.isNullOrBlank() || json == "{}") return null
        return try {
            val root = JSONObject(json)
            val aspect = try {
                AspectRatioOption.valueOf(root.optString("aspectRatio", "RATIO_1_1"))
            } catch (_: Throwable) {
                AspectRatioOption.RATIO_1_1
            }
            val filter = try {
                FilterPreset.valueOf(root.optString("filter", FilterPreset.NONE.name))
            } catch (_: Throwable) {
                FilterPreset.NONE
            }
            val effect = try {
                VisualEffectPreset.valueOf(root.optString("effect", VisualEffectPreset.NONE.name))
            } catch (_: Throwable) {
                VisualEffectPreset.NONE
            }

            val textOverlays = mutableListOf<PhotoTextOverlay>()
            val textArray = root.optJSONArray("textOverlays")
            if (textArray != null) {
                for (i in 0 until textArray.length()) {
                    val obj = textArray.getJSONObject(i)
                    textOverlays.add(
                        PhotoTextOverlay(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            text = obj.optString("text", "Text"),
                            fontSizeSp = obj.optDouble("fontSizeSp", 24.0).toFloat(),
                            color = obj.optLong("color", 0xFFFFFFFF),
                            fontFamily = obj.optString("fontFamily", "Default"),
                            alignment = obj.optString("alignment", "Center"),
                            isBold = obj.optBoolean("isBold", true),
                            isItalic = obj.optBoolean("isItalic", false),
                            hasBackgroundBox = obj.optBoolean("hasBackgroundBox", false),
                            backgroundColor = obj.optLong("backgroundColor", 0xAA000000),
                            posXRatio = obj.optDouble("posXRatio", 0.5).toFloat(),
                            posYRatio = obj.optDouble("posYRatio", 0.5).toFloat()
                        )
                    )
                }
            }

            val photoState = PhotoEditState(
                uriString = root.optString("uriString", ""),
                originalUriString = root.optString("originalUriString", root.optString("uriString", "")),
                cutoutUriString = root.optString("cutoutUriString").takeIf { it.isNotEmpty() },
                rotationDegrees = root.optInt("rotationDegrees", 0),
                isFlippedH = root.optBoolean("isFlippedH", false),
                isFlippedV = root.optBoolean("isFlippedV", false),
                aspectRatio = aspect,
                filter = filter,
                brightness = root.optDouble("brightness", 0.0).toFloat(),
                contrast = root.optDouble("contrast", 1.0).toFloat(),
                saturation = root.optDouble("saturation", 1.0).toFloat(),
                sharpness = root.optDouble("sharpness", 0.0).toFloat(),
                exposure = root.optDouble("exposure", 0.0).toFloat(),
                warmth = root.optDouble("warmth", 0.0).toFloat(),
                blur = root.optDouble("blur", 0.0).toFloat(),
                vignette = root.optDouble("vignette", 0.0).toFloat(),
                frameStyle = root.optString("frameStyle", "None"),
                isBgRemoved = root.optBoolean("isBgRemoved", false),
                backgroundReplacement = root.optString("backgroundReplacement").takeIf { it.isNotEmpty() },
                isAiEnhanced = root.optBoolean("isAiEnhanced", false),
                effect = effect,
                effectIntensity = root.optDouble("effectIntensity", 1.0).toFloat(),
                textOverlays = textOverlays
            )

            val drawingList = mutableListOf<DrawingPath>()
            val drawingsArray = root.optJSONArray("drawings")
            if (drawingsArray != null) {
                for (i in 0 until drawingsArray.length()) {
                    val dObj = drawingsArray.getJSONObject(i)
                    val pts = mutableListOf<DrawingPoint>()
                    val ptsArray = dObj.optJSONArray("points")
                    if (ptsArray != null) {
                        for (p in 0 until ptsArray.length()) {
                            val ptObj = ptsArray.getJSONObject(p)
                            pts.add(DrawingPoint(ptObj.optDouble("x", 0.0).toFloat(), ptObj.optDouble("y", 0.0).toFloat()))
                        }
                    }
                    drawingList.add(
                        DrawingPath(
                            points = pts,
                            color = dObj.optLong("color", 0xFFFFFFFF),
                            strokeWidth = dObj.optDouble("strokeWidth", 8.0).toFloat(),
                            opacity = dObj.optDouble("opacity", 1.0).toFloat(),
                            isEraser = dObj.optBoolean("isEraser", false)
                        )
                    )
                }
            }

            ParsedPhotoProject(state = photoState, drawingPaths = drawingList)
        } catch (_: Throwable) {
            null
        }
    }
}
