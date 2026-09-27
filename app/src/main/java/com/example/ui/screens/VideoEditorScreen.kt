package com.example.ui.screens

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.data.CustomFontEntity
import com.example.data.FontRegistry
import com.example.data.VideoFilterPreset
import com.example.data.VideoTool
import com.example.ui.VideoEditorUiState
import com.example.ui.components.TextAndCustomFontPanel
import com.example.ui.components.VnMultiLayerTimeline
import com.example.ui.theme.RobotoMonoFamily
import com.example.ui.theme.TrackGreen
import com.example.ui.theme.TrackOrange
import com.example.ui.theme.WsBackground
import com.example.ui.theme.WsBorder
import com.example.ui.theme.WsCyan
import com.example.ui.theme.WsPurple
import com.example.ui.theme.WsSurface
import com.example.ui.theme.WsSurfaceElevated
import com.example.ui.theme.WsTextPrimary
import com.example.ui.theme.WsTextSecondary
import kotlin.math.roundToInt

@Composable
fun VideoEditorScreen(
    state: VideoEditorUiState,
    customFonts: List<CustomFontEntity>,
    filterPresets: List<VideoFilterPreset>,
    speedPresets: List<Float>,
    musicLibrary: List<Triple<String, String, Float>>,
    fxLibrary: List<Pair<String, String>>,
    onPickVideoFromGallery: () -> Unit,
    onUploadCustomFontFromDevice: () -> Unit,
    onImportSampleCustomFont: (String, String) -> Unit,
    onDeleteCustomFont: (CustomFontEntity) -> Unit,
    onTogglePlayback: () -> Unit,
    onSeek: (Float) -> Unit,
    onCycleAspectRatio: () -> Unit,
    onSelectTool: (VideoTool) -> Unit,
    onSelectClip: (Int, String) -> Unit,
    onSplitAtPlayhead: () -> Unit,
    onTrimSelected: (Float, Float) -> Unit,
    onDeleteSelected: () -> Unit,
    onSetSpeed: (Float) -> Unit,
    onSetVolume: (Float) -> Unit,
    onSetFilter: (String) -> Unit,
    onAddTextOverlay: () -> Unit,
    onUpdateTextOverlay: (String?, Long?, Float?, Boolean?, Boolean?, Float?, Float?) -> Unit,
    onSelectBuiltInFont: (String, String) -> Unit,
    onSelectCustomFont: (CustomFontEntity) -> Unit,
    onAddStickerOrFx: (String, String, Boolean) -> Unit,
    onSelectMusicTrack: (String, String, Float) -> Unit,
    onShowExportDialog: (Boolean) -> Unit,
    onStartExport4K: (String) -> Unit
) {
    val activeFilter = filterPresets.firstOrNull { it.id == state.activeFilterId } ?: filterPresets.first()
    val selectedTextOverlay = state.textOverlays.firstOrNull { it.id == state.selectedClipId }
        ?: state.textOverlays.firstOrNull()

    val verticalScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WsBackground)
            .verticalScroll(verticalScroll)
            .padding(bottom = 8.dp)
            .testTag("video_editor_screen")
    ) {
        // 1. Top VN Editor Bar (Project Title, Gallery Picker, Aspect Ratio, EXPORT 4K)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(WsSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(WsSurfaceElevated)
                        .border(1.dp, WsBorder, RoundedCornerShape(8.dp))
                        .clickable { onPickVideoFromGallery() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("btn_pick_video_editor")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = "Pick Video",
                            tint = WsCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Import",
                            style = MaterialTheme.typography.labelSmall,
                            color = WsTextPrimary
                        )
                    }
                }

                Column {
                    Text(
                        text = state.projectTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = WsTextPrimary
                    )
                    Text(
                        text = "VN Pro • ${state.playbackSpeed}x • ${activeFilter.name}",
                        fontFamily = RobotoMonoFamily,
                        fontSize = 10.sp,
                        color = WsCyan
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Aspect ratio badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(WsSurfaceElevated)
                        .border(1.dp, WsBorder, RoundedCornerShape(8.dp))
                        .clickable { onCycleAspectRatio() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("btn_aspect_ratio")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AspectRatio,
                            contentDescription = "Aspect Ratio",
                            tint = WsTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = state.aspectRatio,
                            fontFamily = RobotoMonoFamily,
                            fontSize = 11.sp,
                            color = WsTextPrimary
                        )
                    }
                }

                // EXPORT 4K Cyan Button
                Button(
                    onClick = { onShowExportDialog(true) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WsCyan,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("btn_export_4k_top")
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "Export 4K",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "EXPORT 4K",
                        fontFamily = RobotoMonoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Status Feedback Banner if present
        state.statusBannerMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WsCyan.copy(alpha = 0.14f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "✓ $msg",
                    fontFamily = RobotoMonoFamily,
                    fontSize = 11.sp,
                    color = WsCyan
                )
            }
        }

        // 2. Top Video Preview Player (Black #000000 stage with Live Overlays, Filter & Custom Fonts)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(215.dp)
                .background(Color.Black)
                .border(1.dp, WsBorder)
                .testTag("video_preview_player_box"),
            contentAlignment = Alignment.Center
        ) {
            val stageWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
            val stageHeightPx = constraints.maxHeight.toFloat().coerceAtLeast(1f)

            val matrix = ColorMatrix().apply {
                setToScale(activeFilter.contrast, activeFilter.contrast, activeFilter.contrast, 1f)
            }

            if (!state.selectedVideoUri.isNullOrBlank()) {
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            setVideoURI(Uri.parse(state.selectedVideoUri))
                            setOnPreparedListener { mp ->
                                mp.isLooping = true
                                mp.setVolume(state.masterVolume, state.masterVolume)
                                if (state.isPlaying) start()
                            }
                        }
                    },
                    update = { vv ->
                        if (state.isPlaying && !vv.isPlaying) {
                            vv.start()
                        } else if (!state.isPlaying && vv.isPlaying) {
                            vv.pause()
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.img_sample_cyberpunk_video),
                    contentDescription = "Video Preview Canvas",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(matrix),
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Color Filter LUT Overlay + Animated FX Scanlines
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (activeFilter.tintColorHex != 0L) {
                    drawRect(color = Color(activeFilter.tintColorHex))
                }
                val activeFxClips = state.stickerFxClips.filter {
                    it.isFx && state.playheadSec in it.startSec..it.endSec
                }
                if (activeFxClips.isNotEmpty()) {
                    val scanOffset = (state.playheadSec * 80f) % size.height
                    drawLine(
                        color = WsCyan.copy(alpha = 0.55f),
                        start = Offset(0f, scanOffset),
                        end = Offset(size.width, scanOffset),
                        strokeWidth = 3.dp.toPx()
                    )
                    drawLine(
                        color = WsPurple.copy(alpha = 0.45f),
                        start = Offset(0f, (scanOffset + 40f) % size.height),
                        end = Offset(size.width, (scanOffset + 40f) % size.height),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }

            // Active Sticker Badges on Stage
            val visibleStickers = state.stickerFxClips.filter {
                state.playheadSec in it.startSec..it.endSec
            }
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                visibleStickers.forEach { stk ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .border(1.dp, TrackOrange, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stk.label,
                            fontFamily = RobotoMonoFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrackOrange
                        )
                    }
                }
            }

            // Live Rendered Text Overlays (with Custom Font Typeface + Drag-to-Move on Video!)
            state.textOverlays.forEach { overlay ->
                val isVisibleAtPlayhead = state.playheadSec in overlay.startSec..overlay.endSec
                val isSelected = state.selectedClipId == overlay.id
                if (isVisibleAtPlayhead || isSelected) {
                    val fontFamily = FontRegistry.resolveFontFamily(overlay.fontId, overlay.customFontPath)
                    val xOffsetPx = ((overlay.offsetXPercent - 0.5f) * stageWidthPx * 0.75f).roundToInt()
                    val yOffsetPx = ((overlay.offsetYPercent - 0.5f) * stageHeightPx * 0.75f).roundToInt()

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(xOffsetPx, yOffsetPx) }
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (overlay.hasBgBox) Color.Black.copy(alpha = 0.68f)
                                else Color.Transparent
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) WsCyan else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectClip(1, overlay.id) }
                            .pointerInput(overlay.id) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val newX = overlay.offsetXPercent + (dragAmount.x / stageWidthPx)
                                    val newY = overlay.offsetYPercent + (dragAmount.y / stageHeightPx)
                                    onSelectClip(1, overlay.id)
                                    onUpdateTextOverlay(null, null, null, null, null, newX, newY)
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("video_canvas_text_${overlay.id}")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = overlay.text,
                                fontFamily = fontFamily,
                                fontSize = overlay.fontSizeSp.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(overlay.colorHex),
                                style = TextStyle(
                                    shadow = if (overlay.hasGlow) {
                                        Shadow(
                                            color = Color(overlay.colorHex).copy(alpha = 0.85f),
                                            offset = Offset(0f, 0f),
                                            blurRadius = 18f
                                        )
                                    } else null
                                )
                            )
                            if (isSelected) {
                                Text(
                                    text = "Font: ${overlay.fontName}" +
                                        if (!overlay.customFontPath.isNullOrBlank()) " (Custom .TTF)" else "",
                                    fontFamily = RobotoMonoFamily,
                                    fontSize = 9.sp,
                                    color = WsCyan
                                )
                            }
                        }
                    }
                }
            }

            // Playback Transport Overlay at Bottom of Video Player
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.72f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onTogglePlayback,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(WsCyan)
                        .testTag("btn_play_pause_video")
                ) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (state.isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = String.format("%02d:%04.1f", (state.playheadSec / 60).toInt(), state.playheadSec % 60),
                    fontFamily = RobotoMonoFamily,
                    fontSize = 11.sp,
                    color = WsCyan,
                    fontWeight = FontWeight.Bold
                )

                Slider(
                    value = state.playheadSec,
                    onValueChange = onSeek,
                    valueRange = 0f..state.totalDurationSec,
                    colors = SliderDefaults.colors(
                        thumbColor = WsCyan,
                        activeTrackColor = WsCyan,
                        inactiveTrackColor = WsBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                )

                Text(
                    text = String.format("00:%04.1f", state.totalDurationSec),
                    fontFamily = RobotoMonoFamily,
                    fontSize = 11.sp,
                    color = WsTextSecondary
                )
            }
        }

        // 3. Middle Horizontal Toolbar: Split, Speed, Volume, Filter, Text, Sticker, Music, FX
        val toolbarItems: List<Pair<VideoTool, ImageVector>> = listOf(
            VideoTool.SPLIT to Icons.Default.ContentCut,
            VideoTool.SPEED to Icons.Default.Speed,
            VideoTool.VOLUME to Icons.AutoMirrored.Filled.VolumeUp,
            VideoTool.FILTER to Icons.Default.FilterVintage,
            VideoTool.TEXT to Icons.Default.Title,
            VideoTool.STICKER to Icons.Default.EmojiEmotions,
            VideoTool.MUSIC to Icons.Default.MusicNote,
            VideoTool.FX to Icons.Default.Flare
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(WsSurface)
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(toolbarItems, key = { it.first.name }) { (tool, icon) ->
                val isSelected = state.activeTool == tool
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) WsCyan else WsSurfaceElevated)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) WsCyan else WsBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            if (tool == VideoTool.SPLIT && state.activeTool == VideoTool.SPLIT) {
                                onSplitAtPlayhead()
                            } else {
                                onSelectTool(tool)
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("toolbar_tool_${tool.label.lowercase()}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = tool.label,
                        tint = if (isSelected) Color.Black else WsCyan,
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        text = tool.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) Color.Black else WsTextPrimary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 4. Contextual Tool Inspector Panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            when (state.activeTool) {
                VideoTool.TEXT -> {
                    TextAndCustomFontPanel(
                        selectedTextOverlay = selectedTextOverlay,
                        customFonts = customFonts,
                        onAddTextOverlay = onAddTextOverlay,
                        onUpdateText = { onUpdateTextOverlay(it, null, null, null, null, null, null) },
                        onUpdateColor = { onUpdateTextOverlay(null, it, null, null, null, null, null) },
                        onUpdateFontSize = { onUpdateTextOverlay(null, null, it, null, null, null, null) },
                        onToggleGlow = { onUpdateTextOverlay(null, null, null, it, null, null, null) },
                        onToggleBgBox = { onUpdateTextOverlay(null, null, null, null, it, null, null) },
                        onSelectBuiltInFont = onSelectBuiltInFont,
                        onSelectCustomFont = onSelectCustomFont,
                        onUploadCustomFontFromDevice = onUploadCustomFontFromDevice,
                        onImportSampleCustomFont = onImportSampleCustomFont,
                        onDeleteCustomFont = onDeleteCustomFont
                    )
                }

                VideoTool.SPEED -> {
                    SpeedControlPanel(
                        currentSpeed = state.playbackSpeed,
                        speedPresets = speedPresets,
                        onSetSpeed = onSetSpeed
                    )
                }

                VideoTool.VOLUME -> {
                    VolumeControlPanel(
                        volume = state.masterVolume,
                        onSetVolume = onSetVolume
                    )
                }

                VideoTool.FILTER -> {
                    FilterControlPanel(
                        activeFilterId = state.activeFilterId,
                        presets = filterPresets,
                        onSelectFilter = onSetFilter
                    )
                }

                VideoTool.MUSIC -> {
                    MusicControlPanel(
                        activeTrackName = state.audioClips.firstOrNull()?.trackName ?: "",
                        musicLibrary = musicLibrary,
                        onSelectTrack = onSelectMusicTrack
                    )
                }

                VideoTool.STICKER, VideoTool.FX -> {
                    StickerFxControlPanel(
                        fxLibrary = fxLibrary,
                        onAddStickerOrFx = onAddStickerOrFx
                    )
                }

                VideoTool.SPLIT, VideoTool.NONE -> {
                    SplitTrimQuickPanel(
                        playheadSec = state.playheadSec,
                        onSplitAtPlayhead = onSplitAtPlayhead,
                        onTrimStartMinus = { onTrimSelected(-0.5f, 0f) },
                        onTrimEndMinus = { onTrimSelected(0f, -0.5f) },
                        onDeleteClip = onDeleteSelected
                    )
                }
            }
        }

        // 5. Bottom Multi-Layer 4-Track Timeline (Cyan, Purple, Orange, Green left borders)
        VnMultiLayerTimeline(
            state = state,
            onSeek = onSeek,
            onSelectClip = onSelectClip,
            onSplitAtPlayhead = onSplitAtPlayhead,
            onTrimSelected = onTrimSelected,
            onDeleteSelected = onDeleteSelected
        )

        // 6. Prominent Bottom EXPORT 4K Button with Cyan Color
        Button(
            onClick = { onShowExportDialog(true) },
            colors = ButtonDefaults.buttonColors(
                containerColor = WsCyan,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .height(48.dp)
                .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = WsCyan)
                .testTag("btn_export_4k_bottom")
        ) {
            Icon(
                imageVector = Icons.Default.FileDownload,
                contentDescription = "Export 4K Video",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "EXPORT 4K • 60FPS UHD",
                fontFamily = RobotoMonoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }

    // Export 4K Modal Dialog
    if (state.isExportDialogVisible) {
        AlertDialog(
            onDismissRequest = { if (!state.isExporting) onShowExportDialog(false) },
            containerColor = WsSurface,
            titleContentColor = WsTextPrimary,
            textContentColor = WsTextSecondary,
            title = {
                Text(
                    text = "Export 4K Master Video",
                    style = MaterialTheme.typography.titleLarge,
                    color = WsCyan
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Project: ${state.projectTitle} (${state.aspectRatio})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WsTextPrimary
                    )
                    Text(
                        text = "Tracks: ${state.videoClips.size} Video • ${state.textOverlays.size} Text (${state.textOverlays.count { !it.customFontPath.isNullOrBlank() }} Custom Font) • ${state.stickerFxClips.size} FX • ${state.audioClips.size} Audio",
                        fontFamily = RobotoMonoFamily,
                        fontSize = 11.sp,
                        color = WsTextSecondary
                    )

                    if (state.isExporting) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Rendering 4K UHD @ 60fps... ${(state.exportProgress * 100).toInt()}%",
                                fontFamily = RobotoMonoFamily,
                                fontSize = 12.sp,
                                color = WsCyan
                            )
                            LinearProgressIndicator(
                                progress = { state.exportProgress },
                                color = WsCyan,
                                trackColor = WsBorder,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(50))
                            )
                        }
                    }

                    state.exportCompletedMessage?.let { completed ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(TrackGreen.copy(alpha = 0.16f))
                                .border(1.dp, TrackGreen, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = completed,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TrackGreen
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (state.exportCompletedMessage == null) {
                    Button(
                        onClick = { onStartExport4K("4K 60FPS") },
                        enabled = !state.isExporting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WsCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("confirm_export_4k_button")
                    ) {
                        Text(
                            text = if (state.isExporting) "Rendering..." else "Start 4K Export",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onShowExportDialog(false) },
                    enabled = !state.isExporting
                ) {
                    Text(
                        text = if (state.exportCompletedMessage != null) "Done" else "Cancel",
                        color = WsTextSecondary
                    )
                }
            }
        )
    }
}

