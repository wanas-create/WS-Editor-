package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Wallpaper
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.AspectRatioOption
import com.example.model.DrawingPath
import com.example.model.DrawingPoint
import com.example.model.FilterPreset
import com.example.model.PhotoTextOverlay
import com.example.ui.components.ExportDialog
import com.example.ui.theme.WsAccentGold
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
import com.example.viewmodel.PhotoEditorTool
import com.example.viewmodel.ScreenState
import com.example.viewmodel.WsEditorViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoEditorScreen(viewModel: WsEditorViewModel) {
    val context = LocalContext.current
    val photoState by viewModel.photoEditState.collectAsState()
    val activeTool by viewModel.activePhotoTool.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    val isExporting by viewModel.isExporting.collectAsState()
    val exportProgress by viewModel.exportProgress.collectAsState()
    val exportedFile by viewModel.exportedFile.collectAsState()
    val showExportDialog by viewModel.showExportDialog.collectAsState()
    val isPro by viewModel.monetization.isProUser.collectAsState()

    val canUndo by viewModel.canPhotoUndo.collectAsState()
    val canRedo by viewModel.canPhotoRedo.collectAsState()
    val drawingPaths by viewModel.drawingPaths.collectAsState()

    var showAspectMenu by remember { mutableStateOf(false) }
    var isFullScreenPreview by remember { mutableStateOf(false) }

    // Brush controls
    var currentBrushColor by remember { mutableStateOf(Color.White) }
    var currentBrushWidth by remember { mutableFloatStateOf(8f) }
    var currentBrushOpacity by remember { mutableFloatStateOf(1.0f) }
    var isEraserMode by remember { mutableStateOf(false) }

    // Text overlay editing state
    var selectedTextOverlay by remember { mutableStateOf<PhotoTextOverlay?>(null) }
    var showTextDialog by remember { mutableStateOf(false) }

    // Phone Gallery Photo Picker
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            if (photoState == null) {
                viewModel.createPhotoProject(uri)
            } else {
                viewModel.replacePhotoUri(uri)
            }
            Toast.makeText(context, "Photo loaded from gallery", Toast.LENGTH_SHORT).show()
        }
    }

    BackHandler {
        viewModel.saveCurrentProject(asDraft = true)
        viewModel.navigateTo(ScreenState.HOME)
    }

    if (photoState == null) {
        Scaffold(
            containerColor = WsBackground,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = WsBackground,
                        scrolledContainerColor = WsBackground
                    ),
                    navigationIcon = {
                        IconButton(onClick = { viewModel.navigateTo(ScreenState.HOME) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = WsTextPrimary)
                        }
                    },
                    title = {
                        Text(
                            text = "Photo Editor",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = WsTextPrimary
                        )
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(28.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .background(WsSurfaceCardElevated, CircleShape)
                            .border(1.dp, WsBorderGlow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = WsElectricCyan,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                    Text(
                        text = "Choose Photo to Edit",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = WsTextPrimary
                    )
                    Text(
                        text = "Select any picture from your gallery to crop, adjust color, apply professional filters, draw, add text, and use AI tools.",
                        fontSize = 13.sp,
                        color = WsTextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = {
                            galleryPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WsElectricBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = WsTextPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select from Gallery", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WsTextPrimary)
                    }
                }
            }
        }
        return
    }

    val state = photoState!!

    Scaffold(
        containerColor = WsBackground,
        topBar = {
            if (!isFullScreenPreview) {
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
                                text = activeProject?.title ?: "Photo Editor",
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
                        // Undo
                        IconButton(
                            onClick = { viewModel.photoUndo() },
                            enabled = canUndo,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.Undo,
                                contentDescription = "Undo",
                                tint = if (canUndo) WsElectricCyan else WsTextMuted,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        // Redo
                        IconButton(
                            onClick = { viewModel.photoRedo() },
                            enabled = canRedo,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.Redo,
                                contentDescription = "Redo",
                                tint = if (canRedo) WsElectricCyan else WsTextMuted,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        // Reset edits
                        IconButton(
                            onClick = {
                                viewModel.resetPhotoEdits()
                                Toast.makeText(context, "Edits Reset to Original", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = WsTextPrimary,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        // Replace photo from gallery
                        IconButton(
                            onClick = {
                                galleryPickerLauncher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = "Replace Photo",
                                tint = WsTextPrimary,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        // Aspect Ratio Menu
                        Box {
                            TextButton(onClick = { showAspectMenu = true }) {
                                Icon(Icons.Default.AspectRatio, contentDescription = null, tint = WsElectricCyan, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(text = state.aspectRatio.label, color = WsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
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
                                            viewModel.updatePhotoState { it.copy(aspectRatio = option) }
                                            showAspectMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Export Button
                        Button(
                            onClick = { viewModel.showExportSheet(true) },
                            colors = ButtonDefaults.buttonColors(containerColor = WsElectricBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .testTag("photo_export_button")
                        ) {
                            Text(text = "Export", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WsTextPrimary)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ----------------------------------------------------
            // 1. MAIN PHOTO PREVIEW / CANVAS
            // ----------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF0C0D0E))
                    .padding(if (isFullScreenPreview) 0.dp else 12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Viewport aspect ratio container
                val ratioModifier = if (state.aspectRatio == AspectRatioOption.FREE) {
                    Modifier.fillMaxSize()
                } else {
                    Modifier
                        .fillMaxHeight()
                        .aspectRatio(state.aspectRatio.ratio)
                }

                Box(
                    modifier = ratioModifier
                        .background(Color.Black)
                        .border(1.dp, Color(0xFF26282B)),
                    contentAlignment = Alignment.Center
                ) {
                    // Background Replacement Layer
                    if (state.backgroundReplacement != null) {
                        BackgroundReplacementBackdrop(state.backgroundReplacement)
                    }

                    // Photo Image with ColorMatrix (Brightness, Contrast, Saturation, Sharpness, Exposure, Warmth, Filter)
                    val cm = remember(state) {
                        val androidMatrix = android.graphics.ColorMatrix()
                        androidMatrix.setSaturation(state.saturation)
                        val contrast = state.contrast
                        val translate = (-0.5f * contrast + 0.5f) * 255f + ((state.brightness + state.exposure) * 128f)
                        androidMatrix.postConcat(android.graphics.ColorMatrix(floatArrayOf(
                            contrast, 0f, 0f, 0f, translate,
                            0f, contrast, 0f, 0f, translate,
                            0f, 0f, contrast, 0f, translate,
                            0f, 0f, 0f, 1f, 0f
                        )))
                        if (state.warmth != 0f) {
                            val wMat = android.graphics.ColorMatrix(floatArrayOf(
                                1f + (state.warmth * 0.2f), 0f, 0f, 0f, state.warmth * 20f,
                                0f, 1f, 0f, 0f, 0f,
                                0f, 0f, 1f - (state.warmth * 0.2f), 0f, -state.warmth * 20f,
                                0f, 0f, 0f, 1f, 0f
                            ))
                            androidMatrix.postConcat(wMat)
                        }
                        when (state.filter) {
                            FilterPreset.CINEMATIC -> {
                                val fMat = android.graphics.ColorMatrix(floatArrayOf(
                                    1.1f, 0f, 0f, 0f, -10f,
                                    0f, 1.05f, 0f, 0f, -5f,
                                    0f, 0f, 0.95f, 0f, 15f,
                                    0f, 0f, 0f, 1f, 0f
                                ))
                                androidMatrix.postConcat(fMat)
                            }
                            FilterPreset.MONO -> {
                                val mono = android.graphics.ColorMatrix()
                                mono.setSaturation(0f)
                                androidMatrix.postConcat(mono)
                            }
                            FilterPreset.CYBER -> {
                                val cyberMat = android.graphics.ColorMatrix(floatArrayOf(
                                    1.2f, 0f, 0.1f, 0f, 0f,
                                    0f, 0.9f, 0.2f, 0f, -10f,
                                    0.2f, 0f, 1.3f, 0f, 20f,
                                    0f, 0f, 0f, 1f, 0f
                                ))
                                androidMatrix.postConcat(cyberMat)
                            }
                            FilterPreset.WARM -> {
                                val warmMat = android.graphics.ColorMatrix(floatArrayOf(
                                    1.2f, 0f, 0f, 0f, 15f,
                                    0f, 1.05f, 0f, 0f, 5f,
                                    0f, 0f, 0.9f, 0f, -15f,
                                    0f, 0f, 0f, 1f, 0f
                                ))
                                androidMatrix.postConcat(warmMat)
                            }
                            FilterPreset.TEAL_ORANGE -> {
                                val toMat = android.graphics.ColorMatrix(floatArrayOf(
                                    1.3f, 0f, 0f, 0f, 20f,
                                    0f, 1.0f, 0.1f, 0f, 0f,
                                    0f, 0.1f, 1.2f, 0f, -10f,
                                    0f, 0f, 0f, 1f, 0f
                                ))
                                androidMatrix.postConcat(toMat)
                            }
                            FilterPreset.VINTAGE -> {
                                val vintageMat = android.graphics.ColorMatrix(floatArrayOf(
                                    0.9f, 0f, 0f, 0f, 25f,
                                    0f, 0.85f, 0f, 0f, 20f,
                                    0f, 0f, 0.75f, 0f, 10f,
                                    0f, 0f, 0f, 1f, 0f
                                ))
                                androidMatrix.postConcat(vintageMat)
                            }
                            FilterPreset.DRAMATIC -> {
                                val dramMat = android.graphics.ColorMatrix(floatArrayOf(
                                    1.25f, 0f, 0f, 0f, -20f,
                                    0f, 1.25f, 0f, 0f, -20f,
                                    0f, 0f, 1.25f, 0f, -20f,
                                    0f, 0f, 0f, 1f, 0f
                                ))
                                androidMatrix.postConcat(dramMat)
                            }
                            FilterPreset.VIVID -> {
                                val vivid = android.graphics.ColorMatrix()
                                vivid.setSaturation(1.6f)
                                androidMatrix.postConcat(vivid)
                            }
                            FilterPreset.CLEAN -> {
                                val cleanMat = android.graphics.ColorMatrix(floatArrayOf(
                                    1.05f, 0f, 0f, 0f, 8f,
                                    0f, 1.05f, 0f, 0f, 8f,
                                    0f, 0f, 1.05f, 0f, 8f,
                                    0f, 0f, 0f, 1f, 0f
                                ))
                                androidMatrix.postConcat(cleanMat)
                            }
                            else -> {}
                        }
                        androidx.compose.ui.graphics.ColorMatrix(androidMatrix.array)
                    }

                    AsyncImage(
                        model = state.uriString,
                        contentDescription = "Photo Preview",
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(state.rotationDegrees.toFloat())
                            .graphicsLayer {
                                scaleX = if (state.isFlippedH) -1f else 1f
                                scaleY = if (state.isFlippedV) -1f else 1f
                            },
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.colorMatrix(cm)
                    )

                    // Artistic Frames Overlay
                    if (state.frameStyle != "None") {
                        FrameOverlayView(frameStyle = state.frameStyle)
                    }

                    // Text Overlays on Photo
                    state.textOverlays.forEach { textOverlay ->
                        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                            val xPos = maxWidth * textOverlay.posXRatio
                            val yPos = maxHeight * textOverlay.posYRatio

                            Box(
                                modifier = Modifier
                                    .padding(start = (xPos - 60.dp).coerceAtLeast(0.dp), top = (yPos - 20.dp).coerceAtLeast(0.dp))
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (textOverlay.hasBackgroundBox) Color(textOverlay.backgroundColor) else Color.Transparent
                                    )
                                    .clickable {
                                        selectedTextOverlay = textOverlay
                                        showTextDialog = true
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = textOverlay.text,
                                    fontSize = textOverlay.fontSizeSp.sp,
                                    color = Color(textOverlay.color),
                                    fontFamily = when (textOverlay.fontFamily) {
                                        "Serif" -> androidx.compose.ui.text.font.FontFamily.Serif
                                        "Monospace" -> androidx.compose.ui.text.font.FontFamily.Monospace
                                        "Sans" -> androidx.compose.ui.text.font.FontFamily.SansSerif
                                        "Cursive" -> androidx.compose.ui.text.font.FontFamily.Cursive
                                        else -> androidx.compose.ui.text.font.FontFamily.Default
                                    },
                                    fontWeight = if (textOverlay.isBold) FontWeight.Bold else FontWeight.Normal,
                                    fontStyle = if (textOverlay.isItalic) FontStyle.Italic else FontStyle.Normal,
                                    textAlign = when (textOverlay.alignment) {
                                        "Left" -> TextAlign.Left
                                        "Right" -> TextAlign.Right
                                        else -> TextAlign.Center
                                    }
                                )
                            }
                        }
                    }

                    // Interactive Drawing Canvas Overlay
                    var currentStrokePoints by remember { mutableStateOf<List<Offset>>(emptyList()) }

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(alpha = 0.99f)
                            .pointerInput(activeTool, isEraserMode, currentBrushColor, currentBrushWidth, currentBrushOpacity) {
                                if (activeTool == PhotoEditorTool.DRAW) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            currentStrokePoints = listOf(offset)
                                        },
                                        onDrag = { change, _ ->
                                            currentStrokePoints = currentStrokePoints + change.position
                                        },
                                        onDragEnd = {
                                            if (currentStrokePoints.isNotEmpty()) {
                                                val pathPoints = currentStrokePoints.map { DrawingPoint(it.x, it.y) }
                                                viewModel.addDrawingPath(
                                                    DrawingPath(
                                                        points = pathPoints,
                                                        color = currentBrushColor.toArgb().toLong(),
                                                        strokeWidth = currentBrushWidth,
                                                        opacity = currentBrushOpacity,
                                                        isEraser = isEraserMode
                                                    )
                                                )
                                                currentStrokePoints = emptyList()
                                            }
                                        }
                                    )
                                }
                            }
                    ) {
                        // Draw saved strokes
                        drawingPaths.forEach { drawing ->
                            if (drawing.points.size > 1) {
                                val p = Path()
                                p.moveTo(drawing.points.first().x, drawing.points.first().y)
                                for (i in 1 until drawing.points.size) {
                                    p.lineTo(drawing.points[i].x, drawing.points[i].y)
                                }
                                if (drawing.isEraser) {
                                    drawPath(
                                        path = p,
                                        color = Color.Transparent,
                                        blendMode = androidx.compose.ui.graphics.BlendMode.Clear,
                                        style = Stroke(
                                            width = drawing.strokeWidth,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                } else {
                                    drawPath(
                                        path = p,
                                        color = Color(drawing.color).copy(alpha = drawing.opacity),
                                        style = Stroke(
                                            width = drawing.strokeWidth,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                }
                            }
                        }

                        // Draw live active stroke
                        if (currentStrokePoints.size > 1) {
                            val liveP = Path()
                            liveP.moveTo(currentStrokePoints.first().x, currentStrokePoints.first().y)
                            for (i in 1 until currentStrokePoints.size) {
                                liveP.lineTo(currentStrokePoints[i].x, currentStrokePoints[i].y)
                            }
                            if (isEraserMode) {
                                drawPath(
                                    path = liveP,
                                    color = Color.Transparent,
                                    blendMode = androidx.compose.ui.graphics.BlendMode.Clear,
                                    style = Stroke(
                                        width = currentBrushWidth,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            } else {
                                drawPath(
                                    path = liveP,
                                    color = currentBrushColor.copy(alpha = currentBrushOpacity),
                                    style = Stroke(
                                        width = currentBrushWidth,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }
                    }

                    // Top Left: Badges (AI Enhanced / BG Removed)
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (state.isAiEnhanced) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xDD000000), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = WsAccentGold, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("AI ENHANCED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = WsPureWhite)
                                }
                            }
                        }
                        if (state.isBgRemoved) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xDD000000), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("BG REMOVED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = WsPureWhite)
                            }
                        }
                    }
                }

                // Full-screen Toggle Button (Bottom Right)
                IconButton(
                    onClick = { isFullScreenPreview = !isFullScreenPreview },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .background(Color(0x88000000), CircleShape)
                ) {
                    Icon(
                        if (isFullScreenPreview) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = "Toggle Fullscreen Preview",
                        tint = WsPureWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // ----------------------------------------------------
            // 2. BOTTOM TOOLBAR
            // ----------------------------------------------------
            if (!isFullScreenPreview) {
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
                        PhotoToolItem(
                            icon = Icons.Default.Crop,
                            label = "Crop/Ratio",
                            isSelected = activeTool == PhotoEditorTool.CROP_TRANSFORM,
                            onClick = { viewModel.selectPhotoTool(PhotoEditorTool.CROP_TRANSFORM) }
                        )
                    }
                    item {
                        PhotoToolItem(
                            icon = Icons.Default.Rotate90DegreesCw,
                            label = "Rotate/Flip",
                            isSelected = activeTool == PhotoEditorTool.CROP_TRANSFORM,
                            onClick = { viewModel.selectPhotoTool(PhotoEditorTool.CROP_TRANSFORM) }
                        )
                    }
                    item {
                        PhotoToolItem(
                            icon = Icons.Default.Tune,
                            label = "Adjust",
                            isSelected = activeTool == PhotoEditorTool.ADJUST,
                            onClick = { viewModel.selectPhotoTool(PhotoEditorTool.ADJUST) }
                        )
                    }
                    item {
                        PhotoToolItem(
                            icon = Icons.Default.ColorLens,
                            label = "Filters",
                            isSelected = activeTool == PhotoEditorTool.FILTERS,
                            onClick = { viewModel.selectPhotoTool(PhotoEditorTool.FILTERS) }
                        )
                    }
                    item {
                        PhotoToolItem(
                            icon = Icons.Default.Brush,
                            label = "Brush/Erase",
                            isSelected = activeTool == PhotoEditorTool.DRAW,
                            onClick = { viewModel.selectPhotoTool(PhotoEditorTool.DRAW) }
                        )
                    }
                    item {
                        PhotoToolItem(
                            icon = Icons.Default.TextFields,
                            label = "Text",
                            isSelected = activeTool == PhotoEditorTool.TEXT_OVERLAY,
                            onClick = { viewModel.selectPhotoTool(PhotoEditorTool.TEXT_OVERLAY) }
                        )
                    }
                    item {
                        PhotoToolItem(
                            icon = Icons.Default.Wallpaper,
                            label = "Frames",
                            isSelected = activeTool == PhotoEditorTool.FRAMES,
                            onClick = { viewModel.selectPhotoTool(PhotoEditorTool.FRAMES) }
                        )
                    }
                    item {
                        PhotoToolItem(
                            icon = Icons.Default.AutoAwesome,
                            label = "AI Studio",
                            isSelected = activeTool == PhotoEditorTool.AI_TOOLS,
                            onClick = { viewModel.selectPhotoTool(PhotoEditorTool.AI_TOOLS) }
                        )
                    }
                }
            }
        }
    }

    // ----------------------------------------------------
    // 3. TOOL BOTTOM SHEETS
    // ----------------------------------------------------
    if (activeTool != PhotoEditorTool.NONE) {
        PhotoToolBottomSheet(
            activeTool = activeTool,
            state = state,
            viewModel = viewModel,
            onDismiss = { viewModel.selectPhotoTool(PhotoEditorTool.NONE) },
            brushColor = currentBrushColor,
            onColorChange = { currentBrushColor = it },
            brushWidth = currentBrushWidth,
            onWidthChange = { currentBrushWidth = it },
            brushOpacity = currentBrushOpacity,
            onOpacityChange = { currentBrushOpacity = it },
            isEraserMode = isEraserMode,
            onToggleEraser = { isEraserMode = !isEraserMode },
            onClearDrawings = { viewModel.clearDrawingPaths() },
            onAddText = { text ->
                viewModel.addPhotoTextOverlay(PhotoTextOverlay(text = text))
            }
        )
    }

    // Text Overlay Edit Dialog
    if (showTextDialog && selectedTextOverlay != null) {
        val overlay = selectedTextOverlay!!
        var textInput by remember { mutableStateOf(overlay.text) }
        var fontSizeVal by remember { mutableFloatStateOf(overlay.fontSizeSp) }
        var isBoldVal by remember { mutableStateOf(overlay.isBold) }
        var isItalicVal by remember { mutableStateOf(overlay.isItalic) }
        var alignVal by remember { mutableStateOf(overlay.alignment) }
        var hasBgVal by remember { mutableStateOf(overlay.hasBackgroundBox) }
        var fontVal by remember { mutableStateOf(overlay.fontFamily) }
        var colorVal by remember { mutableStateOf(overlay.color) }

        AlertDialog(
            containerColor = WsSurfaceCardElevated,
            titleContentColor = WsTextPrimary,
            textContentColor = WsTextPrimary,
            onDismissRequest = {
                showTextDialog = false
                selectedTextOverlay = null
            },
            title = { Text("Customize Text Overlay", fontWeight = FontWeight.Bold, color = WsTextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        label = { Text("Text") },
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

                    // Font Family Chooser
                    Text("FONT STYLE", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = WsTextSecondary)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val fontOptions = listOf("Default", "Sans", "Serif", "Monospace", "Cursive")
                        items(fontOptions) { fName ->
                            val isSel = fontVal == fName
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { fontVal = fName },
                                color = if (isSel) WsElectricBlue.copy(alpha = 0.22f) else WsButtonDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) WsElectricCyan else WsBorder)
                            ) {
                                Text(
                                    text = fName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) WsElectricCyan else WsTextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Text Color Palette
                    Text("TEXT COLOR", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = WsTextSecondary)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val colorOptions = listOf(
                            0xFFFFFFFF, 0xFF000000, 0xFFFFD700, 0xFFEF4444,
                            0xFF10B981, 0xFF06B6D4, 0xFF8B5CF6, 0xFFEC4899, 0xFFF97316
                        )
                        items(colorOptions) { colLong ->
                            val isSel = colorVal == colLong
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color(colLong), CircleShape)
                                    .border(2.dp, if (isSel) WsElectricCyan else WsBorder, CircleShape)
                                    .clickable { colorVal = colLong }
                            )
                        }
                    }

                    Text("Font Size: ${fontSizeVal.toInt()}sp", fontSize = 12.sp, color = WsTextSecondary)
                    Slider(
                        value = fontSizeVal,
                        onValueChange = { fontSizeVal = it },
                        valueRange = 12f..56f,
                        colors = SliderDefaults.colors(
                            thumbColor = WsElectricCyan,
                            activeTrackColor = WsElectricBlue,
                            inactiveTrackColor = WsSurfaceCard
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = { isBoldVal = !isBoldVal }) {
                                Icon(Icons.Default.FormatBold, contentDescription = "Bold", tint = if (isBoldVal) WsElectricCyan else WsTextMuted)
                            }
                            IconButton(onClick = { isItalicVal = !isItalicVal }) {
                                Icon(Icons.Default.FormatItalic, contentDescription = "Italic", tint = if (isItalicVal) WsElectricCyan else WsTextMuted)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = { alignVal = "Left" }) {
                                Icon(Icons.Default.FormatAlignLeft, contentDescription = "Left", tint = if (alignVal == "Left") WsElectricCyan else WsTextMuted)
                            }
                            IconButton(onClick = { alignVal = "Center" }) {
                                Icon(Icons.Default.FormatAlignCenter, contentDescription = "Center", tint = if (alignVal == "Center") WsElectricCyan else WsTextMuted)
                            }
                            IconButton(onClick = { alignVal = "Right" }) {
                                Icon(Icons.Default.FormatAlignRight, contentDescription = "Right", tint = if (alignVal == "Right") WsElectricCyan else WsTextMuted)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Background Pill Box", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = WsTextPrimary)
                        Switch(
                            checked = hasBgVal,
                            onCheckedChange = { hasBgVal = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = WsPureWhite, checkedTrackColor = WsElectricBlue)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updatePhotoTextOverlay(
                            overlay.copy(
                                text = textInput,
                                fontSizeSp = fontSizeVal,
                                isBold = isBoldVal,
                                isItalic = isItalicVal,
                                alignment = alignVal,
                                hasBackgroundBox = hasBgVal,
                                fontFamily = fontVal,
                                color = colorVal
                            )
                        )
                        showTextDialog = false
                        selectedTextOverlay = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WsElectricBlue)
                ) {
                    Text("Save", color = WsTextPrimary)
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        viewModel.deletePhotoTextOverlay(overlay.id)
                        showTextDialog = false
                        selectedTextOverlay = null
                    }) {
                        Text("Delete", color = WsAccentRed)
                    }
                    TextButton(onClick = {
                        showTextDialog = false
                        selectedTextOverlay = null
                    }) {
                        Text("Cancel", color = WsTextSecondary)
                    }
                }
            }
        )
    }

    // Export Dialog (JPG / PNG, high-res 1080p / 4K, share sheet)
    if (showExportDialog) {
        ExportDialog(
            isVideo = false,
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
private fun BackgroundReplacementBackdrop(replacementStyle: String) {
    when (replacementStyle) {
        "Studio White" -> {
            Box(modifier = Modifier.fillMaxSize().background(Color.White))
        }
        "Minimal Charcoal" -> {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF18191B)))
        }
        "Cyber Gradient" -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Color(0xFF0F2027), Color(0xFF2C5364))
                        )
                    )
            )
        }
        "Sunset Warm" -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color(0xFFFF512F), Color(0xFFDD2476))
                        )
                    )
            )
        }
        "Emerald Forest" -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color(0xFF0B3B24), Color(0xFF10B981))
                        )
                    )
            )
        }
        "Neon Glow" -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Color(0xFF4A00E0), Color(0xFF8E2DE2))
                        )
                    )
            )
        }
        "Pastel Dream" -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Color(0xFFA1C4FD), Color(0xFFC2E9FB))
                        )
                    )
            )
        }
        "Scenic Blue" -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color(0xFF1E3C72), Color(0xFF2A5298))
                        )
                    )
            )
        }
        "Golden Hour" -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color(0xFFF7971E), Color(0xFFFFD200))
                        )
                    )
            )
        }
        else -> {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF1F3F5)))
        }
    }
}

