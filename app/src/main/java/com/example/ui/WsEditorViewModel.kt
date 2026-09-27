package com.example.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AudioTrackClip
import com.example.data.CustomFontEntity
import com.example.data.FontRegistry
import com.example.data.MainTab
import com.example.data.PhotoTool
import com.example.data.StickerFxClip
import com.example.data.TextOverlayItem
import com.example.data.VideoClip
import com.example.data.VideoFilterPreset
import com.example.data.VideoProjectEntity
import com.example.data.VideoTool
import com.example.data.WsEditorDatabase
import com.example.data.WsEditorRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VideoEditorUiState(
    val projectTitle: String = "Cyberpunk_Reel_4K",
    val aspectRatio: String = "16:9",
    val selectedVideoUri: String? = null,
    val isPlaying: Boolean = false,
    val playheadSec: Float = 2.4f,
    val totalDurationSec: Float = 16.0f,
    val activeTool: VideoTool = VideoTool.TEXT,
    val selectedTrackIndex: Int = 1, // 0=Video, 1=Text, 2=Sticker/FX, 3=Audio
    val selectedClipId: String = "txt_1",
    val playbackSpeed: Float = 1.0f,
    val masterVolume: Float = 1.0f,
    val activeFilterId: String = "cyber_neon",
    val videoClips: List<VideoClip> = listOf(
        VideoClip(
            id = "vid_1",
            name = "NeoTokyo_Intro.mp4",
            startSec = 0.0f,
            endSec = 7.5f,
            speed = 1.0f,
            volume = 1.0f
        ),
        VideoClip(
            id = "vid_2",
            name = "Drift_Closeup_4K.mp4",
            startSec = 7.5f,
            endSec = 16.0f,
            speed = 1.25f,
            volume = 0.9f
        )
    ),
    val textOverlays: List<TextOverlayItem> = listOf(
        TextOverlayItem(
            id = "txt_1",
            text = "WS-EDITOR 4K PRO",
            fontId = "space_grotesk",
            fontName = "Space Grotesk",
            customFontPath = null,
            colorHex = 0xFF00D9FF,
            fontSizeSp = 26f,
            startSec = 0.5f,
            endSec = 9.5f,
            offsetXPercent = 0.5f,
            offsetYPercent = 0.72f,
            hasGlow = true,
            hasBgBox = true
        ),
        TextOverlayItem(
            id = "txt_2",
            text = "WSEDITOR.COM STUDIO",
            fontId = "bebas_neue",
            fontName = "Bebas Neue",
            customFontPath = null,
            colorHex = 0xFFFFFFFF,
            fontSizeSp = 22f,
            startSec = 9.5f,
            endSec = 15.5f,
            offsetXPercent = 0.5f,
            offsetYPercent = 0.82f,
            hasGlow = true,
            hasBgBox = false
        )
    ),
    val stickerFxClips: List<StickerFxClip> = listOf(
        StickerFxClip(
            id = "fx_1",
            label = "Neon Glitch FX",
            badge = "FX • GLITCH",
            startSec = 1.0f,
            endSec = 6.5f,
            isFx = true
        ),
        StickerFxClip(
            id = "stk_1",
            label = "⚡ REC 4K Badge",
            badge = "STICKER",
            startSec = 6.5f,
            endSec = 14.0f,
            isFx = false
        )
    ),
    val audioClips: List<AudioTrackClip> = listOf(
        AudioTrackClip(
            id = "aud_1",
            trackName = "Cyberpunk Pulse 128BPM.wav",
            artist = "WS Audio Lab",
            startSec = 0.0f,
            endSec = 16.0f,
            volume = 0.85f
        )
    ),
    val isExportDialogVisible: Boolean = false,
    val exportProgress: Float = 0f,
    val isExporting: Boolean = false,
    val exportCompletedMessage: String? = null,
    val statusBannerMessage: String? = null
)