@Composable
private fun SpeedControlPanel(
    currentSpeed: Float,
    speedPresets: List<Float>,
    onSetSpeed: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(WsSurface)
            .border(1.dp, WsBorder, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Speed Curve Control (0.2x — 8.0x)",
                style = MaterialTheme.typography.titleMedium,
                color = WsTextPrimary
            )
            Text(
                text = String.format("%.1fx", currentSpeed),
                fontFamily = RobotoMonoFamily,
                fontWeight = FontWeight.Bold,
                color = WsCyan
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            speedPresets.forEach { preset ->
                val active = kotlin.math.abs(currentSpeed - preset) < 0.05f
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (active) WsCyan else WsSurfaceElevated)
                        .clickable { onSetSpeed(preset) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${preset}x",
                        fontFamily = RobotoMonoFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (active) Color.Black else WsTextPrimary
                    )
                }
            }
        }

        Slider(
            value = currentSpeed,
            onValueChange = onSetSpeed,
            valueRange = 0.2f..8.0f,
            colors = SliderDefaults.colors(
                thumbColor = WsCyan,
                activeTrackColor = WsCyan,
                inactiveTrackColor = WsBorder
            )
        )
    }
}

@Composable
private fun VolumeControlPanel(
    volume: Float,
    onSetVolume: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(WsSurface)
            .border(1.dp, WsBorder, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Clip & Master Audio Gain",
                style = MaterialTheme.typography.titleMedium,
                color = WsTextPrimary
            )
            Text(
                text = "${(volume * 100).toInt()}%",
                fontFamily = RobotoMonoFamily,
                fontWeight = FontWeight.Bold,
                color = WsCyan
            )
        }
        Slider(
            value = volume,
            onValueChange = onSetVolume,
            valueRange = 0f..2.0f,
            colors = SliderDefaults.colors(
                thumbColor = WsCyan,
                activeTrackColor = WsCyan,
                inactiveTrackColor = WsBorder
            )
        )
    }
}