@Composable
private fun FrameOverlayView(frameStyle: String) {
    when (frameStyle) {
        "Polaroid" -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .border(16.dp, WsPureWhite)
                    .padding(bottom = 44.dp)
            ) {}
        }
        "Minimal White" -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(14.dp, WsPureWhite)
            )
        }
        "Cinema Black" -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(26.dp).background(Color.Black))
                Box(modifier = Modifier.fillMaxWidth().height(26.dp).background(Color.Black))
            }
        }
        "Cyberpunk" -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(4.dp, Color(0xFF00F0FF))
            )
        }
        "Film Border" -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(10.dp, Color(0xFF1E2022))
            )
        }
        "Classic Gold" -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(8.dp, WsAccentGold)
            )
        }
        "Neon Violet" -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(6.dp, Color(0xFFD946EF))
            )
        }
    }
}

@Composable
private fun PhotoToolItem(
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
private fun PhotoToolBottomSheet(
    activeTool: PhotoEditorTool,
    state: com.example.model.PhotoEditState,
    viewModel: WsEditorViewModel,
    onDismiss: () -> Unit,
    brushColor: Color,
    onColorChange: (Color) -> Unit,
    brushWidth: Float,
    onWidthChange: (Float) -> Unit,
    brushOpacity: Float,
    onOpacityChange: (Float) -> Unit,
    isEraserMode: Boolean,
    onToggleEraser: () -> Unit,
    onClearDrawings: () -> Unit,
    onAddText: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    var newTextInput by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = WsSurfaceCardElevated,
        scrimColor = Color.Black.copy(alpha = 0.65f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (activeTool) {
                        PhotoEditorTool.CROP_TRANSFORM -> "Crop & Transform"
                        PhotoEditorTool.FILTERS -> "Cinematic Filters"
                        PhotoEditorTool.ADJUST -> "Color Adjustments"
                        PhotoEditorTool.DRAW -> "Drawing, Brush & Eraser"
                        PhotoEditorTool.TEXT_OVERLAY -> "Text Overlays"
                        PhotoEditorTool.FRAMES -> "Artistic Frames"
                        PhotoEditorTool.AI_TOOLS -> "AI Magic Studio"
                        else -> "Tool"
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
                PhotoEditorTool.CROP_TRANSFORM -> {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("ASPECT RATIOS", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = WsTextSecondary)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(AspectRatioOption.entries) { ratio ->
                                val isSelected = state.aspectRatio == ratio
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            viewModel.updatePhotoState { it.copy(aspectRatio = ratio) }
                                        },
                                    color = if (isSelected) WsElectricBlue else WsButtonDark,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) WsElectricCyan else WsBorder),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = ratio.label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) WsPureWhite else WsTextSecondary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Text("ROTATE & FLIP", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = WsTextSecondary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.updatePhotoState {
                                        it.copy(rotationDegrees = (it.rotationDegrees + 90) % 360)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WsButtonDark)
                            ) {
                                Icon(Icons.Default.Rotate90DegreesCw, contentDescription = null, tint = WsTextPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Rotate 90°", fontSize = 12.sp, color = WsTextPrimary)
                            }
                            Button(
                                onClick = {
                                    viewModel.updatePhotoState { it.copy(isFlippedH = !it.isFlippedH) }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WsButtonDark)
                            ) {
                                Icon(Icons.Default.Flip, contentDescription = null, tint = WsTextPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Flip Horiz", fontSize = 12.sp, color = WsTextPrimary)
                            }
                            Button(
                                onClick = {
                                    viewModel.updatePhotoState { it.copy(isFlippedV = !it.isFlippedV) }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WsButtonDark)
                            ) {
                                Icon(Icons.Default.Flip, contentDescription = null, tint = WsTextPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Flip Vert", fontSize = 12.sp, color = WsTextPrimary)
                            }
                        }
                    }
                }

                PhotoEditorTool.FILTERS -> {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        itemsIndexed(FilterPreset.entries) { _, preset ->
                            val isSelected = state.filter == preset
                            Surface(
                                modifier = Modifier
                                    .size(width = 86.dp, height = 74.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.updatePhotoState { it.copy(filter = preset) }
                                    },
                                color = if (isSelected) WsElectricBlue else WsButtonDark,
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
                                        tint = if (isSelected) WsPureWhite else WsElectricCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = preset.displayName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) WsPureWhite else WsTextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                PhotoEditorTool.ADJUST -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        AdjustRow(
                            label = "Brightness",
                            value = state.brightness,
                            range = -0.5f..0.5f,
                            onValueChange = { viewModel.updatePhotoState { s -> s.copy(brightness = it) } }
                        )
                        AdjustRow(
                            label = "Contrast",
                            value = state.contrast,
                            range = 0.5f..2.0f,
                            onValueChange = { viewModel.updatePhotoState { s -> s.copy(contrast = it) } }
                        )
                        AdjustRow(
                            label = "Saturation",
                            value = state.saturation,
                            range = 0f..2.5f,
                            onValueChange = { viewModel.updatePhotoState { s -> s.copy(saturation = it) } }
                        )
                        AdjustRow(
                            label = "Exposure",
                            value = state.exposure,
                            range = -0.5f..0.5f,
                            onValueChange = { viewModel.updatePhotoState { s -> s.copy(exposure = it) } }
                        )
                        AdjustRow(
                            label = "Sharpness",
                            value = state.sharpness,
                            range = 0f..1.0f,
                            onValueChange = { viewModel.updatePhotoState { s -> s.copy(sharpness = it) } }
                        )
                        AdjustRow(
                            label = "Warmth / Temperature",
                            value = state.warmth,
                            range = -0.5f..0.5f,
                            onValueChange = { viewModel.updatePhotoState { s -> s.copy(warmth = it) } }
                        )
                    }
                }

                PhotoEditorTool.DRAW -> {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "BRUSH COLOR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WsTextSecondary)
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onToggleEraser() },
                                color = if (isEraserMode) WsAccentRed else WsButtonDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isEraserMode) WsAccentRed else WsBorder)
                            ) {
                                Text(
                                    text = if (isEraserMode) "Eraser ON" else "Switch to Eraser",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEraserMode) WsPureWhite else WsTextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            val colors = listOf(
                                Color.White, Color.Black, Color(0xFFEF4444),
                                Color(0xFFF97316), Color(0xFFF59E0B), Color(0xFF10B981),
                                Color(0xFF06B6D4), Color(0xFF3B82F6), Color(0xFF8B5CF6)
                            )
                            items(colors) { col ->
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(col, CircleShape)
                                        .border(2.dp, if (brushColor == col && !isEraserMode) WsElectricCyan else WsBorder, CircleShape)
                                        .clickable { onColorChange(col) }
                                )
                            }
                        }

                        Text(text = "BRUSH SIZE: ${brushWidth.toInt()}px", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WsTextSecondary)
                        Slider(
                            value = brushWidth,
                            onValueChange = onWidthChange,
                            valueRange = 2f..48f,
                            colors = SliderDefaults.colors(
                                thumbColor = WsElectricBlue,
                                activeTrackColor = WsElectricBlue,
                                inactiveTrackColor = WsBorder
                            )
                        )

                        Text(text = "BRUSH OPACITY: ${(brushOpacity * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WsTextSecondary)
                        Slider(
                            value = brushOpacity,
                            onValueChange = onOpacityChange,
                            valueRange = 0.1f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = WsElectricBlue,
                                activeTrackColor = WsElectricBlue,
                                inactiveTrackColor = WsBorder
                            )
                        )

                        OutlinedButton(
                            onClick = onClearDrawings,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WsTextSecondary)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = WsAccentRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Clear All Drawings", color = WsTextPrimary)
                        }
                    }
                }

                PhotoEditorTool.TEXT_OVERLAY -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newTextInput,
                                onValueChange = { newTextInput = it },
                                placeholder = { Text("Enter text overlay...", color = WsTextMuted) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = WsTextPrimary,
                                    unfocusedTextColor = WsTextPrimary,
                                    focusedContainerColor = WsBackground,
                                    unfocusedContainerColor = WsBackground,
                                    focusedBorderColor = WsElectricCyan,
                                    unfocusedBorderColor = WsBorder
                                )
                            )
                            Button(
                                onClick = {
                                    if (newTextInput.isNotBlank()) {
                                        onAddText(newTextInput.trim())
                                        newTextInput = ""
                                        onDismiss()
                                        Toast.makeText(context, "Text added. Tap text on photo to customize.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WsElectricBlue)
                            ) {
                                Text("Add", color = WsPureWhite)
                            }
                        }

                        if (state.textOverlays.isNotEmpty()) {
                            Text("ACTIVE TEXT LAYERS (TAP TO EDIT)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WsTextSecondary)
                            state.textOverlays.forEach { overlay ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp)),
                                    color = WsSurfaceCard,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = overlay.text, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = WsTextPrimary)
                                        IconButton(onClick = { viewModel.deletePhotoTextOverlay(overlay.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = WsAccentRed, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                PhotoEditorTool.FRAMES -> {
                    val frameOptions = listOf("None", "Polaroid", "Minimal White", "Cinema Black", "Cyberpunk", "Film Border", "Classic Gold", "Neon Violet")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(frameOptions) { frame ->
                            val isSelected = state.frameStyle == frame
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.updatePhotoState { it.copy(frameStyle = frame) }
                                    },
                                color = if (isSelected) WsElectricBlue else WsButtonDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) WsElectricCyan else WsBorder),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = frame,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) WsPureWhite else WsTextSecondary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }

                PhotoEditorTool.AI_TOOLS -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Background Remover
                        Button(
                            onClick = {
                                viewModel.updatePhotoState { it.copy(isBgRemoved = !it.isBgRemoved) }
                                onDismiss()
                                Toast.makeText(context, if (!state.isBgRemoved) "Background Cutout Active" else "Background Restored", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WsButtonDark),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WsBorderGlow)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = WsElectricCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (state.isBgRemoved) "Restore Original Background" else "AI Background Remover Cutout",
                                color = WsTextPrimary
                            )
                        }

                        // AI Background Replacement Presets
                        Text("AI BACKGROUND REPLACEMENT", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = WsTextSecondary)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val bgStyles = listOf(
                                "None", "Studio White", "Minimal Charcoal", "Cyber Gradient",
                                "Sunset Warm", "Emerald Forest", "Neon Glow", "Pastel Dream", "Scenic Blue", "Golden Hour"
                            )
                            items(bgStyles) { bg ->
                                val isSelected = (state.backgroundReplacement ?: "None") == bg
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            viewModel.updatePhotoState {
                                                it.copy(backgroundReplacement = if (bg == "None") null else bg)
                                            }
                                        },
                                    color = if (isSelected) WsElectricBlue else WsButtonDark,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) WsElectricCyan else WsBorder)
                                ) {
                                    Text(
                                        text = bg,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) WsPureWhite else WsTextSecondary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        // AI Auto Clarity
                        OutlinedButton(
                            onClick = {
                                viewModel.updatePhotoState { it.copy(isAiEnhanced = true) }
                                onDismiss()
                                Toast.makeText(context, "AI Detail & Clarity Boost Applied", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WsBorderGlow),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WsElectricCyan)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = WsElectricCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AI Auto Clarity & Detail Boost", color = WsTextPrimary)
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun AdjustRow(
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
            Text(text = String.format(Locale.US, "%.2f", value), fontSize = 12.sp, color = WsElectricCyan)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = WsElectricBlue,
                activeTrackColor = WsElectricBlue,
                inactiveTrackColor = WsBorder
            )
        )
    }
}