data class PhotoEditorUiState(
    val selectedPhotoUri: String? = null,
    val activeTool: PhotoTool = PhotoTool.FILTERS,
    val activeFilterId: String = "cyber_neon",
    val beautySmooth: Float = 0.45f,
    val beautyNeonRim: Float = 0.60f,
    val beautySharpness: Float = 0.35f,
    val isBgRemoved: Boolean = false,
    val bgBackdropMode: String = "Neon Cyber Grid", // "Transparent Checker", "Neon Cyber Grid", "Purple Studio Glow", "Solid Obsidian"
    val brightness: Float = 0.0f,
    val contrast: Float = 1.08f,
    val saturation: Float = 1.15f,
    val warmth: Float = 0.0f,
    val isComparingOriginal: Boolean = false,
    val photoSavedBanner: String? = null
)

class WsEditorViewModel(
    private val repository: WsEditorRepository
) : ViewModel() {

    private val _showSplash = MutableStateFlow(true)
    val showSplash: StateFlow<Boolean> = _showSplash.asStateFlow()

    private val _showProPaywall = MutableStateFlow(false)
    val showProPaywall: StateFlow<Boolean> = _showProPaywall.asStateFlow()

    private val _isProUnlocked = MutableStateFlow(false)
    val isProUnlocked: StateFlow<Boolean> = _isProUnlocked.asStateFlow()

    private val _activeProPlanSummary = MutableStateFlow<String?>(null)
    val activeProPlanSummary: StateFlow<String?> = _activeProPlanSummary.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    val projects: StateFlow<List<VideoProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customFonts: StateFlow<List<CustomFontEntity>> = repository.allCustomFonts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _videoState = MutableStateFlow(VideoEditorUiState())
    val videoState: StateFlow<VideoEditorUiState> = _videoState.asStateFlow()

    private val _photoState = MutableStateFlow(PhotoEditorUiState())
    val photoState: StateFlow<PhotoEditorUiState> = _photoState.asStateFlow()

    private var playbackJob: Job? = null

    val filterPresets = listOf(
        VideoFilterPreset("original", "Original", "RAW", 0x00000000, 1.0f, 1.0f, 0f),
        VideoFilterPreset("cyber_neon", "Cyber Neon", "VN PRO", 0x3300D9FF, 1.18f, 1.35f, 5f),
        VideoFilterPreset("purple_haze", "Purple Haze", "SYNTH", 0x387B00FF, 1.15f, 1.28f, 0f),
        VideoFilterPreset("matrix_teal", "Matrix Teal", "CINEMA", 0x3300E676, 1.22f, 0.90f, -5f),
        VideoFilterPreset("sunset_gold", "Golden Flare", "WARM", 0x33FF8A00, 1.12f, 1.25f, 8f),
        VideoFilterPreset("noir_4k", "Obsidian Noir", "MONO", 0x44000000, 1.40f, 0.05f, -8f),
        VideoFilterPreset("tokyo_rain", "Tokyo Rain", "LUT", 0x2E0099FF, 1.25f, 1.15f, -4f)
    )

    val speedPresets = listOf(0.2f, 0.5f, 1.0f, 1.5f, 2.0f, 4.0f, 8.0f)

    val musicLibrary = listOf(
        Triple("Cyberpunk Pulse 128BPM.wav", "WS Audio Lab", 16.0f),
        Triple("Synthwave Horizon 110BPM.mp3", "Neon Drive", 16.0f),
        Triple("Trap Beat 808 Bass.wav", "Studio Master", 14.5f),
        Triple("Cinematic Trailer Rise.flac", "Hans V.", 16.0f),
        Triple("Lo-Fi Midnight Cut.mp3", "Tokyo Tape", 15.0f)
    )

    val fxLibrary = listOf(
        Pair("Neon Glitch FX", "FX • GLITCH"),
        Pair("Chromatic Split", "FX • RGB"),
        Pair("VHS Scanlines", "FX • RETRO"),
        Pair("Beat Zoom Pulse", "FX • MOTION"),
        Pair("Cyber Halo Flash", "FX • LIGHT"),
        Pair("🔥 Fire Frame", "STICKER"),
        Pair("⚡ REC 4K Badge", "STICKER"),
        Pair("💎 VN PRO Crown", "STICKER")
    )

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun finishSplash() {
        _showSplash.value = false
    }

    fun openProPaywall() {
        _showProPaywall.value = true
    }

    fun closeProPaywall() {
        _showProPaywall.value = false
    }

    fun unlockProPlan(planTitle: String, priceText: String, countryCode: String) {
        _isProUnlocked.value = true
        _activeProPlanSummary.value = "$planTitle ($priceText • $countryCode)"
        _showProPaywall.value = false
        _videoState.update {
            it.copy(statusBannerMessage = "WS-Editor Pro Unlocked: $planTitle ($priceText)")
        }
    }

    fun selectTab(tab: MainTab) {
        if (tab != MainTab.VIDEO && _videoState.value.isPlaying) {
            togglePlayback()
        }
        _currentTab.value = tab
    }

    fun openQuickTool(toolName: String) {
        when (toolName) {
            "Trim" -> {
                _currentTab.value = MainTab.VIDEO
                _videoState.update { it.copy(activeTool = VideoTool.SPLIT, selectedTrackIndex = 0) }
            }
            "Filter" -> {
                _currentTab.value = MainTab.VIDEO
                _videoState.update { it.copy(activeTool = VideoTool.FILTER) }
            }
            "Text" -> {
                _currentTab.value = MainTab.VIDEO
                _videoState.update {
                    it.copy(
                        activeTool = VideoTool.TEXT,
                        selectedTrackIndex = 1,
                        selectedClipId = it.textOverlays.firstOrNull()?.id ?: ""
                    )
                }
            }
            "Music" -> {
                _currentTab.value = MainTab.VIDEO
                _videoState.update { it.copy(activeTool = VideoTool.MUSIC, selectedTrackIndex = 3) }
            }
            "Speed" -> {
                _currentTab.value = MainTab.VIDEO
                _videoState.update { it.copy(activeTool = VideoTool.SPEED, selectedTrackIndex = 0) }
            }
            "Effect" -> {
                _currentTab.value = MainTab.VIDEO
                _videoState.update { it.copy(activeTool = VideoTool.FX, selectedTrackIndex = 2) }
            }
            "Beauty" -> {
                _currentTab.value = MainTab.PHOTO
                _photoState.update { it.copy(activeTool = PhotoTool.BEAUTY) }
            }
            "AI" -> {
                _currentTab.value = MainTab.WANAS_AI
            }
        }
    }

    fun createNewProject(title: String = "WS_Project_${(100..999).random()}", videoUri: String? = null) {
        viewModelScope.launch {
            val newEntity = VideoProjectEntity(
                title = title,
                aspectRatio = "16:9",
                resolution = "4K 60FPS",
                durationSec = 16.0f,
                videoUri = videoUri,
                filterId = "cyber_neon",
                playbackSpeed = 1.0f,
                customFontCount = customFonts.value.size,
                clipsCount = 4
            )
            repository.insertProject(newEntity)
            _videoState.update {
                it.copy(
                    projectTitle = title,
                    selectedVideoUri = videoUri,
                    playheadSec = 0f,
                    isPlaying = false,
                    statusBannerMessage = "Project '$title' loaded on timeline"
                )
            }
            _currentTab.value = MainTab.VIDEO
        }
    }

    fun openExistingProject(project: VideoProjectEntity) {
        _videoState.update {
            it.copy(
                projectTitle = project.title,
                aspectRatio = project.aspectRatio,
                selectedVideoUri = project.videoUri,
                totalDurationSec = project.durationSec,
                activeFilterId = project.filterId,
                playbackSpeed = project.playbackSpeed,
                playheadSec = 0f,
                isPlaying = false,
                statusBannerMessage = "Opened '${project.title}'"
            )
        }
        _currentTab.value = MainTab.VIDEO
    }

    fun deleteProject(projectId: Int) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
        }
    }

    // --- VIDEO EDITOR ACTIONS ---

    fun onVideoPickedFromGallery(uri: Uri) {
        val uriString = uri.toString()
        _videoState.update { state ->
            val updatedClips = if (state.videoClips.isEmpty()) {
                listOf(
                    VideoClip(
                        id = "vid_${System.currentTimeMillis()}",
                        name = "Gallery_Clip_4K.mp4",
                        startSec = 0f,
                        endSec = state.totalDurationSec,
                        speed = state.playbackSpeed,
                        volume = 1.0f,
                        videoUri = uriString
                    )
                )
            } else {
                state.videoClips.mapIndexed { idx, clip ->
                    if (idx == 0) clip.copy(name = "Gallery_Video_01.mp4", videoUri = uriString) else clip
                }
            }
            state.copy(
                selectedVideoUri = uriString,
                videoClips = updatedClips,
                playheadSec = 0f,
                statusBannerMessage = "Video imported from device gallery"
            )
        }
        _currentTab.value = MainTab.VIDEO
    }

    fun togglePlayback() {
        val nextPlaying = !_videoState.value.isPlaying
        _videoState.update { it.copy(isPlaying = nextPlaying) }
        playbackJob?.cancel()
        if (nextPlaying) {
            playbackJob = viewModelScope.launch {
                while (_videoState.value.isPlaying) {
                    delay(50L)
                    _videoState.update { state ->
                        val step = 0.05f * state.playbackSpeed.coerceIn(0.2f, 8.0f)
                        val nextSec = state.playheadSec + step
                        if (nextSec >= state.totalDurationSec) {
                            state.copy(playheadSec = 0f, isPlaying = false)
                        } else {
                            state.copy(playheadSec = nextSec)
                        }
                    }
                }
            }
        }
    }

    fun seekPlayhead(seconds: Float) {
        _videoState.update { state ->
            state.copy(playheadSec = seconds.coerceIn(0f, state.totalDurationSec))
        }
    }

    fun selectVideoTool(tool: VideoTool) {
        _videoState.update { state ->
            val targetTrack = when (tool) {
                VideoTool.SPLIT, VideoTool.SPEED, VideoTool.VOLUME -> 0
                VideoTool.TEXT -> 1
                VideoTool.STICKER, VideoTool.FX -> 2
                VideoTool.MUSIC -> 3
                else -> state.selectedTrackIndex
            }
            state.copy(activeTool = tool, selectedTrackIndex = targetTrack)
        }
    }

    fun selectTimelineClip(trackIndex: Int, clipId: String) {
        _videoState.update { state ->
            val matchingTool = when (trackIndex) {
                0 -> if (state.activeTool in listOf(VideoTool.SPLIT, VideoTool.SPEED, VideoTool.VOLUME)) state.activeTool else VideoTool.SPLIT
                1 -> VideoTool.TEXT
                2 -> VideoTool.FX
                3 -> VideoTool.MUSIC
                else -> state.activeTool
            }
            state.copy(
                selectedTrackIndex = trackIndex,
                selectedClipId = clipId,
                activeTool = matchingTool
            )
        }
    }

    fun cycleAspectRatio() {
        val ratios = listOf("16:9", "9:16", "1:1", "4:5")
        _videoState.update { state ->
            val nextIdx = (ratios.indexOf(state.aspectRatio) + 1) % ratios.size
            state.copy(aspectRatio = ratios[nextIdx])
        }
    }

    fun splitClipAtPlayhead() {
        _videoState.update { state ->
            val playhead = state.playheadSec
            when (state.selectedTrackIndex) {
                0 -> {
                    val target = state.videoClips.firstOrNull { playhead > it.startSec + 0.6f && playhead < it.endSec - 0.6f }
                        ?: return@update state.copy(statusBannerMessage = "Move playhead inside a video clip to split")
                    val left = target.copy(id = "${target.id}_A", name = "${target.name.substringBeforeLast(".")}_Pt1.mp4", endSec = playhead)
                    val right = target.copy(id = "vid_${System.currentTimeMillis()}", name = "${target.name.substringBeforeLast(".")}_Pt2.mp4", startSec = playhead)
                    val updated = state.videoClips.flatMap { if (it.id == target.id) listOf(left, right) else listOf(it) }
                    state.copy(
                        videoClips = updated,
                        selectedClipId = right.id,
                        statusBannerMessage = "Split video clip at ${formatSec(playhead)}"
                    )
                }
                1 -> {
                    val target = state.textOverlays.firstOrNull { playhead > it.startSec + 0.5f && playhead < it.endSec - 0.5f }
                        ?: return@update state.copy(statusBannerMessage = "Move playhead inside a text clip to split")
                    val left = target.copy(id = "${target.id}_A", endSec = playhead)
                    val right = target.copy(id = "txt_${System.currentTimeMillis()}", startSec = playhead)
                    val updated = state.textOverlays.flatMap { if (it.id == target.id) listOf(left, right) else listOf(it) }
                    state.copy(
                        textOverlays = updated,
                        selectedClipId = right.id,
                        statusBannerMessage = "Split text layer at ${formatSec(playhead)}"
                    )
                }
                2 -> {
                    val target = state.stickerFxClips.firstOrNull { playhead > it.startSec + 0.5f && playhead < it.endSec - 0.5f }
                        ?: return@update state.copy(statusBannerMessage = "Move playhead inside an FX/Sticker clip to split")
                    val left = target.copy(id = "${target.id}_A", endSec = playhead)
                    val right = target.copy(id = "fx_${System.currentTimeMillis()}", startSec = playhead)
                    val updated = state.stickerFxClips.flatMap { if (it.id == target.id) listOf(left, right) else listOf(it) }
                    state.copy(
                        stickerFxClips = updated,
                        selectedClipId = right.id,
                        statusBannerMessage = "Split FX layer at ${formatSec(playhead)}"
                    )
                }
                else -> state.copy(statusBannerMessage = "Split applied at ${formatSec(playhead)}")
            }
        }
    }

    fun trimSelectedClip(deltaStartSec: Float, deltaEndSec: Float) {
        _videoState.update { state ->
            val maxDur = state.totalDurationSec
            when (state.selectedTrackIndex) {
                0 -> {
                    val updated = state.videoClips.map { clip ->
                        if (clip.id == state.selectedClipId) {
                            val newStart = (clip.startSec + deltaStartSec).coerceIn(0f, clip.endSec - 0.8f)
                            val newEnd = (clip.endSec + deltaEndSec).coerceIn(newStart + 0.8f, maxDur)
                            clip.copy(startSec = newStart, endSec = newEnd)
                        } else clip
                    }
                    state.copy(videoClips = updated)
                }
                1 -> {
                    val updated = state.textOverlays.map { txt ->
                        if (txt.id == state.selectedClipId) {
                            val newStart = (txt.startSec + deltaStartSec).coerceIn(0f, txt.endSec - 0.8f)
                            val newEnd = (txt.endSec + deltaEndSec).coerceIn(newStart + 0.8f, maxDur)
                            txt.copy(startSec = newStart, endSec = newEnd)
                        } else txt
                    }
                    state.copy(textOverlays = updated)
                }
                2 -> {
                    val updated = state.stickerFxClips.map { fx ->
                        if (fx.id == state.selectedClipId) {
                            val newStart = (fx.startSec + deltaStartSec).coerceIn(0f, fx.endSec - 0.8f)
                            val newEnd = (fx.endSec + deltaEndSec).coerceIn(newStart + 0.8f, maxDur)
                            fx.copy(startSec = newStart, endSec = newEnd)
                        } else fx
                    }
                    state.copy(stickerFxClips = updated)
                }
                3 -> {
                    val updated = state.audioClips.map { aud ->
                        if (aud.id == state.selectedClipId) {
                            val newStart = (aud.startSec + deltaStartSec).coerceIn(0f, aud.endSec - 0.8f)
                            val newEnd = (aud.endSec + deltaEndSec).coerceIn(newStart + 0.8f, maxDur)
                            aud.copy(startSec = newStart, endSec = newEnd)
                        } else aud
                    }
                    state.copy(audioClips = updated)
                }
                else -> state
            }
        }
    }

    fun deleteSelectedClip() {
        _videoState.update { state ->
            when (state.selectedTrackIndex) {
                0 -> {
                    if (state.videoClips.size <= 1) {
                        return@update state.copy(statusBannerMessage = "At least 1 main video clip is required")
                    }
                    val remaining = state.videoClips.filterNot { it.id == state.selectedClipId }
                    state.copy(
                        videoClips = remaining,
                        selectedClipId = remaining.first().id,
                        statusBannerMessage = "Video segment removed"
                    )
                }
                1 -> {
                    val remaining = state.textOverlays.filterNot { it.id == state.selectedClipId }
                    state.copy(
                        textOverlays = remaining,
                        selectedClipId = remaining.firstOrNull()?.id ?: "",
                        statusBannerMessage = "Text overlay removed"
                    )
                }
                2 -> {
                    val remaining = state.stickerFxClips.filterNot { it.id == state.selectedClipId }
                    state.copy(
                        stickerFxClips = remaining,
                        selectedClipId = remaining.firstOrNull()?.id ?: "",
                        statusBannerMessage = "FX/Sticker clip removed"
                    )
                }
                else -> state
            }
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        val clamped = speed.coerceIn(0.2f, 8.0f)
        _videoState.update { state ->
            val updatedClips = state.videoClips.map { clip ->
                if (clip.id == state.selectedClipId) clip.copy(speed = clamped) else clip
            }
            state.copy(
                playbackSpeed = clamped,
                videoClips = updatedClips,
                statusBannerMessage = "Playback speed set to ${String.format("%.1fx", clamped)}"
            )
        }
    }

    fun setMasterVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 2.0f)
        _videoState.update { state ->
            state.copy(masterVolume = clamped)
        }
    }

    fun setVideoFilter(filterId: String) {
        _videoState.update { state ->
            state.copy(activeFilterId = filterId)
        }
    }

    // --- TEXT OVERLAY & CUSTOM FONT UPLOAD ---

    fun addTextOverlay(initialText: String = "NEW STUDIO TEXT") {
        _videoState.update { state ->
            val start = state.playheadSec.coerceIn(0f, (state.totalDurationSec - 3f).coerceAtLeast(0f))
            val end = (start + 5.0f).coerceAtMost(state.totalDurationSec)
            val customFont = customFonts.value.firstOrNull()
            val newItem = TextOverlayItem(
                id = "txt_${System.currentTimeMillis()}",
                text = initialText,
                fontId = customFont?.fontId ?: "space_grotesk",
                fontName = customFont?.displayName ?: "Space Grotesk",
                customFontPath = customFont?.filePath,
                colorHex = 0xFF00D9FF,
                fontSizeSp = 26f,
                startSec = start,
                endSec = end,
                offsetXPercent = 0.5f,
                offsetYPercent = (0.35f + (state.textOverlays.size % 3) * 0.18f).coerceIn(0.2f, 0.85f),
                hasGlow = true,
                hasBgBox = true
            )
            state.copy(
                textOverlays = state.textOverlays + newItem,
                selectedTrackIndex = 1,
                selectedClipId = newItem.id,
                activeTool = VideoTool.TEXT,
                statusBannerMessage = "Added text overlay (${newItem.fontName})"
            )
        }
    }

    fun updateSelectedTextOverlay(
        text: String? = null,
        colorHex: Long? = null,
        fontSizeSp: Float? = null,
        hasGlow: Boolean? = null,
        hasBgBox: Boolean? = null,
        offsetXPercent: Float? = null,
        offsetYPercent: Float? = null
    ) {
        _videoState.update { state ->
            val targetId = state.selectedClipId.ifEmpty { state.textOverlays.firstOrNull()?.id ?: "" }
            val updated = state.textOverlays.map { item ->
                if (item.id == targetId) {
                    item.copy(
                        text = text ?: item.text,
                        colorHex = colorHex ?: item.colorHex,
                        fontSizeSp = fontSizeSp ?: item.fontSizeSp,
                        hasGlow = hasGlow ?: item.hasGlow,
                        hasBgBox = hasBgBox ?: item.hasBgBox,
                        offsetXPercent = (offsetXPercent ?: item.offsetXPercent).coerceIn(0.1f, 0.9f),
                        offsetYPercent = (offsetYPercent ?: item.offsetYPercent).coerceIn(0.1f, 0.9f)
                    )
                } else item
            }
            state.copy(textOverlays = updated, selectedClipId = targetId)
        }
    }

    fun applyBuiltInFontToSelectedText(fontId: String, displayName: String) {
        _videoState.update { state ->
            val targetId = state.selectedClipId.ifEmpty { state.textOverlays.firstOrNull()?.id ?: "" }
            val updated = state.textOverlays.map { item ->
                if (item.id == targetId) {
                    item.copy(fontId = fontId, fontName = displayName, customFontPath = null)
                } else item
            }
            state.copy(
                textOverlays = updated,
                selectedClipId = targetId,
                statusBannerMessage = "Font changed to $displayName"
            )
        }
    }

    fun applyCustomFontToSelectedText(customFont: CustomFontEntity) {
        _videoState.update { state ->
            val targetId = state.selectedClipId.ifEmpty { state.textOverlays.firstOrNull()?.id ?: "" }
            val updated = if (state.textOverlays.isEmpty()) {
                listOf(
                    TextOverlayItem(
                        id = "txt_${System.currentTimeMillis()}",
                        text = "CUSTOM FONT TEXT",
                        fontId = customFont.fontId,
                        fontName = customFont.displayName,
                        customFontPath = customFont.filePath
                    )
                )
            } else {
                state.textOverlays.map { item ->
                    if (item.id == targetId) {
                        item.copy(
                            fontId = customFont.fontId,
                            fontName = customFont.displayName,
                            customFontPath = customFont.filePath
                        )
                    } else item
                }
            }
            state.copy(
                textOverlays = updated,
                selectedTrackIndex = 1,
                selectedClipId = updated.firstOrNull { it.fontId == customFont.fontId }?.id ?: targetId,
                statusBannerMessage = "Applied custom font '${customFont.displayName}' to text overlay"
            )
        }
    }

    fun uploadCustomFontFromDeviceUri(uri: Uri) {
        viewModelScope.launch {
            val result = repository.importCustomFontFromUri(uri)
            result.onSuccess { entity ->
                // Pre-warm Compose FontFamily cache
                FontRegistry.resolveFontFamily(entity.fontId, entity.filePath)
                applyCustomFontToSelectedText(entity)
            }.onFailure { err ->
                _videoState.update {
                    it.copy(statusBannerMessage = "Font upload error: ${err.message ?: "Invalid font file"}")
                }
            }
        }
    }

    fun importSampleCustomFont(assetFileName: String, displayName: String) {
        viewModelScope.launch {
            val result = repository.importSampleFontFromAssets(assetFileName, displayName)
            result.onSuccess { entity ->
                FontRegistry.resolveFontFamily(entity.fontId, entity.filePath)
                applyCustomFontToSelectedText(entity)
            }.onFailure { err ->
                _videoState.update {
                    it.copy(statusBannerMessage = "Import failed: ${err.message}")
                }
            }
        }
    }

    fun deleteCustomFont(font: CustomFontEntity) {
        viewModelScope.launch {
            repository.deleteCustomFont(font)
            _videoState.update { state ->
                val updated = state.textOverlays.map { txt ->
                    if (txt.customFontPath == font.filePath) {
                        txt.copy(
                            fontId = "space_grotesk",
                            fontName = "Space Grotesk",
                            customFontPath = null
                        )
                    } else txt
                }
                state.copy(
                    textOverlays = updated,
                    statusBannerMessage = "Removed custom font '${font.displayName}'"
                )
            }
        }
    }

    // --- FX, STICKER, AND MUSIC ---

    fun addStickerOrFx(label: String, badge: String, isFx: Boolean) {
        _videoState.update { state ->
            val start = state.playheadSec.coerceIn(0f, (state.totalDurationSec - 2.5f).coerceAtLeast(0f))
            val end = (start + 4.5f).coerceAtMost(state.totalDurationSec)
            val newClip = StickerFxClip(
                id = "fx_${System.currentTimeMillis()}",
                label = label,
                badge = badge,
                startSec = start,
                endSec = end,
                isFx = isFx
            )
            state.copy(
                stickerFxClips = state.stickerFxClips + newClip,
                selectedTrackIndex = 2,
                selectedClipId = newClip.id,
                statusBannerMessage = "Added '$label' to timeline"
            )
        }
    }

    fun selectMusicTrack(trackName: String, artist: String, durationSec: Float) {
        _videoState.update { state ->
            val newAudio = AudioTrackClip(
                id = "aud_${System.currentTimeMillis()}",
                trackName = trackName,
                artist = artist,
                startSec = 0f,
                endSec = durationSec.coerceAtMost(state.totalDurationSec),
                volume = 0.9f
            )
            state.copy(
                audioClips = listOf(newAudio),
                selectedTrackIndex = 3,
                selectedClipId = newAudio.id,
                statusBannerMessage = "Music track set to '$trackName'"
            )
        }
    }

    // --- EXPORT 4K ---

    fun showExportDialog(show: Boolean) {
        _videoState.update {
            it.copy(
                isExportDialogVisible = show,
                isExporting = false,
                exportProgress = 0f,
                exportCompletedMessage = null
            )
        }
    }

    fun startExport4K(resolutionLabel: String = "4K UHD (3840×2160) • 60 FPS") {
        viewModelScope.launch {
            _videoState.update {
                it.copy(isExporting = true, exportProgress = 0.05f, exportCompletedMessage = null)
            }
            for (step in 1..20) {
                delay(90L)
                _videoState.update { it.copy(exportProgress = step / 20f) }
            }
            val current = _videoState.value
            repository.insertProject(
                VideoProjectEntity(
                    title = current.projectTitle,
                    aspectRatio = current.aspectRatio,
                    resolution = resolutionLabel,
                    durationSec = current.totalDurationSec,
                    videoUri = current.selectedVideoUri,
                    filterId = current.activeFilterId,
                    playbackSpeed = current.playbackSpeed,
                    volume = current.masterVolume,
                    musicTrackName = current.audioClips.firstOrNull()?.trackName ?: "None",
                    customFontCount = current.textOverlays.count { !it.customFontPath.isNullOrBlank() },
                    clipsCount = current.videoClips.size + current.textOverlays.size + current.stickerFxClips.size
                )
            )
            _videoState.update {
                it.copy(
                    isExporting = false,
                    exportProgress = 1f,
                    exportCompletedMessage = "Exported ${current.projectTitle}.mp4 in $resolutionLabel and saved to My Projects!"
                )
            }
        }
    }

    fun clearStatusBanner() {
        _videoState.update { it.copy(statusBannerMessage = null) }
    }

    // --- PHOTO EDITOR ACTIONS ---

    fun onPhotoPickedFromGallery(uri: Uri) {
        _photoState.update {
            it.copy(
                selectedPhotoUri = uri.toString(),
                photoSavedBanner = "Photo loaded from gallery"
            )
        }
        _currentTab.value = MainTab.PHOTO
    }

    fun selectPhotoTool(tool: PhotoTool) {
        _photoState.update { it.copy(activeTool = tool) }
    }

    fun setPhotoFilter(filterId: String) {
        _photoState.update { it.copy(activeFilterId = filterId) }
    }

    fun updatePhotoBeauty(smooth: Float? = null, neonRim: Float? = null, sharpness: Float? = null) {
        _photoState.update {
            it.copy(
                beautySmooth = smooth ?: it.beautySmooth,
                beautyNeonRim = neonRim ?: it.beautyNeonRim,
                beautySharpness = sharpness ?: it.beautySharpness
            )
        }
    }

    fun togglePhotoBgRemove(enabled: Boolean? = null, backdropMode: String? = null) {
        _photoState.update {
            it.copy(
                isBgRemoved = enabled ?: !it.isBgRemoved,
                bgBackdropMode = backdropMode ?: it.bgBackdropMode
            )
        }
    }

    fun updatePhotoAdjustments(
        brightness: Float? = null,
        contrast: Float? = null,
        saturation: Float? = null,
        warmth: Float? = null
    ) {
        _photoState.update {
            it.copy(
                brightness = brightness ?: it.brightness,
                contrast = contrast ?: it.contrast,
                saturation = saturation ?: it.saturation,
                warmth = warmth ?: it.warmth
            )
        }
    }

    fun resetPhotoEdits() {
        _photoState.update {
            PhotoEditorUiState(selectedPhotoUri = it.selectedPhotoUri, activeTool = it.activeTool)
        }
    }

    fun setComparingOriginal(comparing: Boolean) {
        _photoState.update { it.copy(isComparingOriginal = comparing) }
    }

    fun saveEditedPhoto() {
        _photoState.update {
            it.copy(photoSavedBanner = "Saved 4K Photo Preset to WS-Editor Studio Gallery!")
        }
    }

    private fun formatSec(sec: Float): String = String.format("%.1fs", sec)

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = WsEditorDatabase.getInstance(context)
                    val repo = WsEditorRepository(
                        context = context.applicationContext,
                        projectDao = db.projectDao(),
                        customFontDao = db.customFontDao()
                    )
                    return WsEditorViewModel(repo) as T
                }
            }
    }
}