@Composable
private fun FilterControlPanel(
    activeFilterId: String,
    presets: List<VideoFilterPreset>,
    onSelectFilter: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(WsSurface)
            .border(1.dp, WsBorder, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Cinema Color Grading LUTs",
            style = MaterialTheme.typography.titleMedium,
            color = WsTextPrimary
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(presets, key = { it.id }) { filter ->
                val selected = filter.id == activeFilterId
                Column(
                    modifier = Modifier
                        .width(86.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) WsCyan.copy(alpha = 0.2f) else WsSurfaceElevated)
                        .border(
                            width = if (selected) 1.5.dp else 1.dp,
                            color = if (selected) WsCyan else WsBorder,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectFilter(filter.id) }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (filter.tintColorHex == 0L) Color.DarkGray
                                else Color(filter.tintColorHex).copy(alpha = 0.85f)
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                    )
                    Text(
                        text = filter.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) WsCyan else WsTextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = filter.category,
                        fontFamily = RobotoMonoFamily,
                        fontSize = 9.sp,
                        color = WsTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun MusicControlPanel(
    activeTrackName: String,
    musicLibrary: List<Triple<String, String, Float>>,
    onSelectTrack: (String, String, Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(WsSurface)
            .border(1.dp, WsBorder, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Add Royalty-Free Studio Music",
            style = MaterialTheme.typography.titleMedium,
            color = WsTextPrimary
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(musicLibrary, key = { it.first }) { (title, artist, dur) ->
                val selected = activeTrackName == title
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) TrackGreen.copy(alpha = 0.18f) else WsSurfaceElevated)
                        .border(1.dp, if (selected) TrackGreen else WsBorder, RoundedCornerShape(10.dp))
                        .clickable { onSelectTrack(title, artist, dur) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "♫ $title",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) TrackGreen else WsTextPrimary
                    )
                    Text(
                        text = "$artist • ${dur}s",
                        fontFamily = RobotoMonoFamily,
                        fontSize = 10.sp,
                        color = WsTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun StickerFxControlPanel(
    fxLibrary: List<Pair<String, String>>,
    onAddStickerOrFx: (String, String, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(WsSurface)
            .border(1.dp, WsBorder, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Add VN Motion FX & Stickers",
            style = MaterialTheme.typography.titleMedium,
            color = WsTextPrimary
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(fxLibrary, key = { it.first }) { (label, badge) ->
                val isFx = badge.startsWith("FX")
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(WsSurfaceElevated)
                        .border(1.dp, if (isFx) WsPurple else TrackOrange, RoundedCornerShape(10.dp))
                        .clickable { onAddStickerOrFx(label, badge, isFx) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = WsTextPrimary
                        )
                        Text(
                            text = "+ Add $badge",
                            fontFamily = RobotoMonoFamily,
                            fontSize = 9.sp,
                            color = if (isFx) WsCyan else TrackOrange
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SplitTrimQuickPanel(
    playheadSec: Float,
    onSplitAtPlayhead: () -> Unit,
    onTrimStartMinus: () -> Unit,
    onTrimEndMinus: () -> Unit,
    onDeleteClip: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(WsSurface)
            .border(1.dp, WsBorder, RoundedCornerShape(14.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Split & Trim Clip",
                style = MaterialTheme.typography.titleMedium,
                color = WsTextPrimary
            )
            Text(
                text = "Playhead @ ${String.format("%.1fs", playheadSec)} • Drag clip edges or use quick actions",
                fontFamily = RobotoMonoFamily,
                fontSize = 10.sp,
                color = WsTextSecondary
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Button(
                onClick = onSplitAtPlayhead,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WsCyan,
                    contentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text("Split Here", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
