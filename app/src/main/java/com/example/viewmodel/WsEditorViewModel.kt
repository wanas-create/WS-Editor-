package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiEngine
import com.example.data.ProjectEntity
import com.example.data.ProjectRepository
import com.example.data.WsDatabase
import com.example.media.MediaExportEngine
import com.example.media.VideoFrameProvider
import com.example.media.VoiceRecorder
import com.example.model.AspectRatioOption
import com.example.model.AudioTrackItem
import com.example.model.DrawingPath
import com.example.model.ExportConfig
import com.example.model.FilterPreset
import com.example.model.PhotoEditState
import com.example.model.PhotoTextOverlay
import com.example.model.SubtitleItem
import com.example.model.TransitionType
import com.example.model.VideoClip
import com.example.monetization.MonetizationManager
import com.example.model.VisualEffectPreset
import com.example.util.MediaStorageHelper
import com.example.util.ProjectJsonHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

enum class ScreenState {
    HOME,
    VIDEO_EDITOR,
    PHOTO_EDITOR,
    AI_TOOLS,
    SETTINGS
}

enum class VideoEditorTool {
    NONE,
    TRIM_SPLIT,
    SPEED,
    TRANSFORM,
    EFFECTS,
    FILTERS,
    ADJUST,
    AUDIO,
    TEXT_SUBTITLES,
    TRANSITION,
    PIP,
    CHROMA_KEY,
    AI_EFFECTS
}

enum class PhotoEditorTool {
    NONE,
    CROP_TRANSFORM,
    EFFECTS,
    FILTERS,
    ADJUST,
    DRAW,
    TEXT_OVERLAY,
    FRAMES,
    AI_TOOLS
}

data class VideoEditorSnapshot(
    val clips: List<VideoClip>,
    val selectedIndex: Int,
    val audioTracks: List<AudioTrackItem>,
    val subtitles: List<SubtitleItem>,
    val aspectRatio: AspectRatioOption
)

data class PhotoEditorSnapshot(
    val state: PhotoEditState,
    val drawings: List<DrawingPath>
)

class WsEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProjectRepository = ProjectRepository(
        WsDatabase.getDatabase(application).projectDao()
    )

    val monetization = MonetizationManager.instance
    val voiceRecorder = VoiceRecorder(application)

    // Navigation State
    private val _currentScreen = MutableStateFlow(ScreenState.HOME)
    val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

    // Projects Data
    val recentProjects: StateFlow<List<ProjectEntity>> = repository.recentProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val draftProjects: StateFlow<List<ProjectEntity>> = repository.draftProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deletedProjects: StateFlow<List<ProjectEntity>> = repository.deletedProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Project
    private val _activeProject = MutableStateFlow<ProjectEntity?>(null)
    val activeProject: StateFlow<ProjectEntity?> = _activeProject.asStateFlow()

    // Video Editing State
    private val _videoClips = MutableStateFlow<List<VideoClip>>(emptyList())
    val videoClips: StateFlow<List<VideoClip>> = _videoClips.asStateFlow()

    private val _selectedClipIndex = MutableStateFlow(0)
    val selectedClipIndex: StateFlow<Int> = _selectedClipIndex.asStateFlow()

    private val _aspectRatio = MutableStateFlow(AspectRatioOption.RATIO_16_9)
    val aspectRatio: StateFlow<AspectRatioOption> = _aspectRatio.asStateFlow()

    private val _playheadMs = MutableStateFlow(0L)
    val playheadMs: StateFlow<Long> = _playheadMs.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _activeVideoTool = MutableStateFlow(VideoEditorTool.NONE)
    val activeVideoTool: StateFlow<VideoEditorTool> = _activeVideoTool.asStateFlow()

    private val _audioTracks = MutableStateFlow<List<AudioTrackItem>>(emptyList())
    val audioTracks: StateFlow<List<AudioTrackItem>> = _audioTracks.asStateFlow()

    private val _subtitles = MutableStateFlow<List<SubtitleItem>>(emptyList())
    val subtitles: StateFlow<List<SubtitleItem>> = _subtitles.asStateFlow()

    // Undo / Redo & Auto-Save
    private val undoStack = mutableListOf<VideoEditorSnapshot>()
    private val redoStack = mutableListOf<VideoEditorSnapshot>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    private var autoSaveJob: Job? = null
    fun autoSaveCurrentProject() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(400)
            saveCurrentProject(asDraft = true)
        }
    }

    fun pushUndoState() {
        val snapshot = VideoEditorSnapshot(
            clips = _videoClips.value,
            selectedIndex = _selectedClipIndex.value,
            audioTracks = _audioTracks.value,
            subtitles = _subtitles.value,
            aspectRatio = _aspectRatio.value
        )
        undoStack.add(snapshot)
        if (undoStack.size > 25) undoStack.removeAt(0)
        redoStack.clear()
        _canUndo.value = true
        _canRedo.value = false
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val current = VideoEditorSnapshot(
                clips = _videoClips.value,
                selectedIndex = _selectedClipIndex.value,
                audioTracks = _audioTracks.value,
                subtitles = _subtitles.value,
                aspectRatio = _aspectRatio.value
            )
            redoStack.add(current)
            val previous = undoStack.removeAt(undoStack.lastIndex)
            _videoClips.value = previous.clips
            _selectedClipIndex.value = previous.selectedIndex.coerceIn(0, (previous.clips.size - 1).coerceAtLeast(0))
            _audioTracks.value = previous.audioTracks
            _subtitles.value = previous.subtitles
            _aspectRatio.value = previous.aspectRatio
            _canUndo.value = undoStack.isNotEmpty()
            _canRedo.value = true
            autoSaveCurrentProject()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val current = VideoEditorSnapshot(
                clips = _videoClips.value,
                selectedIndex = _selectedClipIndex.value,
                audioTracks = _audioTracks.value,
                subtitles = _subtitles.value,
                aspectRatio = _aspectRatio.value
            )
            undoStack.add(current)
            val next = redoStack.removeAt(redoStack.lastIndex)
            _videoClips.value = next.clips
            _selectedClipIndex.value = next.selectedIndex.coerceIn(0, (next.clips.size - 1).coerceAtLeast(0))
            _audioTracks.value = next.audioTracks
            _subtitles.value = next.subtitles
            _aspectRatio.value = next.aspectRatio
            _canUndo.value = true
            _canRedo.value = redoStack.isNotEmpty()
            autoSaveCurrentProject()
        }
    }

    // Photo Editing State
    private val _photoEditState = MutableStateFlow<PhotoEditState?>(null)
    val photoEditState: StateFlow<PhotoEditState?> = _photoEditState.asStateFlow()

    private val _activePhotoTool = MutableStateFlow(PhotoEditorTool.NONE)
    val activePhotoTool: StateFlow<PhotoEditorTool> = _activePhotoTool.asStateFlow()

    private val _drawingPaths = MutableStateFlow<List<DrawingPath>>(emptyList())
    val drawingPaths: StateFlow<List<DrawingPath>> = _drawingPaths.asStateFlow()

    // Export State
    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _exportProgress = MutableStateFlow(0)
    val exportProgress: StateFlow<Int> = _exportProgress.asStateFlow()

    private val _exportedFile = MutableStateFlow<File?>(null)
    val exportedFile: StateFlow<File?> = _exportedFile.asStateFlow()

    private val _showExportDialog = MutableStateFlow(false)
    val showExportDialog: StateFlow<Boolean> = _showExportDialog.asStateFlow()

    // AI Tools State
    private val _aiProcessing = MutableStateFlow(false)
    val aiProcessing: StateFlow<Boolean> = _aiProcessing.asStateFlow()

    private val _aiResultUri = MutableStateFlow<String?>(null)
    val aiResultUri: StateFlow<String?> = _aiResultUri.asStateFlow()

    private val _aiGeneratedImages = MutableStateFlow<List<String>>(emptyList())
    val aiGeneratedImages: StateFlow<List<String>> = _aiGeneratedImages.asStateFlow()

    private val _aiBgCutoutUri = MutableStateFlow<String?>(null)
    val aiBgCutoutUri: StateFlow<String?> = _aiBgCutoutUri.asStateFlow()

    private val _aiBgGeneratedUri = MutableStateFlow<String?>(null)
    val aiBgGeneratedUri: StateFlow<String?> = _aiBgGeneratedUri.asStateFlow()

    private val _aiVoiceAudioUri = MutableStateFlow<String?>(null)
    val aiVoiceAudioUri: StateFlow<String?> = _aiVoiceAudioUri.asStateFlow()

    private val _aiVideoGeneratedUri = MutableStateFlow<String?>(null)
    val aiVideoGeneratedUri: StateFlow<String?> = _aiVideoGeneratedUri.asStateFlow()

    private val _aiEnhancedImageUri = MutableStateFlow<String?>(null)
    val aiEnhancedImageUri: StateFlow<String?> = _aiEnhancedImageUri.asStateFlow()

    private val _aiEnhancedOriginalUri = MutableStateFlow<String?>(null)
    val aiEnhancedOriginalUri: StateFlow<String?> = _aiEnhancedOriginalUri.asStateFlow()

    private val _aiCaptionsList = MutableStateFlow<List<SubtitleItem>>(emptyList())
    val aiCaptionsList: StateFlow<List<SubtitleItem>> = _aiCaptionsList.asStateFlow()

    private val _aiError = MutableStateFlow<String?>(null)
    val aiError: StateFlow<String?> = _aiError.asStateFlow()

    private val _aiProgress = MutableStateFlow(0)
    val aiProgress: StateFlow<Int> = _aiProgress.asStateFlow()

    private var playbackJob: Job? = null

    val totalVideoDurationMs: Long
        get() = _videoClips.value.sumOf { it.effectiveDurationMs }

    fun navigateTo(screen: ScreenState) {
        pausePlayback()
        _currentScreen.value = screen
    }

    // ----------------------------------------------------
    // PROJECT MANAGEMENT
    // ----------------------------------------------------

    fun createVideoProject(uris: List<Uri>, projectName: String = "WS Video Project") {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val persistentClips = uris.mapIndexed { index, uri ->
                val localPath = MediaStorageHelper.persistMediaLocally(context, uri, "vid_clip_${index + 1}")
                val realDuration = VideoFrameProvider.getVideoDurationMs(context, localPath)
                val duration = if (realDuration > 0L) realDuration else 10000L
                VideoClip(
                    uriString = localPath,
                    name = "Clip ${index + 1}",
                    originalDurationMs = duration,
                    trimStartMs = 0L,
                    trimEndMs = duration
                )
            }
            _videoClips.value = persistentClips
            _selectedClipIndex.value = 0
            _playheadMs.value = 0L
            _audioTracks.value = emptyList()
            _subtitles.value = emptyList()
            _aspectRatio.value = AspectRatioOption.RATIO_16_9

            val totalDur = persistentClips.sumOf { it.effectiveDurationMs }
            val thumb = persistentClips.firstOrNull()?.uriString
            val projectData = ProjectJsonHelper.serializeVideoProject(
                clips = persistentClips,
                audioTracks = emptyList(),
                subtitles = emptyList(),
                aspectRatio = AspectRatioOption.RATIO_16_9,
                playheadMs = 0L
            )
            val newEntity = ProjectEntity(
                title = projectName,
                type = "VIDEO",
                thumbnailUri = thumb,
                aspectRatio = "16:9",
                durationMs = totalDur,
                isDraft = false,
                mediaUrisJson = org.json.JSONArray(persistentClips.map { it.uriString }).toString(),
                projectDataJson = projectData
            )
            val id = repository.saveProject(newEntity)
            _activeProject.value = newEntity.copy(id = id)
            _currentScreen.value = ScreenState.VIDEO_EDITOR
        }
    }

    fun createPhotoProject(uri: Uri, projectName: String = "WS Photo Project") {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val localPath = MediaStorageHelper.persistMediaLocally(context, uri, "photo_project")
            val photoState = PhotoEditState(uriString = localPath, originalUriString = localPath)
            _photoEditState.value = photoState
            _drawingPaths.value = emptyList()

            val projectData = ProjectJsonHelper.serializePhotoProject(photoState, emptyList())
            val newEntity = ProjectEntity(
                title = projectName,
                type = "PHOTO",
                thumbnailUri = localPath,
                aspectRatio = "1:1",
                isDraft = false,
                mediaUrisJson = org.json.JSONArray(listOf(localPath)).toString(),
                projectDataJson = projectData
            )
            val id = repository.saveProject(newEntity)
            _activeProject.value = newEntity.copy(id = id)
            _currentScreen.value = ScreenState.PHOTO_EDITOR
        }
    }

    fun openExistingProject(project: ProjectEntity) {
        viewModelScope.launch {
            _activeProject.value = project
            val context = getApplication<Application>()
            if (project.type == "VIDEO") {
                val parsed = ProjectJsonHelper.deserializeVideoProject(project.projectDataJson)
                if (parsed != null && parsed.clips.isNotEmpty()) {
                    _videoClips.value = parsed.clips
                    _audioTracks.value = parsed.audioTracks
                    _subtitles.value = parsed.subtitles
                    _aspectRatio.value = parsed.aspectRatio
                    _selectedClipIndex.value = 0
                    _playheadMs.value = parsed.playheadMs.coerceIn(0L, parsed.clips.sumOf { it.effectiveDurationMs }.coerceAtLeast(100L))
                } else {
                    // Fallback to thumbnail uri or media list
                    val uriToUse = project.thumbnailUri ?: ""
                    val realDur = if (project.durationMs > 0L) {
                        project.durationMs
                    } else {
                        VideoFrameProvider.getVideoDurationMs(context, uriToUse)
                    }
                    val finalDuration = if (realDur > 0L) realDur else 10000L
                    val clip = VideoClip(
                        uriString = uriToUse,
                        name = project.title,
                        originalDurationMs = finalDuration,
                        trimStartMs = 0L,
                        trimEndMs = finalDuration
                    )
                    _videoClips.value = listOf(clip)
                    _selectedClipIndex.value = 0
                    _playheadMs.value = 0L
                    _audioTracks.value = emptyList()
                    _subtitles.value = emptyList()
                }
                _currentScreen.value = ScreenState.VIDEO_EDITOR
            } else {
                val parsed = ProjectJsonHelper.deserializePhotoProject(project.projectDataJson)
                if (parsed != null) {
                    _photoEditState.value = parsed.state
                    _drawingPaths.value = parsed.drawingPaths
                } else {
                    _photoEditState.value = PhotoEditState(
                        uriString = project.thumbnailUri ?: "",
                        originalUriString = project.thumbnailUri ?: ""
                    )
                    _drawingPaths.value = emptyList()
                }
                _currentScreen.value = ScreenState.PHOTO_EDITOR
            }
        }
    }

    fun saveCurrentProject(asDraft: Boolean = false) {
        val proj = _activeProject.value ?: return
        viewModelScope.launch {
            try {
                val updated = if (proj.type == "VIDEO") {
                    val clips = _videoClips.value
                    val thumb = clips.firstOrNull()?.uriString ?: proj.thumbnailUri
                    val dataJson = ProjectJsonHelper.serializeVideoProject(
                        clips = clips,
                        audioTracks = _audioTracks.value,
                        subtitles = _subtitles.value,
                        aspectRatio = _aspectRatio.value,
                        playheadMs = _playheadMs.value
                    )
                    val mediaJson = org.json.JSONArray(clips.map { it.uriString }).toString()
                    proj.copy(
                        updatedAt = System.currentTimeMillis(),
                        durationMs = totalVideoDurationMs,
                        thumbnailUri = thumb,
                        aspectRatio = _aspectRatio.value.label,
                        isDraft = asDraft,
                        mediaUrisJson = mediaJson,
                        projectDataJson = dataJson
                    )
                } else {
                    val state = _photoEditState.value ?: return@launch
                    val dataJson = ProjectJsonHelper.serializePhotoProject(
                        state = state,
                        drawingPaths = _drawingPaths.value
                    )
                    val mediaJson = org.json.JSONArray(listOf(state.uriString)).toString()
                    proj.copy(
                        updatedAt = System.currentTimeMillis(),
                        thumbnailUri = state.uriString,
                        aspectRatio = state.aspectRatio.label,
                        isDraft = asDraft,
                        mediaUrisJson = mediaJson,
                        projectDataJson = dataJson
                    )
                }
                repository.updateProject(updated)
                _activeProject.value = updated
            } catch (e: Throwable) {
                android.util.Log.e("WsEditorViewModel", "Failed saving project: ${e.message}", e)
            }
        }
    }

    fun renameProject(id: Long, newTitle: String) {
        viewModelScope.launch {
            repository.rename(id, newTitle)
        }
    }

    fun duplicateProject(id: Long) {
        viewModelScope.launch {
            repository.duplicateProject(id)
        }
    }

    fun softDeleteProject(id: Long) {
        viewModelScope.launch {
            repository.softDelete(id)
        }
    }

    fun restoreProject(id: Long) {
        viewModelScope.launch {
            repository.restore(id)
        }
    }

    fun permanentlyDeleteProject(id: Long) {
        viewModelScope.launch {
            repository.permanentlyDelete(id)
        }
    }

    // ----------------------------------------------------
    // VIDEO EDITOR CONTROLS & TIMELINE
    // ----------------------------------------------------

    fun setAspectRatio(ratio: AspectRatioOption) {
        pushUndoState()
        _aspectRatio.value = ratio
        autoSaveCurrentProject()
    }

    fun selectClip(index: Int) {
        if (index in _videoClips.value.indices) {
            _selectedClipIndex.value = index
        }
    }

    fun selectVideoTool(tool: VideoEditorTool) {
        _activeVideoTool.value = if (_activeVideoTool.value == tool) VideoEditorTool.NONE else tool
    }

    fun setPlayheadMs(ms: Long) {
        val total = totalVideoDurationMs.coerceAtLeast(100L)
        _playheadMs.value = ms.coerceIn(0L, total)
    }

    fun updatePlayheadFromPlayer(ms: Long) {
        val total = totalVideoDurationMs.coerceAtLeast(100L)
        _playheadMs.value = ms.coerceIn(0L, total)
    }

    fun setPlaying(playing: Boolean) {
        _isPlaying.value = playing
        if (!playing) {
            playbackJob?.cancel()
            playbackJob = null
        }
    }

    fun togglePlayback() {
        if (_isPlaying.value) {
            _isPlaying.value = false
        } else {
            if (_playheadMs.value >= totalVideoDurationMs - 100L) {
                _playheadMs.value = 0L
            }
            _isPlaying.value = true
        }
    }

    fun pausePlayback() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
    }

    // Split clip at current playhead
    fun splitClipAtPlayhead() {
        val clips = _videoClips.value.toMutableList()
        val currentPlayhead = _playheadMs.value
        var accum = 0L
        var targetIndex = -1
        var splitOffsetInClip = 0L

        for (i in clips.indices) {
            val clipDuration = clips[i].effectiveDurationMs
            if (currentPlayhead >= accum && currentPlayhead < accum + clipDuration) {
                targetIndex = i
                splitOffsetInClip = currentPlayhead - accum
                break
            }
            accum += clipDuration
        }

        if (targetIndex != -1 && splitOffsetInClip > 300L) {
            pushUndoState()
            val clip = clips[targetIndex]
            val splitMsOriginal = (splitOffsetInClip * clip.speed).toLong() + clip.trimStartMs

            val firstPart = clip.copy(
                id = java.util.UUID.randomUUID().toString(),
                name = "${clip.name} (Part 1)",
                trimEndMs = splitMsOriginal
            )
            val secondPart = clip.copy(
                id = java.util.UUID.randomUUID().toString(),
                name = "${clip.name} (Part 2)",
                trimStartMs = splitMsOriginal
            )

            clips.removeAt(targetIndex)
            clips.add(targetIndex, secondPart)
            clips.add(targetIndex, firstPart)

            _videoClips.value = clips
            _selectedClipIndex.value = targetIndex + 1
            autoSaveCurrentProject()
        }
    }

    fun trimSelectedClip(trimStartMs: Long, trimEndMs: Long) {
        val index = _selectedClipIndex.value
        val list = _videoClips.value.toMutableList()
        if (index in list.indices) {
            pushUndoState()
            list[index] = list[index].copy(
                trimStartMs = trimStartMs.coerceAtLeast(0L),
                trimEndMs = trimEndMs.coerceAtMost(list[index].originalDurationMs)
            )
            _videoClips.value = list
            autoSaveCurrentProject()
        }
    }

    fun setClipSpeed(speed: Float) {
        val index = _selectedClipIndex.value
        val list = _videoClips.value.toMutableList()
        if (index in list.indices) {
            pushUndoState()
            list[index] = list[index].copy(speed = speed)
            _videoClips.value = list
            autoSaveCurrentProject()
        }
    }

    fun setClipVolume(volume: Float) {
        val index = _selectedClipIndex.value
        val list = _videoClips.value.toMutableList()
        if (index in list.indices) {
            pushUndoState()
            list[index] = list[index].copy(
                volume = volume.coerceIn(0f, 2f),
                isMuted = volume == 0f
            )
            _videoClips.value = list
            autoSaveCurrentProject()
        }
    }

    fun toggleClipMute() {
        val index = _selectedClipIndex.value
        val list = _videoClips.value.toMutableList()
        if (index in list.indices) {
            pushUndoState()
            val current = list[index]
            val newMute = !current.isMuted
            list[index] = current.copy(
                isMuted = newMute,
                volume = if (newMute) 0f else 1.0f
            )
            _videoClips.value = list
            autoSaveCurrentProject()
        }
    }

    fun rotateSelectedClip() {
        val index = _selectedClipIndex.value
        val list = _videoClips.value.toMutableList()
        if (index in list.indices) {
            pushUndoState()
            val newRot = (list[index].rotationDegrees + 90) % 360
            list[index] = list[index].copy(rotationDegrees = newRot)
            _videoClips.value = list
            autoSaveCurrentProject()
        }
    }

    fun flipSelectedClipHorizontal() {
        val index = _selectedClipIndex.value
        val list = _videoClips.value.toMutableList()
        if (index in list.indices) {
            pushUndoState()
            list[index] = list[index].copy(isFlippedH = !list[index].isFlippedH)
            _videoClips.value = list
            autoSaveCurrentProject()
        }
    }

    fun flipSelectedClipVertical() {
        val index = _selectedClipIndex.value
        val list = _videoClips.value.toMutableList()
        if (index in list.indices) {
            pushUndoState()
            list[index] = list[index].copy(isFlippedV = !list[index].isFlippedV)
            _videoClips.value = list
            autoSaveCurrentProject()
        }
    }

    fun reverseSelectedClip() {
        val index = _selectedClipIndex.value
        val list = _videoClips.value.toMutableList()
        if (index in list.indices) {
            pushUndoState()
            list[index] = list[index].copy(isReversed = !list[index].isReversed)
            _videoClips.value = list
            autoSaveCurrentProject()
        }
    }

    fun applyFilterToSelectedClip(filter: FilterPreset) {
        val index = _selectedClipIndex.value
        val list = _videoClips.value.toMutableList()
        if (index in list.indices) {
            pushUndoState()
            list[index] = list[index].copy(filter = filter)
            _videoClips.value = list
            autoSaveCurrentProject()
        }
    }

    fun applyEffectToSelectedClip(effect: VisualEffectPreset) {
        val index = _selectedClipIndex.value
        val list = _videoClips.value.toMutableList()
        if (index in list.indices) {
            pushUndoState()
            list[index] = list[index].copy(effect = effect)
            _videoClips.value = list
            autoSaveCurrentProject()
        }
    }

    fun setEffectIntensity(intensity: Float) {
        val index = _selectedClipIndex.value
        val list = _videoClips.value.toMutableList()
        if (index in list.indices) {
            val safe = intensity.coerceIn(0f, 1f)
            list[index] = list[index].copy(effectIntensity = safe)
            _videoClips.value = list
            autoSaveCurrentProject()
        }
    }

    fun updateColorAdjustment(
        brightness: Float? = null,
        contrast: Float? = null,
        saturation: Float? = null,
        sharpness: Float? = null,
        blur: Float? = null
    ) {
        val index = _selectedClipIndex.value
        val list = _videoClips.value.toMutableList()
        if (index in list.indices) {
            val current = list[index]
            list[index] = current.copy(
                brightness = brightness ?: current.brightness,
                contrast = contrast ?: current.contrast,
                saturation = saturation ?: current.saturation,
                sharpness = sharpness ?: current.sharpness,
                blur = blur ?: current.blur
            )
            _videoClips.value = list
        }
    }

    fun toggleChromaKey(enabled: Boolean, color: Long = 0xFF00FF00) {
        val index = _selectedClipIndex.value
        val list = _videoClips.value.toMutableList()
        if (index in list.indices) {
            list[index] = list[index].copy(
                isChromaKeyEnabled = enabled,
                chromaKeyColor = color
            )
            _videoClips.value = list
        }
    }

    fun setPipSettings(isPip: Boolean, scale: Float = 0.4f, offsetX: Float = 0.25f, offsetY: Float = 0.25f) {
        val index = _selectedClipIndex.value
        val list = _videoClips.value.toMutableList()
        if (index in list.indices) {
            list[index] = list[index].copy(
                isPip = isPip,
                pipScale = scale,
                pipOffsetX = offsetX,
                pipOffsetY = offsetY
            )
            _videoClips.value = list
        }
    }

    fun setClipTransition(transition: TransitionType) {
        val index = _selectedClipIndex.value
        val list = _videoClips.value.toMutableList()
        if (index in list.indices) {
            pushUndoState()
            list[index] = list[index].copy(transition = transition)
            _videoClips.value = list
            autoSaveCurrentProject()
        }
    }

    fun deleteSelectedClip() {
        val list = _videoClips.value.toMutableList()
        val index = _selectedClipIndex.value
        if (list.size > 1 && index in list.indices) {
            pushUndoState()
            list.removeAt(index)
            _videoClips.value = list
            _selectedClipIndex.value = (index - 1).coerceAtLeast(0)
            autoSaveCurrentProject()
        }
    }

    fun duplicateSelectedClip() {
        val list = _videoClips.value.toMutableList()
        val index = _selectedClipIndex.value
        if (index in list.indices) {
            pushUndoState()
            val original = list[index]
            val duplicate = original.copy(
                id = java.util.UUID.randomUUID().toString(),
                name = "${original.name} (Copy)"
            )
            list.add(index + 1, duplicate)
            _videoClips.value = list
            _selectedClipIndex.value = index + 1
            autoSaveCurrentProject()
        }
    }

    fun moveClip(fromIndex: Int, toIndex: Int) {
        val list = _videoClips.value.toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices && fromIndex != toIndex) {
            pushUndoState()
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            _videoClips.value = list
            _selectedClipIndex.value = toIndex
            autoSaveCurrentProject()
        }
    }

    fun addMediaClips(uris: List<Uri>) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            pushUndoState()
            val current = _videoClips.value.toMutableList()
            uris.forEachIndexed { index, uri ->
                val localPath = MediaStorageHelper.persistMediaLocally(context, uri, "added_clip_${current.size + index + 1}")
                val realDuration = VideoFrameProvider.getVideoDurationMs(context, localPath)
                val duration = if (realDuration > 0L) realDuration else 10000L
                current.add(
                    VideoClip(
                        uriString = localPath,
                        name = "Clip ${current.size + 1}",
                        originalDurationMs = duration,
                        trimStartMs = 0L,
                        trimEndMs = duration
                    )
                )
            }
            _videoClips.value = current
            autoSaveCurrentProject()
        }
    }

    fun importAudioFile(uri: Uri, title: String = "Imported Music") {
        viewModelScope.launch {
            val context = getApplication<Application>()
            pushUndoState()
            val localPath = MediaStorageHelper.persistMediaLocally(context, uri, "audio_track")
            val realDuration = VideoFrameProvider.getVideoDurationMs(context, localPath)
            val duration = if (realDuration > 0L) realDuration else totalVideoDurationMs.coerceAtLeast(15000L)
            val track = AudioTrackItem(
                title = title,
                uriString = localPath,
                startTimelineMs = _playheadMs.value,
                durationMs = duration,
                isVoiceRecording = false
            )
            _audioTracks.value = _audioTracks.value + track
            autoSaveCurrentProject()
        }
    }

    fun updateSubtitle(id: String, newText: String, color: Long? = null, fontSize: Float? = null) {
        pushUndoState()
        val list = _subtitles.value.map {
            if (it.id == id) {
                it.copy(
                    text = newText,
                    textColor = color ?: it.textColor,
                    fontSizeSp = fontSize ?: it.fontSizeSp
                )
            } else it
        }
        _subtitles.value = list
        autoSaveCurrentProject()
    }

    // Audio & Voice Recording
    fun addAudioTrack(title: String, durationMs: Long = 10000L, isVoiceRecord: Boolean = false, uri: String? = null) {
        val track = AudioTrackItem(
            title = title,
            uriString = uri,
            startTimelineMs = _playheadMs.value,
            durationMs = durationMs,
            isVoiceRecording = isVoiceRecord
        )
        _audioTracks.value = _audioTracks.value + track
    }

    fun removeAudioTrack(id: String) {
        _audioTracks.value = _audioTracks.value.filterNot { it.id == id }
    }

    // Subtitles & Captions
    fun addSubtitle(text: String) {
        val item = SubtitleItem(
            text = text,
            startTimelineMs = _playheadMs.value,
            endTimelineMs = (_playheadMs.value + 3000L).coerceAtMost(totalVideoDurationMs.coerceAtLeast(3000L))
        )
        _subtitles.value = _subtitles.value + item
    }

    fun removeSubtitle(id: String) {
        _subtitles.value = _subtitles.value.filterNot { it.id == id }
    }

    fun generateAutoCaptions() {
        viewModelScope.launch {
            _aiProcessing.value = true
            val generated = AiEngine.generateAutoCaptions(totalVideoDurationMs.coerceAtLeast(6000L))
            _subtitles.value = generated
            _aiProcessing.value = false
        }
    }

    // Video-to-Photo extraction
    fun captureCurrentFrameAsPhoto(): String {
        val playhead = _playheadMs.value
        var accum = 0L
        var targetClip: VideoClip? = null
        var clipTimeMs = 0L
        for (clip in _videoClips.value) {
            val dur = clip.effectiveDurationMs
            if (playhead in accum..(accum + dur)) {
                targetClip = clip
                val offset = playhead - accum
                clipTimeMs = clip.trimStartMs + (offset * clip.speed).toLong()
                break
            }
            accum += dur
        }
        if (targetClip == null) {
            targetClip = _videoClips.value.getOrNull(_selectedClipIndex.value) ?: _videoClips.value.firstOrNull()
            clipTimeMs = targetClip?.trimStartMs ?: 0L
        }
        return targetClip?.uriString ?: ""
    }

    // ----------------------------------------------------
    // PHOTO EDITOR CONTROLS
    // ----------------------------------------------------

    private val photoUndoStack = mutableListOf<PhotoEditorSnapshot>()
    private val photoRedoStack = mutableListOf<PhotoEditorSnapshot>()

    private val _canPhotoUndo = MutableStateFlow(false)
    val canPhotoUndo: StateFlow<Boolean> = _canPhotoUndo.asStateFlow()

    private val _canPhotoRedo = MutableStateFlow(false)
    val canPhotoRedo: StateFlow<Boolean> = _canPhotoRedo.asStateFlow()

    fun pushPhotoUndoState() {
        val currState = _photoEditState.value ?: return
        photoUndoStack.add(PhotoEditorSnapshot(currState, _drawingPaths.value))
        if (photoUndoStack.size > 25) photoUndoStack.removeAt(0)
        photoRedoStack.clear()
        _canPhotoUndo.value = true
        _canPhotoRedo.value = false
    }

    fun photoUndo() {
        val currState = _photoEditState.value ?: return
        if (photoUndoStack.isNotEmpty()) {
            photoRedoStack.add(PhotoEditorSnapshot(currState, _drawingPaths.value))
            val prev = photoUndoStack.removeAt(photoUndoStack.lastIndex)
            _photoEditState.value = prev.state
            _drawingPaths.value = prev.drawings
            _canPhotoUndo.value = photoUndoStack.isNotEmpty()
            _canPhotoRedo.value = true
            autoSaveCurrentProject()
        }
    }

    fun photoRedo() {
        val currState = _photoEditState.value ?: return
        if (photoRedoStack.isNotEmpty()) {
            photoUndoStack.add(PhotoEditorSnapshot(currState, _drawingPaths.value))
            val next = photoRedoStack.removeAt(photoRedoStack.lastIndex)
            _photoEditState.value = next.state
            _drawingPaths.value = next.drawings
            _canPhotoUndo.value = true
            _canPhotoRedo.value = photoRedoStack.isNotEmpty()
            autoSaveCurrentProject()
        }
    }

    fun resetPhotoEdits() {
        val curr = _photoEditState.value ?: return
        pushPhotoUndoState()
        _photoEditState.value = PhotoEditState(uriString = curr.uriString)
        _drawingPaths.value = emptyList()
        autoSaveCurrentProject()
    }

    fun selectPhotoTool(tool: PhotoEditorTool) {
        _activePhotoTool.value = if (_activePhotoTool.value == tool) PhotoEditorTool.NONE else tool
    }

    fun updatePhotoState(transform: (PhotoEditState) -> PhotoEditState) {
        val current = _photoEditState.value ?: return
        pushPhotoUndoState()
        _photoEditState.value = transform(current)
        autoSaveCurrentProject()
    }

    fun addDrawingPath(path: DrawingPath) {
        pushPhotoUndoState()
        _drawingPaths.value = _drawingPaths.value + path
        autoSaveCurrentProject()
    }

    fun clearDrawingPaths() {
        pushPhotoUndoState()
        _drawingPaths.value = emptyList()
        autoSaveCurrentProject()
    }

    fun addPhotoTextOverlay(overlay: PhotoTextOverlay) {
        pushPhotoUndoState()
        val curr = _photoEditState.value ?: return
        _photoEditState.value = curr.copy(textOverlays = curr.textOverlays + overlay)
        autoSaveCurrentProject()
    }

    fun updatePhotoTextOverlay(updated: PhotoTextOverlay) {
        pushPhotoUndoState()
        val curr = _photoEditState.value ?: return
        _photoEditState.value = curr.copy(textOverlays = curr.textOverlays.map { if (it.id == updated.id) updated else it })
        autoSaveCurrentProject()
    }

    fun deletePhotoTextOverlay(id: String) {
        pushPhotoUndoState()
        val curr = _photoEditState.value ?: return
        _photoEditState.value = curr.copy(textOverlays = curr.textOverlays.filterNot { it.id == id })
        autoSaveCurrentProject()
    }

    fun replacePhotoUri(newUri: Uri) {
        val context = getApplication<Application>()
        val localPath = MediaStorageHelper.persistMediaLocally(context, newUri, "photo_replaced")
        pushPhotoUndoState()
        val curr = _photoEditState.value ?: return
        _photoEditState.value = curr.copy(uriString = localPath, originalUriString = localPath)
        autoSaveCurrentProject()
    }

    fun applyEffectToPhoto(effect: VisualEffectPreset) {
        pushPhotoUndoState()
        val curr = _photoEditState.value ?: return
        _photoEditState.value = curr.copy(effect = effect)
        autoSaveCurrentProject()
    }

    fun setPhotoEffectIntensity(intensity: Float) {
        val curr = _photoEditState.value ?: return
        val safe = intensity.coerceIn(0f, 1f)
        _photoEditState.value = curr.copy(effectIntensity = safe)
        autoSaveCurrentProject()
    }

    // ----------------------------------------------------
    // AI TOOLS EXECUTION
    // ----------------------------------------------------

    private var lastAiAction: (() -> Unit)? = null

    fun retryLastAiAction() {
        val action = lastAiAction
        if (action != null) {
            _aiError.value = null
            action.invoke()
        }
    }

    fun triggerAiTextToImage(prompt: String) {
        generateMultipleAiImages(prompt, 1)
    }

    fun generateMultipleAiImages(prompt: String, count: Int = 1) {
        lastAiAction = { generateMultipleAiImages(prompt, count) }
        viewModelScope.launch {
            _aiProcessing.value = true
            _aiError.value = null
            _aiProgress.value = 15
            try {
                delay(300)
                _aiProgress.value = 55
                val paths = AiEngine.generateMultipleAiImages(getApplication(), prompt, count)
                _aiGeneratedImages.value = paths
                _aiResultUri.value = paths.firstOrNull()
                _aiProgress.value = 100
            } catch (e: Exception) {
                _aiError.value = e.localizedMessage ?: "Failed to generate AI images"
            } finally {
                _aiProcessing.value = false
            }
        }
    }

    fun removePhotoBackground(uri: Uri) {
        lastAiAction = { removePhotoBackground(uri) }
        viewModelScope.launch {
            _aiProcessing.value = true
            _aiError.value = null
            _aiProgress.value = 20
            try {
                val bitmap = loadBitmapFromUri(uri)
                if (bitmap != null) {
                    _aiProgress.value = 65
                    val path = AiEngine.removeBackgroundAndSave(getApplication(), bitmap)
                    _aiBgCutoutUri.value = path
                    _aiProgress.value = 100
                } else {
                    _aiError.value = "Unable to load selected photo"
                }
            } catch (e: Exception) {
                _aiError.value = e.localizedMessage ?: "Background removal failed"
            } finally {
                _aiProcessing.value = false
            }
        }
    }

    fun generatePhotoBackground(uri: Uri, bgPrompt: String) {
        lastAiAction = { generatePhotoBackground(uri, bgPrompt) }
        viewModelScope.launch {
            _aiProcessing.value = true
            _aiError.value = null
            _aiProgress.value = 20
            try {
                val bitmap = loadBitmapFromUri(uri)
                if (bitmap != null) {
                    _aiProgress.value = 60
                    val path = AiEngine.generateAiBackground(getApplication(), bitmap, bgPrompt)
                    _aiBgGeneratedUri.value = path
                    _aiProgress.value = 100
                } else {
                    _aiError.value = "Unable to load photo"
                }
            } catch (e: Exception) {
                _aiError.value = e.localizedMessage ?: "Background generation failed"
            } finally {
                _aiProcessing.value = false
            }
        }
    }

    fun generateAutoCaptionsForVideo(videoDurationMs: Long = 10000L, style: String = "Viral Dynamic") {
        lastAiAction = { generateAutoCaptionsForVideo(videoDurationMs, style) }
        viewModelScope.launch {
            _aiProcessing.value = true
            _aiError.value = null
            _aiProgress.value = 30
            try {
                delay(400)
                _aiProgress.value = 75
                val captions = AiEngine.generateAutoCaptions(videoDurationMs.coerceAtLeast(4000L), style)
                _aiCaptionsList.value = captions
                _subtitles.value = captions
                _aiProgress.value = 100
            } catch (e: Exception) {
                _aiError.value = e.localizedMessage ?: "Auto captions generation failed"
            } finally {
                _aiProcessing.value = false
            }
        }
    }

    fun updateAiCaptionItem(id: String, newText: String) {
        _aiCaptionsList.value = _aiCaptionsList.value.map {
            if (it.id == id) it.copy(text = newText) else it
        }
        _subtitles.value = _aiCaptionsList.value
    }

    fun deleteAiCaptionItem(id: String) {
        _aiCaptionsList.value = _aiCaptionsList.value.filterNot { it.id == id }
        _subtitles.value = _aiCaptionsList.value
    }

    fun addAiCaptionItem(text: String = "New caption line") {
        val current = _aiCaptionsList.value
        val lastEnd = current.lastOrNull()?.endTimelineMs ?: 0L
        val item = SubtitleItem(
            id = java.util.UUID.randomUUID().toString(),
            text = text,
            startTimelineMs = lastEnd + 100L,
            endTimelineMs = lastEnd + 2200L,
            posYRatio = 0.80f,
            fontSizeSp = 20f,
            textColor = 0xFFFFFFFF,
            bgColor = 0xCC111111,
            isBold = true
        )
        _aiCaptionsList.value = current + item
        _subtitles.value = _aiCaptionsList.value
    }

    fun generateSpeechAudio(text: String, voiceName: String, pitch: Float = 1.0f, speed: Float = 1.0f) {
        lastAiAction = { generateSpeechAudio(text, voiceName, pitch, speed) }
        viewModelScope.launch {
            _aiProcessing.value = true
            _aiError.value = null
            _aiProgress.value = 25
            try {
                delay(300)
                _aiProgress.value = 70
                val path = AiEngine.generateSpeechAudio(getApplication(), text, voiceName, pitch, speed)
                _aiVoiceAudioUri.value = path
                _aiProgress.value = 100
            } catch (e: Exception) {
                _aiError.value = e.localizedMessage ?: "Speech generation failed"
            } finally {
                _aiProcessing.value = false
            }
        }
    }

    fun generateAiVideo(imageUri: Uri?, motionPrompt: String, durationSec: Int = 4, aspect: String = "16:9") {
        lastAiAction = { generateAiVideo(imageUri, motionPrompt, durationSec, aspect) }
        viewModelScope.launch {
            _aiProcessing.value = true
            _aiError.value = null
            _aiProgress.value = 20
            try {
                val bitmap = if (imageUri != null) loadBitmapFromUri(imageUri) else null
                _aiProgress.value = 60
                val path = AiEngine.generateAiVideo(getApplication(), bitmap, motionPrompt, durationSec, aspect)
                _aiVideoGeneratedUri.value = path
                _aiProgress.value = 100
            } catch (e: Exception) {
                _aiError.value = e.localizedMessage ?: "AI Video generation failed"
            } finally {
                _aiProcessing.value = false
            }
        }
    }

    fun enhancePhoto(uri: Uri) {
        lastAiAction = { enhancePhoto(uri) }
        viewModelScope.launch {
            _aiProcessing.value = true
            _aiError.value = null
            _aiProgress.value = 20
            try {
                _aiEnhancedOriginalUri.value = uri.toString()
                val bitmap = loadBitmapFromUri(uri)
                if (bitmap != null) {
                    _aiProgress.value = 65
                    val path = AiEngine.enhancePhotoQuality(getApplication(), bitmap)
                    _aiEnhancedImageUri.value = path
                    _aiProgress.value = 100
                } else {
                    _aiError.value = "Unable to load photo for enhancement"
                }
            } catch (e: Exception) {
                _aiError.value = e.localizedMessage ?: "Photo enhancement failed"
            } finally {
                _aiProcessing.value = false
            }
        }
    }

    fun triggerAiEnhanceCurrentPhoto(bitmap: Bitmap?) {
        if (bitmap == null) return
        viewModelScope.launch {
            _aiProcessing.value = true
            AiEngine.enhanceImage(bitmap)
            updatePhotoState { it.copy(isAiEnhanced = true) }
            _aiProcessing.value = false
        }
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val resolver = getApplication<Application>().contentResolver
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                val source = android.graphics.ImageDecoder.createSource(resolver, uri)
                android.graphics.ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = android.graphics.ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                }
            } else {
                @Suppress("DEPRECATION")
                android.provider.MediaStore.Images.Media.getBitmap(resolver, uri)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun addAudioToVideoTimeline(audioPath: String, title: String = "AI Voiceover") {
        val track = AudioTrackItem(
            title = title,
            uriString = audioPath,
            startTimelineMs = _playheadMs.value,
            durationMs = 6000L,
            isVoiceRecording = true
        )
        _audioTracks.value = _audioTracks.value + track
        navigateTo(ScreenState.VIDEO_EDITOR)
    }

    fun importGeneratedVideoToEditor(videoPath: String) {
        viewModelScope.launch {
            val realDuration = VideoFrameProvider.getVideoDurationMs(getApplication(), videoPath)
            val duration = if (realDuration > 0L) realDuration else 6000L
            val clip = VideoClip(
                uriString = videoPath,
                name = "AI Motion Clip",
                originalDurationMs = duration,
                trimStartMs = 0L,
                trimEndMs = duration
            )
            _videoClips.value = _videoClips.value + clip
            navigateTo(ScreenState.VIDEO_EDITOR)
        }
    }

    fun clearAiError() {
        _aiError.value = null
    }

    // ----------------------------------------------------
    // EXPORT SYSTEM
    // ----------------------------------------------------

    fun showExportSheet(show: Boolean) {
        _showExportDialog.value = show
    }

    fun startExport(config: ExportConfig, sourceBitmap: Bitmap? = null) {
        viewModelScope.launch {
            _isExporting.value = true
            _exportProgress.value = 0

            val proj = _activeProject.value
            val title = proj?.title ?: "WS Project"

            val file = if (proj?.type == "VIDEO") {
                MediaExportEngine.exportVideo(
                    context = getApplication(),
                    projectName = title,
                    clips = _videoClips.value,
                    config = config,
                    onProgress = { _exportProgress.value = it }
                )
            } else {
                MediaExportEngine.exportPhoto(
                    context = getApplication(),
                    projectName = title,
                    state = _photoEditState.value ?: PhotoEditState(uriString = ""),
                    sourceBitmap = sourceBitmap,
                    config = config,
                    onProgress = { _exportProgress.value = it }
                )
            }

            _exportedFile.value = file
            _isExporting.value = false

            // Trigger non-disruptive monetization hook after export
            monetization.triggerExportInterstitial {
                // Done
            }
        }
    }

    fun resetExportState() {
        _exportedFile.value = null
        _exportProgress.value = 0
        _isExporting.value = false
        _showExportDialog.value = false
    }

    override fun onCleared() {
        super.onCleared()
        pausePlayback()
    }
}
