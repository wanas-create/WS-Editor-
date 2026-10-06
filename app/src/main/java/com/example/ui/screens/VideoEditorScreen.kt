package com.example.ui.screens

import android.net.Uri
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.media.VideoFrameProvider
import kotlin.math.roundToInt
import com.example.model.AspectRatioOption
import com.example.model.FilterPreset
import com.example.model.SubtitleItem
import com.example.model.TransitionType
import com.example.model.VideoClip
import com.example.ui.components.ExportDialog
import com.example.ui.theme.WsAccentGold
import com.example.ui.theme.WsAccentGreen
import com.example.ui.theme.WsAccentRed
import com.example.ui.theme.WsBackground
import com.example.ui.theme.WsBorder
import com.example.ui.theme.WsBorderGlow
import com.example.ui.theme.WsButtonDark
import com.example.ui.theme.WsElectricBlue
import com.example.ui.theme.WsElectricCyan
import com.example.ui.theme.WsMediumGray
import com.example.ui.theme.WsOffWhite
import com.example.ui.theme.WsPureWhite
import com.example.ui.theme.WsSurfaceCard
import com.example.ui.theme.WsSurfaceCardElevated
import com.example.ui.theme.WsTextMuted
import com.example.ui.theme.WsTextPrimary
import com.example.ui.theme.WsTextSecondary
import com.example.ui.theme.WsTimelineBg
import com.example.ui.theme.WsTimelinePlayhead
import com.example.ui.theme.WsTimelineText
import com.example.ui.theme.WsTimelineTrack
import com.example.viewmodel.ScreenState
import com.example.viewmodel.VideoEditorTool
import com.example.viewmodel.WsEditorViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoEditorScreen(viewModel: WsEditorViewModel) {
    val context = LocalContext.current
    val clips by viewModel.videoClips.collectAsState()
    val selectedIndex by viewModel.selectedClipIndex.collectAsState()
    val playheadMs by viewModel.playheadMs.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val aspectRatio by viewModel.aspectRatio.collectAsState()
    val activeTool by viewModel.activeVideoTool.collectAsState()
    val audioTracks by viewModel.audioTracks.collectAsState()
    val subtitles by viewModel.subtitles.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()

    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()

    val isExporting by viewModel.isExporting.collectAsState()
    val exportProgress by viewModel.exportProgress.collectAsState()
    val exportedFile by viewModel.exportedFile.collectAsState()
    val showExportDialog by viewModel.showExportDialog.collectAsState()
    val isPro by viewModel.monetization.isProUser.collectAsState()

    var showAspectMenu by remember { mutableStateOf(false) }
    var timelineZoom by remember { mutableFloatStateOf(1.0f) }

    // Subtitle editing state dialog
    var subtitleToEdit by remember { mutableStateOf<SubtitleItem?>(null) }
    var subtitleEditText by remember { mutableStateOf("") }

    // Media picker for adding more clips to current project
    val addMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.addMediaClips(uris)
        }
    }

    // Audio file picker from phone storage
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.importAudioFile(uri, "Imported Music")
            Toast.makeText(context, "Music track imported", Toast.LENGTH_SHORT).show()
        }
    }

    // Auto-save when navigating back
    BackHandler {
        viewModel.saveCurrentProject(asDraft = true)
        viewModel.navigateTo(ScreenState.HOME)
    }

    val selectedClip = clips.getOrNull(selectedIndex) ?: clips.firstOrNull()
    val totalDurationMs = viewModel.totalVideoDurationMs.coerceAtLeast(1000L)

    // Determine active clip and relative time at playheadMs for frame-accurate preview
    var accumMs = 0L
    var activeClipAtPlayhead: VideoClip? = null
    var activeClipRelativeTimeMs = 0L

    for (clip in clips) {
        val duration = clip.effectiveDurationMs
        if (playheadMs in accumMs..(accumMs + duration)) {
            activeClipAtPlayhead = clip
            val offset = playheadMs - accumMs
            activeClipRelativeTimeMs = clip.trimStartMs + (offset * clip.speed).toLong().coerceIn(0L, clip.originalDurationMs)
            break
        }
        accumMs += duration
    }
    if (activeClipAtPlayhead == null) {
        activeClipAtPlayhead = selectedClip ?: clips.firstOrNull()
        activeClipRelativeTimeMs = activeClipAtPlayhead?.trimStartMs ?: 0L
    }

    var currentFrameBitmap by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(activeClipAtPlayhead?.uriString, activeClipRelativeTimeMs) {
        val uri = activeClipAtPlayhead?.uriString
        if (!uri.isNullOrEmpty()) {
            val bmp = VideoFrameProvider.getFrameAtTime(
                context,
                uri,
                activeClipRelativeTimeMs,
                targetWidth = 720,
                targetHeight = 480
            )
            if (bmp != null) {
                currentFrameBitmap = bmp
            }
        }
    }

    Scaffold(
        containerColor = WsBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WsBackground,
                    scrolledContainerColor = WsBackground
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.saveCurrentProject(asDraft = true)
                        viewModel.navigateTo(ScreenState.HOME)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = WsTextPrimary
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            text = activeProject?.title ?: "Video Editor",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = WsTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Auto-saving as draft",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = WsTextSecondary
                        )
                    }
                },
                actions = {
                    // Undo Button
                    IconButton(
                        onClick = { viewModel.undo() },
                        enabled = canUndo,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Undo,
                            contentDescription = "Undo",
                            tint = if (canUndo) WsElectricCyan else WsTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Redo Button
                    IconButton(
                        onClick = { viewModel.redo() },
                        enabled = canRedo,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Redo,
                            contentDescription = "Redo",
                            tint = if (canRedo) WsElectricCyan else WsTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Aspect Ratio Dropdown
                    Box {
                        TextButton(onClick = { showAspectMenu = true }) {
                            Icon(Icons.Default.AspectRatio, contentDescription = null, tint = WsElectricCyan, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = aspectRatio.label, color = WsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        }
                        DropdownMenu(
                            expanded = showAspectMenu,
                            onDismissRequest = { showAspectMenu = false },
                            modifier = Modifier.background(WsSurfaceCardElevated)
                        ) {
                            AspectRatioOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label, fontWeight = FontWeight.SemiBold, color = WsTextPrimary) },
                                    onClick = {
                                        viewModel.setAspectRatio(option)
                                        showAspectMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Export Button with Electric Blue Accent
                    Button(
                        onClick = { viewModel.showExportSheet(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = WsElectricBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .testTag("export_button")
                    ) {
                        Text(text = "Export", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WsTextPrimary)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ----------------------------------------------------
            // 1. VIDEO PREVIEW PLAYER VIEWPORT
            // ----------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.40f)
                    .background(Color(0xFF090A0B))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                // Viewport aspect ratio container
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(aspectRatio.ratio)
                        .background(Color.Black)
                        .border(1.dp, Color(0xFF242629)),
                    contentAlignment = Alignment.Center
                ) {
                    val clipToRender = activeClipAtPlayhead
                    if (clipToRender != null) {
                        // Apply transforms & filters
                        val cm = remember(clipToRender.filter, clipToRender.contrast, clipToRender.saturation, clipToRender.brightness) {
                            val androidMatrix = android.graphics.ColorMatrix()
                            androidMatrix.setSaturation(clipToRender.saturation)
                            val contrast = clipToRender.contrast
                            val translate = (-0.5f * contrast + 0.5f) * 255f + (clipToRender.brightness * 128f)
                            androidMatrix.postConcat(android.graphics.ColorMatrix(floatArrayOf(
                                contrast, 0f, 0f, 0f, translate,
                                0f, contrast, 0f, 0f, translate,
                                0f, 0f, contrast, 0f, translate,
                                0f, 0f, 0f, 1f, 0f
                            )))
                            if (clipToRender.filter == FilterPreset.MONO) {
                                val mono = android.graphics.ColorMatrix()
                                mono.setSaturation(0f)
                                androidMatrix.postConcat(mono)
                            }
                            androidx.compose.ui.graphics.ColorMatrix(androidMatrix.array)
                        }

                        if (currentFrameBitmap != null) {
                            Image(
                                bitmap = currentFrameBitmap!!.asImageBitmap(),
                                contentDescription = "Video Frame",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .rotate(clipToRender.rotationDegrees.toFloat())
                                    .graphicsLayer {
                                        scaleX = if (clipToRender.isFlippedH) -1f else 1f
                                        scaleY = if (clipToRender.isFlippedV) -1f else 1f
                                    },
                                contentScale = ContentScale.Fit,
                                colorFilter = ColorFilter.colorMatrix(cm)
                            )
                        } else {
                            AsyncImage(
                                model = clipToRender.uriString,
                                contentDescription = "Video Frame",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .rotate(clipToRender.rotationDegrees.toFloat())
                                    .graphicsLayer {
                                        scaleX = if (clipToRender.isFlippedH) -1f else 1f
                                        scaleY = if (clipToRender.isFlippedV) -1f else 1f
                                    },
                                contentScale = ContentScale.Fit,
                                colorFilter = ColorFilter.colorMatrix(cm)
                            )
                        }

                        // Top Left: Status Indicators
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (clipToRender.isMuted) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xDD000000), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.VolumeOff, contentDescription = null, tint = WsAccentRed, modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("MUTED", fontSize = 8.sp, color = WsPureWhite, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            if (clipToRender.isChromaKeyEnabled) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xDD000000), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text("CHROMA", fontSize = 8.sp, color = WsAccentGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Picture-in-Picture (PIP) Floating Overlay
                        if (clipToRender.isPip) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(10.dp)
                                    .size(width = 100.dp, height = 64.dp)
                                    .border(1.5.dp, WsPureWhite, RoundedCornerShape(6.dp))
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF1E2022))
                            ) {
                                AsyncImage(
                                    model = clipToRender.uriString,
                                    contentDescription = "PIP Clip",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .background(Color(0xDD000000), RoundedCornerShape(bottomEnd = 4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("PIP", fontSize = 7.5.sp, color = WsPureWhite, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = WsMediumGray, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("No Clips in Timeline", color = WsMediumGray, fontSize = 12.sp)
                        }
                    }

                    // Active Subtitle Overlay Rendering
                    val currentSubtitle = subtitles.firstOrNull {
                        playheadMs in it.startTimelineMs..it.endTimelineMs
                    }
                    if (currentSubtitle != null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                                .background(Color(currentSubtitle.bgColor), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = currentSubtitle.text,
                                color = Color(currentSubtitle.textColor),
                                fontSize = currentSubtitle.fontSizeSp.sp,
                                fontWeight = if (currentSubtitle.isBold) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Play / Pause Overlay Button
                IconButton(
                    onClick = { viewModel.togglePlayback() },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(52.dp)
                        .background(Color(0x66000000), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = WsPureWhite,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Timecode Badge (Bottom Left)
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xAA000000)
                ) {
                    Text(
                        text = "${formatTimecode(playheadMs)} / ${formatTimecode(totalDurationMs)}",
                        color = WsPureWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Video-to-Photo Snapshot Button (Top Right)
                IconButton(
                    onClick = {
                        val photoUri = viewModel.captureCurrentFrameAsPhoto()
                        Toast.makeText(context, "Frame Saved to Photos", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(34.dp)
                        .background(Color(0x88000000), CircleShape)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Video to Photo",
                        tint = WsPureWhite,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            // ----------------------------------------------------
            // 2. TIMELINE ACTION CONTROLS STRIP
            // ----------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .background(WsSurfaceCard)
                    .border(androidx.compose.foundation.BorderStroke(1.dp, WsBorder))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Split, Duplicate, Delete, Reorder
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Split
                    IconButton(
                        onClick = { viewModel.splitClipAtPlayhead() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Crop, contentDescription = "Split Clip", tint = WsTextPrimary, modifier = Modifier.size(17.dp))
                    }

                    // Duplicate
                    IconButton(
                        onClick = { viewModel.duplicateSelectedClip() },
                        modifier = Modifier.size(32.dp),
                        enabled = clips.isNotEmpty()
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate Clip", tint = if (clips.isNotEmpty()) WsTextPrimary else WsTextMuted, modifier = Modifier.size(16.dp))
                    }

                    // Delete Clip
                    IconButton(
                        onClick = { viewModel.deleteSelectedClip() },
                        modifier = Modifier.size(32.dp),
                        enabled = clips.size > 1
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Clip",
                            tint = if (clips.size > 1) WsTextPrimary else WsTextMuted,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Reorder: Move Left
                    IconButton(
                        onClick = { viewModel.moveClip(selectedIndex, selectedIndex - 1) },
                        modifier = Modifier.size(32.dp),
                        enabled = selectedIndex > 0
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Move Left",
                            tint = if (selectedIndex > 0) WsTextPrimary else WsTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Reorder: Move Right
                    IconButton(
                        onClick = { viewModel.moveClip(selectedIndex, selectedIndex + 1) },
                        modifier = Modifier.size(32.dp),
                        enabled = selectedIndex < clips.size - 1
                    ) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Move Right",
                            tint = if (selectedIndex < clips.size - 1) WsTextPrimary else WsTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Mute / Unmute selected clip
                    IconButton(
                        onClick = { viewModel.toggleClipMute() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (selectedClip?.isMuted == true) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Toggle Mute",
                            tint = if (selectedClip?.isMuted == true) WsAccentRed else WsTextPrimary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // Zoom & Add Media (+)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { timelineZoom = (timelineZoom - 0.25f).coerceAtLeast(0.5f) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = WsTextPrimary, modifier = Modifier.size(15.dp))
                    }
                    Text(text = "${(timelineZoom * 100).toInt()}%", fontSize = 10.sp, color = WsTextSecondary)
                    IconButton(
                        onClick = { timelineZoom = (timelineZoom + 0.25f).coerceAtMost(3.0f) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = WsTextPrimary, modifier = Modifier.size(15.dp))
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Add Video / Photo Button
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                addMediaLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                )
                            },
                        color = WsButtonDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WsElectricCyan.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = WsElectricCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = "Media", color = WsTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // 3. CAPCUT/VN MULTI-TRACK TIMELINE WITH CONTINUOUS THUMBNAILS
            // ----------------------------------------------------
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.40f)
                    .background(WsTimelineBg)
            ) {
                val containerWidthDp = maxWidth
                val centerOffsetDp = containerWidthDp / 2
                val dpPerSecond = (50f * timelineZoom).coerceIn(20f, 200f)
                val totalSeconds = (totalDurationMs.toFloat() / 1000f).coerceAtLeast(0.1f)
                val timelineContentWidthDp = (totalSeconds * dpPerSecond).dp.coerceAtLeast(360.dp)

                val scrollState = rememberScrollState()

                // Sync scrollState with playhead when not actively dragged by user
                LaunchedEffect(playheadMs, isPlaying) {
                    if (!scrollState.isScrollInProgress) {
                        val maxScroll = scrollState.maxValue
                        if (maxScroll > 0 && totalDurationMs > 0) {
                            val targetScroll = ((playheadMs.toFloat() / totalDurationMs.toFloat()) * maxScroll).roundToInt()
                            if (Math.abs(scrollState.value - targetScroll) > 2) {
                                scrollState.scrollTo(targetScroll)
                            }
                        }
                    }
                }

                // When user scrolls/swipes timeline horizontally, update playhead smoothly
                LaunchedEffect(scrollState.value, scrollState.isScrollInProgress) {
                    if (scrollState.isScrollInProgress) {
                        val maxScroll = scrollState.maxValue
                        if (maxScroll > 0 && totalDurationMs > 0) {
                            val progress = (scrollState.value.toFloat() / maxScroll.toFloat()).coerceIn(0f, 1f)
                            val targetMs = (progress * totalDurationMs).toLong()
                            viewModel.setPlayheadMs(targetMs)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(scrollState)
                ) {
                    // Leading spacer so 0:00.00 aligns exactly with center playhead
                    Spacer(modifier = Modifier.width(centerOffsetDp))

                    Column(
                        modifier = Modifier
                            .width(timelineContentWidthDp)
                            .fillMaxHeight()
                    ) {
                        // Time Ruler
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                                .pointerInput(totalDurationMs) {
                                    detectTapGestures { offset ->
                                        val ratio = (offset.x / size.width).coerceIn(0f, 1f)
                                        val targetMs = (ratio * totalDurationMs).toLong()
                                        viewModel.setPlayheadMs(targetMs)
                                    }
                                }
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF141619))
                            ) {
                                val rulerWidth = size.width
                                if (rulerWidth <= 0f || totalDurationMs <= 0L) return@Canvas

                                val pxPerSec = (rulerWidth / (totalDurationMs.toFloat() / 1000f)).coerceAtLeast(1f)
                                val secStep = when {
                                    pxPerSec >= 80f -> 1
                                    pxPerSec >= 35f -> 2
                                    pxPerSec >= 15f -> 5
                                    pxPerSec >= 6f -> 10
                                    else -> 30
                                }
                                val totalSec = (totalDurationMs / 1000L).toInt()

                                val textPaint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.parseColor("#94A3B8")
                                    textSize = 22f
                                    isAntiAlias = true
                                }

                                for (s in 0..totalSec step secStep) {
                                    val x = (s * 1000L.toFloat() / totalDurationMs.toFloat()) * rulerWidth
                                    val isMajor = (s % (secStep * 5) == 0) || s == 0
                                    val tickHeight = if (isMajor) 14f else 8f
                                    val strokeW = if (isMajor) 2f else 1.2f

                                    drawLine(
                                        color = if (isMajor) Color(0xFFE2E8F0) else Color(0xFF64748B),
                                        start = Offset(x, 0f),
                                        end = Offset(x, tickHeight),
                                        strokeWidth = strokeW
                                    )

                                    if (isMajor) {
                                        val min = s / 60
                                        val sec = s % 60
                                        val label = String.format(Locale.US, "%02d:%02d", min, sec)
                                        drawContext.canvas.nativeCanvas.drawText(
                                            label,
                                            (x + 4f).coerceAtMost(rulerWidth - 42f),
                                            20f,
                                            textPaint
                                        )
                                    }
                                }
                            }
                        }

                        // Track 1: Subtitles & Text Overlays Track
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(28.dp)
                                .background(Color(0xFF151618))
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (subtitles.isEmpty()) {
                                Text(
                                    text = "  + Subtitles: Tap Text tool below or Auto-Captions",
                                    fontSize = 9.5.sp,
                                    color = Color(0xFF6B7280)
                                )
                            } else {
                                subtitles.forEach { sub ->
                                    val subStartRatio = (sub.startTimelineMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
                                    val subEndRatio = (sub.endTimelineMs.toFloat() / totalDurationMs.toFloat()).coerceIn(subStartRatio, 1f)
                                    val subWidth = ((subEndRatio - subStartRatio) * timelineContentWidthDp.value).dp.coerceAtLeast(60.dp)

                                    Surface(
                                        modifier = Modifier
                                            .width(subWidth)
                                            .padding(horizontal = 2.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .clickable {
                                                subtitleToEdit = sub
                                                subtitleEditText = sub.text
                                            },
                                        color = Color(0xFF2C3E50)
                                    ) {
                                        Text(
                                            text = sub.text,
                                            fontSize = 9.sp,
                                            color = WsPureWhite,
                                            maxLines = 1,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Track 2: Main Video Clips Track (Continuous Thumbnails Filmstrip)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .background(WsTimelineTrack)
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            clips.forEachIndexed { index, clip ->
                                val isSelected = index == selectedIndex
                                val clipDurationRatio = (clip.effectiveDurationMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
                                val clipWidthDp = (clipDurationRatio * timelineContentWidthDp.value).dp.coerceAtLeast(70.dp)

                                Box(
                                    modifier = Modifier
                                        .width(clipWidthDp)
                                        .fillMaxHeight()
                                        .padding(horizontal = 2.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) Color(0xFF162238) else Color(0xFF121419))
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) WsElectricCyan else WsBorder,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .clickable { viewModel.selectClip(index) }
                                ) {
                                    // Filmstrip of continuous thumbnail tiles across entire clip duration
                                    val tileWidthDp = 52.dp
                                    val tileCount = (clipWidthDp.value / tileWidthDp.value).toInt().coerceAtLeast(1)
                                    val thumbnailTimes = remember(clip.id, clip.trimStartMs, clip.trimEndMs, tileCount) {
                                        VideoFrameProvider.getClipThumbnailTimes(clip, tileCount)
                                    }

                                    Row(modifier = Modifier.fillMaxSize()) {
                                        thumbnailTimes.forEach { timeMs ->
                                            VideoThumbnailTile(
                                                uriString = clip.uriString,
                                                timeMs = timeMs,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight()
                                            )
                                        }
                                    }

                                    // Gradient shadow overlay for legible text
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color(0x33000000),
                                                        Color.Transparent,
                                                        Color(0xAA000000)
                                                    )
                                                )
                                            )
                                    )

                                    // Trim handles on selected clip
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.CenterStart)
                                                .width(6.dp)
                                                .fillMaxHeight()
                                                .background(WsElectricCyan, RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                                .width(6.dp)
                                                .fillMaxHeight()
                                                .background(WsElectricCyan, RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                                        )
                                    }

                                    // Clip info overlay (bottom)
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .background(Color(0xDD000000), RoundedCornerShape(bottomStart = 4.dp, topEnd = 4.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${clip.name} • ${formatTimecode(clip.effectiveDurationMs)}",
                                            fontSize = 8.5.sp,
                                            color = WsPureWhite,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        if (clip.speed != 1.0f) {
                                            Text(
                                                text = " • ${clip.speed}x",
                                                fontSize = 8.sp,
                                                color = WsAccentGold
                                            )
                                        }
                                        if (clip.isMuted) {
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Icon(Icons.Default.VolumeOff, contentDescription = null, tint = WsAccentRed, modifier = Modifier.size(9.dp))
                                        }
                                    }

                                    if (clip.transition != TransitionType.NONE) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                                .padding(end = 6.dp)
                                                .size(18.dp)
                                                .background(WsAccentGold, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("⚡", fontSize = 8.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Track 3: Audio & Music Track
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(34.dp)
                                .background(Color(0xFF141517))
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (audioTracks.isEmpty()) {
                                Text(
                                    text = "  + Audio: Import music or record voiceover below",
                                    fontSize = 9.5.sp,
                                    color = Color(0xFF6B7280)
                                )
                            } else {
                                audioTracks.forEach { track ->
                                    val startRatio = (track.startTimelineMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
                                    val endRatio = ((track.startTimelineMs + track.durationMs).toFloat() / totalDurationMs.toFloat()).coerceIn(startRatio, 1f)
                                    val trackWidth = ((endRatio - startRatio) * timelineContentWidthDp.value).dp.coerceAtLeast(80.dp)

                                    Surface(
                                        modifier = Modifier
                                            .width(trackWidth)
                                            .padding(horizontal = 3.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = if (track.isVoiceRecording) Color(0xFF991B1B) else Color(0xFF1E3A8A)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                if (track.isVoiceRecording) Icons.Default.Mic else Icons.Default.MusicNote,
                                                contentDescription = null,
                                                tint = WsPureWhite,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${track.title} (${formatTimecode(track.durationMs)})",
                                                fontSize = 9.sp,
                                                color = WsPureWhite,
                                                maxLines = 1
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Remove Track",
                                                tint = Color.LightGray,
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clickable { viewModel.removeAudioTrack(track.id) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Trailing spacer so totalDurationMs aligns exactly with center playhead
                    Spacer(modifier = Modifier.width(centerOffsetDp))
                }

                // Professional Center Playhead Line (CapCut / VN style)
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(2.5.dp)
                        .fillMaxHeight()
                        .background(WsElectricCyan)
                )
                // Top Playhead Pin Marker
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(width = 12.dp, height = 14.dp)
                        .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                        .background(WsElectricCyan)
                )
            }

            // ----------------------------------------------------
            // 4. BOTTOM PROFESSIONAL TOOLBAR
            // ----------------------------------------------------
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .background(WsSurfaceCard)
                    .border(androidx.compose.foundation.BorderStroke(1.dp, WsBorder))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    EditorToolItem(
                        icon = Icons.Default.Crop,
                        label = "Trim/Split",
                        isSelected = activeTool == VideoEditorTool.TRIM_SPLIT,
                        onClick = { viewModel.selectVideoTool(VideoEditorTool.TRIM_SPLIT) }
                    )
                }
                item {
                    EditorToolItem(
                        icon = Icons.Default.Speed,
                        label = "Speed",
                        isSelected = activeTool == VideoEditorTool.SPEED,
                        onClick = { viewModel.selectVideoTool(VideoEditorTool.SPEED) }
                    )
                }
                item {
                    EditorToolItem(
                        icon = Icons.Default.VolumeUp,
                        label = "Volume/Mute",
                        isSelected = activeTool == VideoEditorTool.AUDIO,
                        onClick = { viewModel.selectVideoTool(VideoEditorTool.AUDIO) }
                    )
                }
                item {
                    EditorToolItem(
                        icon = Icons.Default.Transform,
                        label = "Rotate/Crop",
                        isSelected = activeTool == VideoEditorTool.TRANSFORM,
                        onClick = { viewModel.selectVideoTool(VideoEditorTool.TRANSFORM) }
                    )
                }
                item {
                    EditorToolItem(
                        icon = Icons.Default.ColorLens,
                        label = "Filters",
                        isSelected = activeTool == VideoEditorTool.FILTERS,
                        onClick = { viewModel.selectVideoTool(VideoEditorTool.FILTERS) }
                    )
                }
                item {
                    EditorToolItem(
                        icon = Icons.Default.Tune,
                        label = "Adjust",
                        isSelected = activeTool == VideoEditorTool.ADJUST,
                        onClick = { viewModel.selectVideoTool(VideoEditorTool.ADJUST) }
                    )
                }
                item {
                    EditorToolItem(
                        icon = Icons.Default.Subtitles,
                        label = "Text/Captions",
                        isSelected = activeTool == VideoEditorTool.TEXT_SUBTITLES,
                        onClick = { viewModel.selectVideoTool(VideoEditorTool.TEXT_SUBTITLES) }
                    )
                }
                item {
                    EditorToolItem(
                        icon = Icons.Default.Animation,
                        label = "Transitions",
                        isSelected = activeTool == VideoEditorTool.TRANSITION,
                        onClick = { viewModel.selectVideoTool(VideoEditorTool.TRANSITION) }
                    )
                }
                item {
                    EditorToolItem(
                        icon = Icons.Default.PictureInPicture,
                        label = "Overlay/PIP",
                        isSelected = activeTool == VideoEditorTool.PIP,
                        onClick = { viewModel.selectVideoTool(VideoEditorTool.PIP) }
                    )
                }
                item {
                    EditorToolItem(
                        icon = Icons.Default.GraphicEq,
                        label = "Chroma Key",
                        isSelected = activeTool == VideoEditorTool.CHROMA_KEY,
                        onClick = { viewModel.selectVideoTool(VideoEditorTool.CHROMA_KEY) }
                    )
                }
            }
        }
    }

    // ----------------------------------------------------
    // 5. TOOL BOTTOM SHEETS
    // ----------------------------------------------------
    if (activeTool != VideoEditorTool.NONE) {
        ToolBottomSheet(
            activeTool = activeTool,
            clip = selectedClip,
            viewModel = viewModel,
            onDismiss = { viewModel.selectVideoTool(VideoEditorTool.NONE) },
            onImportAudio = { audioPickerLauncher.launch("audio/*") }
        )
    }

    // Subtitle Edit Dialog
    subtitleToEdit?.let { sub ->
        AlertDialog(
            containerColor = WsSurfaceCardElevated,
            titleContentColor = WsTextPrimary,
            textContentColor = WsTextPrimary,
            onDismissRequest = { subtitleToEdit = null },
            title = { Text("Edit Subtitle", fontWeight = FontWeight.Bold, color = WsTextPrimary) },
            text = {
                OutlinedTextField(
                    value = subtitleEditText,
                    onValueChange = { subtitleEditText = it },
                    label = { Text("Overlay Text") },
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedTextColor = WsTextPrimary,
                        unfocusedTextColor = WsTextPrimary,
                        focusedBorderColor = WsElectricCyan,
                        unfocusedBorderColor = WsBorder,
                        focusedLabelColor = WsElectricCyan,
                        unfocusedLabelColor = WsTextSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (subtitleEditText.isNotBlank()) {
                            viewModel.updateSubtitle(sub.id, subtitleEditText.trim())
                        }
                        subtitleToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WsElectricBlue)
                ) {
                    Text("Save", color = WsTextPrimary)
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        viewModel.removeSubtitle(sub.id)
                        subtitleToEdit = null
                    }) {
                        Text("Delete", color = WsAccentRed)
                    }
                    TextButton(onClick = { subtitleToEdit = null }) {
                        Text("Cancel", color = WsTextSecondary)
                    }
                }
            }
        )
    }

    // Export Dialog (480p, 720p, 1080p, 4K)
    if (showExportDialog) {
        ExportDialog(
            isVideo = true,
            isPro = isPro,
            isExporting = isExporting,
            exportProgress = exportProgress,
            exportedFile = exportedFile,
            onDismiss = { viewModel.resetExportState() },
            onStartExport = { config ->
                viewModel.startExport(config)
            },
            onWatchRewardedFor4k = { onEarned ->
                viewModel.monetization.watchRewardedAdFor4k(onEarned)
            }
        )
    }
}

@Composable
private fun EditorToolItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    if (isSelected) WsElectricBlue.copy(alpha = 0.22f) else WsButtonDark,
                    CircleShape
                )
                .border(
                    1.dp,
                    if (isSelected) WsElectricCyan else WsBorder,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) WsElectricCyan else WsTextSecondary,
                modifier = Modifier.size(19.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) WsTextPrimary else WsTextMuted
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolBottomSheet(
    activeTool: VideoEditorTool,
    clip: VideoClip?,
    viewModel: WsEditorViewModel,
    onDismiss: () -> Unit,
    onImportAudio: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = WsSurfaceCardElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp)
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (activeTool) {
                        VideoEditorTool.TRIM_SPLIT -> "Trim & Cut Clips"
                        VideoEditorTool.SPEED -> "Speed Control"
                        VideoEditorTool.AUDIO -> "Volume & Audio Controls"
                        VideoEditorTool.TRANSFORM -> "Crop & Transform"
                        VideoEditorTool.FILTERS -> "Cinematic Filters"
                        VideoEditorTool.ADJUST -> "Color Adjustments"
                        VideoEditorTool.TEXT_SUBTITLES -> "Text & Subtitles"
                        VideoEditorTool.TRANSITION -> "Transition Effects"
                        VideoEditorTool.PIP -> "Picture-in-Picture"
                        VideoEditorTool.CHROMA_KEY -> "Green Screen / Chroma Key"
                        else -> "Edit Tool"
                    },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = WsTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = WsTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (activeTool) {
                VideoEditorTool.TRIM_SPLIT -> {
                    if (clip != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.splitClipAtPlayhead()
                                        onDismiss()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = WsElectricBlue),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Crop, contentDescription = null, tint = WsTextPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Split at Playhead", fontSize = 12.sp, color = WsTextPrimary)
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.duplicateSelectedClip()
                                        Toast.makeText(context, "Clip Duplicated", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WsTextPrimary),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = WsTextPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Duplicate Clip", fontSize = 12.sp, color = WsTextPrimary)
                                }
                            }

                            // Trim Start / End controls
                            Text(
                                text = "TRIM DURATION: ${formatTimecode(clip.effectiveDurationMs)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WsTextSecondary
                            )

                            Column {
                                Text("Trim Start: ${formatTimecode(clip.trimStartMs)}", fontSize = 12.sp, color = WsTextPrimary)
                                Slider(
                                    value = clip.trimStartMs.toFloat(),
                                    onValueChange = {
                                        viewModel.trimSelectedClip(it.toLong(), clip.trimEndMs)
                                    },
                                    valueRange = 0f..(clip.trimEndMs - 300L).coerceAtLeast(0L).toFloat(),
                                    colors = SliderDefaults.colors(
                                        thumbColor = WsElectricCyan,
                                        activeTrackColor = WsElectricBlue,
                                        inactiveTrackColor = WsSurfaceCard
                                    )
                                )
                            }

                            Column {
                                Text("Trim End: ${formatTimecode(clip.trimEndMs)}", fontSize = 12.sp, color = WsTextPrimary)
                                Slider(
                                    value = clip.trimEndMs.toFloat(),
                                    onValueChange = {
                                        viewModel.trimSelectedClip(clip.trimStartMs, it.toLong())
                                    },
                                    valueRange = (clip.trimStartMs + 300L).toFloat()..clip.originalDurationMs.toFloat(),
                                    colors = SliderDefaults.colors(
                                        thumbColor = WsElectricCyan,
                                        activeTrackColor = WsElectricBlue,
                                        inactiveTrackColor = WsSurfaceCard
                                    )
                                )
                            }
                        }
                    }
                }

                VideoEditorTool.SPEED -> {
                    // Speed Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.5f, 2.0f, 4.0f).forEach { speedVal ->
                            val isSelected = clip?.speed == speedVal
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setClipSpeed(speedVal) },
                                color = if (isSelected) WsElectricBlue.copy(alpha = 0.22f) else WsButtonDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) WsElectricCyan else WsBorder),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${speedVal}x",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp,
                                        color = if (isSelected) WsElectricCyan else WsTextSecondary
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Smooth Speed Curve: ${String.format(Locale.US, "%.2f", clip?.speed ?: 1.0f)}x", fontSize = 12.sp, color = WsTextSecondary)
                    Slider(
                        value = clip?.speed ?: 1.0f,
                        onValueChange = { viewModel.setClipSpeed(it) },
                        valueRange = 0.2f..6.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = WsElectricCyan,
                            activeTrackColor = WsElectricBlue,
                            inactiveTrackColor = WsSurfaceCard
                        )
                    )
                }

                VideoEditorTool.AUDIO -> {
                    var isRecording by remember { mutableStateOf(false) }

                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Volume slider and mute toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Clip Volume: ${((clip?.volume ?: 1f) * 100).toInt()}%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = WsTextPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Mute", fontSize = 12.sp, color = WsTextSecondary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = clip?.isMuted == true,
                                    onCheckedChange = { viewModel.toggleClipMute() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = WsPureWhite,
                                        checkedTrackColor = WsElectricBlue
                                    )
                                )
                            }
                        }

                        Slider(
                            value = clip?.volume ?: 1.0f,
                            onValueChange = { viewModel.setClipVolume(it) },
                            valueRange = 0f..2.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = WsElectricCyan,
                                activeTrackColor = WsElectricBlue,
                                inactiveTrackColor = WsSurfaceCard
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Audio Import & Sound Effects
                        Text("IMPORT MUSIC & SOUNDS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = WsTextSecondary)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onImportAudio,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WsButtonDark),
                                border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder)
                            ) {
                                Icon(Icons.Default.MusicNote, contentDescription = null, tint = WsElectricCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Import Audio", fontSize = 12.sp, color = WsTextPrimary)
                            }

                            Button(
                                onClick = {
                                    if (isRecording) {
                                        val recordedFile = viewModel.voiceRecorder.stopRecording()
                                        isRecording = false
                                        if (recordedFile != null) {
                                            viewModel.addAudioTrack("Voiceover", isVoiceRecord = true, uri = recordedFile.absolutePath)
                                            Toast.makeText(context, "Voiceover Saved", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        viewModel.voiceRecorder.startRecording(
                                            onStarted = { isRecording = true },
                                            onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                                        )
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRecording) WsAccentRed else WsButtonDark,
                                    contentColor = WsTextPrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isRecording) WsAccentRed else WsBorder),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = if (isRecording) WsPureWhite else WsElectricCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isRecording) "Stop Rec" else "Record Mic", fontSize = 12.sp, color = WsTextPrimary)
                            }
                        }

                        // Preset SFX buttons
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val presets = listOf("Cinematic Whoosh", "Bass Drop", "Camera Shutter", "Pop Ambient")
                            items(presets) { sfx ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            viewModel.addAudioTrack(sfx, durationMs = 2500L)
                                            Toast.makeText(context, "$sfx Added", Toast.LENGTH_SHORT).show()
                                        },
                                    color = WsButtonDark,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder)
                                ) {
                                    Text(
                                        text = "+ $sfx",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = WsTextPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                VideoEditorTool.TRANSFORM -> {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("ASPECT RATIO", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = WsTextSecondary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AspectRatioOption.entries.forEach { option ->
                                val isSelected = viewModel.aspectRatio.collectAsState().value == option
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.setAspectRatio(option) },
                                    color = if (isSelected) WsElectricBlue.copy(alpha = 0.22f) else WsButtonDark,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) WsElectricCyan else WsBorder),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = option.label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSelected) WsElectricCyan else WsTextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("ORIENTATION & FLIP", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = WsTextSecondary)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.rotateSelectedClip() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WsButtonDark),
                                border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder)
                            ) {
                                Icon(Icons.Default.Rotate90DegreesCw, contentDescription = null, tint = WsElectricCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Rotate 90°", fontSize = 12.sp, color = WsTextPrimary)
                            }
                            Button(
                                onClick = { viewModel.flipSelectedClipHorizontal() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WsButtonDark),
                                border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder)
                            ) {
                                Icon(Icons.Default.Flip, contentDescription = null, tint = WsElectricCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Flip Horiz", fontSize = 12.sp, color = WsTextPrimary)
                            }
                            Button(
                                onClick = { viewModel.flipSelectedClipVertical() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WsButtonDark),
                                border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder)
                            ) {
                                Icon(Icons.Default.Flip, contentDescription = null, tint = WsElectricCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Flip Vert", fontSize = 12.sp, color = WsTextPrimary)
                            }
                        }
                    }
                }

                VideoEditorTool.FILTERS -> {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        itemsIndexed(FilterPreset.entries) { _, preset ->
                            val isSelected = clip?.filter == preset
                            Surface(
                                modifier = Modifier
                                    .size(width = 86.dp, height = 74.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.applyFilterToSelectedClip(preset) },
                                color = if (isSelected) WsElectricBlue.copy(alpha = 0.22f) else WsButtonDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) WsElectricCyan else WsBorder),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.ColorLens,
                                        contentDescription = null,
                                        tint = if (isSelected) WsElectricCyan else WsTextSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = preset.displayName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) WsElectricCyan else WsTextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                VideoEditorTool.ADJUST -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        AdjustmentSlider(
                            label = "Brightness",
                            value = clip?.brightness ?: 0f,
                            range = -0.5f..0.5f,
                            onValueChange = { viewModel.updateColorAdjustment(brightness = it) }
                        )
                        AdjustmentSlider(
                            label = "Contrast",
                            value = clip?.contrast ?: 1f,
                            range = 0.5f..2.0f,
                            onValueChange = { viewModel.updateColorAdjustment(contrast = it) }
                        )
                        AdjustmentSlider(
                            label = "Saturation",
                            value = clip?.saturation ?: 1f,
                            range = 0f..2.5f,
                            onValueChange = { viewModel.updateColorAdjustment(saturation = it) }
                        )
                        AdjustmentSlider(
                            label = "Sharpness",
                            value = clip?.sharpness ?: 0f,
                            range = 0f..1f,
                            onValueChange = { viewModel.updateColorAdjustment(sharpness = it) }
                        )
                    }
                }

                VideoEditorTool.TEXT_SUBTITLES -> {
                    var newSubtitleInput by remember { mutableStateOf("") }
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                viewModel.generateAutoCaptions()
                                onDismiss()
                                Toast.makeText(context, "AI Synced Captions Generated", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = WsElectricBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = WsTextPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate AI Auto Captions", fontWeight = FontWeight.Bold, color = WsTextPrimary)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newSubtitleInput,
                                onValueChange = { newSubtitleInput = it },
                                placeholder = { Text("Enter text overlay...", color = WsTextMuted) },
                                singleLine = true,
                                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = WsTextPrimary,
                                    unfocusedTextColor = WsTextPrimary,
                                    focusedBorderColor = WsElectricCyan,
                                    unfocusedBorderColor = WsBorder
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    if (newSubtitleInput.isNotBlank()) {
                                        viewModel.addSubtitle(newSubtitleInput.trim())
                                        newSubtitleInput = ""
                                        onDismiss()
                                        Toast.makeText(context, "Text Added to Timeline", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WsElectricBlue)
                            ) {
                                Text("Add", color = WsTextPrimary)
                            }
                        }
                    }
                }

                VideoEditorTool.TRANSITION -> {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        itemsIndexed(TransitionType.entries) { _, trans ->
                            val isSelected = clip?.transition == trans
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setClipTransition(trans) },
                                color = if (isSelected) WsElectricBlue.copy(alpha = 0.22f) else WsButtonDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) WsElectricCyan else WsBorder),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = trans.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) WsElectricCyan else WsTextSecondary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }

                VideoEditorTool.PIP -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable Picture-in-Picture Overlay", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = WsTextPrimary)
                        Switch(
                            checked = clip?.isPip == true,
                            onCheckedChange = { viewModel.setPipSettings(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = WsPureWhite,
                                checkedTrackColor = WsElectricBlue
                            )
                        )
                    }
                }

                VideoEditorTool.CHROMA_KEY -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable Green Screen Cutout", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = WsTextPrimary)
                        Switch(
                            checked = clip?.isChromaKeyEnabled == true,
                            onCheckedChange = { viewModel.toggleChromaKey(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = WsPureWhite,
                                checkedTrackColor = WsElectricBlue
                            )
                        )
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun AdjustmentSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = WsTextPrimary)
            Text(text = String.format(Locale.US, "%.2f", value), fontSize = 12.sp, color = WsTextSecondary)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = WsElectricCyan,
                activeTrackColor = WsElectricBlue,
                inactiveTrackColor = WsSurfaceCard
            )
        )
    }
}

private fun formatTimecode(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val millis = (ms % 1000) / 10
    return String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, millis)
}

@Composable
private fun VideoThumbnailTile(
    uriString: String,
    timeMs: Long,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var frameBitmap by remember(uriString, timeMs) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(uriString, timeMs) {
        val bmp = VideoFrameProvider.getFrameAtTime(
            context,
            uriString,
            timeMs,
            targetWidth = 140,
            targetHeight = 100
        )
        if (bmp != null) {
            frameBitmap = bmp
        }
    }

    Box(
        modifier = modifier
            .background(Color(0xFF16181B))
            .border(0.5.dp, Color(0xFF2A2D35))
    ) {
        if (frameBitmap != null) {
            Image(
                bitmap = frameBitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            AsyncImage(
                model = uriString,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}
