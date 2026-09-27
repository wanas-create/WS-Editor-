package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CustomFontEntity
import com.example.data.FontRegistry
import com.example.data.TextOverlayItem
import com.example.ui.VideoEditorUiState
import com.example.ui.theme.RobotoMonoFamily
import com.example.ui.theme.TrackCyan
import com.example.ui.theme.TrackGreen
import com.example.ui.theme.TrackOrange
import com.example.ui.theme.TrackPurple
import com.example.ui.theme.WsBorder
import com.example.ui.theme.WsCyan
import com.example.ui.theme.WsDanger
import com.example.ui.theme.WsPrimaryGradient
import com.example.ui.theme.WsPurple
import com.example.ui.theme.WsSurface
import com.example.ui.theme.WsSurfaceElevated
import com.example.ui.theme.WsSurfaceHigh
import com.example.ui.theme.WsTextPrimary
import com.example.ui.theme.WsTextSecondary

@Composable
fun TextAndCustomFontPanel(
    selectedTextOverlay: TextOverlayItem?,
    customFonts: List<CustomFontEntity>,
    onAddTextOverlay: () -> Unit,
    onUpdateText: (String) -> Unit,
    onUpdateColor: (Long) -> Unit,
    onUpdateFontSize: (Float) -> Unit,
    onToggleGlow: (Boolean) -> Unit,
    onToggleBgBox: (Boolean) -> Unit,
    onSelectBuiltInFont: (String, String) -> Unit,
    onSelectCustomFont: (CustomFontEntity) -> Unit,
    onUploadCustomFontFromDevice: () -> Unit,
    onImportSampleCustomFont: (String, String) -> Unit,
    onDeleteCustomFont: (CustomFontEntity) -> Unit
) {
    val activeOverlay = selectedTextOverlay
    val colorOptions = listOf(
        0xFF00D9FFL,
        0xFFFFFFFFL,
        0xFF7B00FFL,
        0xFF00E676L,
        0xFFFF8A00L,
        0xFFFF2E93L,
        0xFFFFE600L
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WsSurface)
            .border(1.dp, WsBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
            .testTag("text_overlay_custom_font_panel"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header + Add Text + Upload Custom Font (.TTF/.OTF) CTA
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Title,
                    contentDescription = null,
                    tint = WsCyan,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Text Overlay & Custom Fonts",
                    style = MaterialTheme.typography.titleMedium,
                    color = WsTextPrimary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(WsSurfaceElevated)
                        .border(1.dp, WsBorder, RoundedCornerShape(8.dp))
                        .clickable { onAddTextOverlay() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("btn_add_text_layer")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Text Layer",
                            tint = WsCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Add Text",
                            style = MaterialTheme.typography.labelSmall,
                            color = WsTextPrimary
                        )
                    }
                }

                Button(
                    onClick = onUploadCustomFontFromDevice,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WsCyan,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("btn_upload_custom_font")
                ) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = "Upload Custom Font from Device",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Upload Font (.TTF/.OTF)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (activeOverlay != null) {
            // Text input & quick style toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = activeOverlay.text,
                    onValueChange = onUpdateText,
                    singleLine = true,
                    label = { Text("Overlay Text (${activeOverlay.fontName})", fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WsCyan,
                        unfocusedBorderColor = WsBorder,
                        focusedTextColor = WsTextPrimary,
                        unfocusedTextColor = WsTextPrimary,
                        focusedLabelColor = WsCyan,
                        unfocusedLabelColor = WsTextSecondary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("text_overlay_input_field")
                )

                // Glow toggle
                StyleChipButton(
                    label = "Glow",
                    active = activeOverlay.hasGlow,
                    onClick = { onToggleGlow(!activeOverlay.hasGlow) }
                )

                // Box toggle
                StyleChipButton(
                    label = "Box",
                    active = activeOverlay.hasBgBox,
                    onClick = { onToggleBgBox(!activeOverlay.hasBgBox) }
                )
            }

            // Color Swatches + Font Size Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    colorOptions.forEach { hex ->
                        val isSelected = activeOverlay.colorHex == hex
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(hex))
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) Color.White else WsBorder,
                                    shape = CircleShape
                                )
                                .clickable { onUpdateColor(hex) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected Color",
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "${activeOverlay.fontSizeSp.toInt()}sp",
                    fontFamily = RobotoMonoFamily,
                    fontSize = 11.sp,
                    color = WsCyan
                )

                Slider(
                    value = activeOverlay.fontSizeSp,
                    onValueChange = onUpdateFontSize,
                    valueRange = 16f..46f,
                    colors = SliderDefaults.colors(
                        thumbColor = WsCyan,
                        activeTrackColor = WsCyan,
                        inactiveTrackColor = WsBorder
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Custom Fonts Section (Uploaded from device + Quick Sample .TTF Pack Import)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "UPLOADED CUSTOM FONTS (${customFonts.size})",
                    fontFamily = RobotoMonoFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = WsCyan
                )

                // Quick sample .TTF import buttons so users can test custom font file loading immediately
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SampleFontQuickImportChip(
                        label = "+CyberDisplay.ttf",
                        onClick = { onImportSampleCustomFont("CyberDisplay-Bold.ttf", "CyberDisplay Bold") }
                    )
                    SampleFontQuickImportChip(
                        label = "+NeonScript.ttf",
                        onClick = { onImportSampleCustomFont("NeonScript-Regular.ttf", "NeonScript Regular") }
                    )
                }
            }

            if (customFonts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(WsSurfaceElevated)
                        .border(1.dp, WsBorder, RoundedCornerShape(10.dp))
                        .clickable { onUploadCustomFontFromDevice() }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("empty_custom_font_dropzone")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FontDownload,
                            contentDescription = null,
                            tint = WsCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "No custom fonts uploaded yet — Tap to select .TTF or .OTF from device",
                                style = MaterialTheme.typography.bodyMedium,
                                color = WsTextPrimary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Or tap '+CyberDisplay.ttf' above to test custom font file import instantly",
                                style = MaterialTheme.typography.labelSmall,
                                color = WsTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(customFonts, key = { it.id }) { customFont ->
                        val isSelected = activeOverlay?.fontId == customFont.fontId ||
                            activeOverlay?.customFontPath == customFont.filePath
                        val resolvedFamily = FontRegistry.resolveFontFamily(customFont.fontId, customFont.filePath)

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) WsCyan.copy(alpha = 0.18f) else WsSurfaceElevated)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) WsCyan else WsPurple.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onSelectCustomFont(customFont) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("custom_font_item_${customFont.displayName}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Text(
                                        text = customFont.displayName,
                                        fontFamily = resolvedFamily,
                                        fontSize = 14.sp,
                                        color = if (isSelected) WsCyan else WsTextPrimary,
                                        maxLines = 1
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(WsPurple.copy(alpha = 0.35f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = if (customFont.isDeviceUpload) "DEVICE .TTF" else "CUSTOM .TTF",
                                            fontFamily = RobotoMonoFamily,
                                            fontSize = 8.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                                Text(
                                    text = "${customFont.fileName} • ${customFont.fileSizeKb} KB",
                                    fontFamily = RobotoMonoFamily,
                                    fontSize = 9.sp,
                                    color = WsTextSecondary
                                )
                            }

                            IconButton(
                                onClick = { onDeleteCustomFont(customFont) },
                                modifier = Modifier.size(22.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete Custom Font",
                                    tint = WsDanger,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Built-In Studio Fonts Row
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "STUDIO BUILT-IN FONTS",
                fontFamily = RobotoMonoFamily,
                fontSize = 10.sp,
                color = WsTextSecondary
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(FontRegistry.builtInFonts, key = { it.id }) { fontOption ->
                    val isSelected = activeOverlay?.fontId == fontOption.id &&
                        activeOverlay.customFontPath.isNullOrBlank()
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) WsCyan.copy(alpha = 0.16f) else WsSurfaceElevated)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) WsCyan else WsBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectBuiltInFont(fontOption.id, fontOption.displayName) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("builtin_font_${fontOption.id}"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = fontOption.displayName,
                            fontFamily = fontOption.fontFamily,
                            fontSize = 14.sp,
                            color = if (isSelected) WsCyan else WsTextPrimary
                        )
                        Text(
                            text = fontOption.styleTag,
                            fontFamily = RobotoMonoFamily,
                            fontSize = 9.sp,
                            color = WsTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StyleChipButton(
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) WsCyan.copy(alpha = 0.2f) else WsSurfaceElevated)
            .border(1.dp, if (active) WsCyan else WsBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) WsCyan else WsTextSecondary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SampleFontQuickImportChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(WsPurple.copy(alpha = 0.22f))
            .border(1.dp, WsPurple, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .testTag("sample_font_chip_$label")
    ) {
        Text(
            text = label,
            fontFamily = RobotoMonoFamily,
            fontSize = 9.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun VnMultiLayerTimeline(
    state: VideoEditorUiState,
    onSeek: (Float) -> Unit,
    onSelectClip: (Int, String) -> Unit,
    onSplitAtPlayhead: () -> Unit,
    onTrimSelected: (Float, Float) -> Unit,
    onDeleteSelected: () -> Unit
) {
    val scrollState = rememberScrollState()
    val timelineWidthDp: Dp = 520.dp
    val labelColumnWidth: Dp = 64.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .background(Color(0xFF0E0E13))
            .border(1.dp, WsBorder, RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .padding(vertical = 8.dp, horizontal = 10.dp)
            .testTag("vn_multilayer_timeline"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Quick Trim & Split Action Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "VN 4-TRACK TIMELINE",
                    fontFamily = RobotoMonoFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = WsCyan
                )
                Text(
                    text = "• ${String.format("%02d:%04.1f", (state.playheadSec / 60).toInt(), state.playheadSec % 60)} / ${String.format("%.1fs", state.totalDurationSec)}",
                    fontFamily = RobotoMonoFamily,
                    fontSize = 10.sp,
                    color = WsTextSecondary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TimelineMicroButton(
                    label = "Trim In -0.5s",
                    onClick = { onTrimSelected(-0.5f, 0f) }
                )
                TimelineMicroButton(
                    label = "Trim Out -0.5s",
                    onClick = { onTrimSelected(0f, -0.5f) }
                )
                TimelineMicroButton(
                    label = "✂ Split",
                    accent = WsCyan,
                    onClick = onSplitAtPlayhead
                )
                TimelineMicroButton(
                    label = "🗑 Cut",
                    accent = WsDanger,
                    onClick = onDeleteSelected
                )
            }
        }

        // Scrollable 4-Track Multi-Layer Area with Playhead
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left Fixed Track Headers with Required Colored Left Borders (Cyan, Purple, Orange, Green)
            Column(
                modifier = Modifier.width(labelColumnWidth),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Spacer(modifier = Modifier.height(18.dp)) // Ruler offset
                TrackTypeBadge(label = "VIDEO", borderColor = TrackCyan)
                TrackTypeBadge(label = "TEXT", borderColor = TrackPurple)
                TrackTypeBadge(label = "FX/STK", borderColor = TrackOrange)
                TrackTypeBadge(label = "MUSIC", borderColor = TrackGreen)
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Horizontal Scrollable Tracks + Playhead Line
            Box(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(scrollState)
            ) {
                Column(
                    modifier = Modifier
                        .width(timelineWidthDp)
                        .pointerInput(state.totalDurationSec) {
                            detectHorizontalDragGestures { change, _ ->
                                change.consume()
                                val ratio = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                                onSeek(ratio * state.totalDurationSec)
                            }
                        },
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    // Time Ruler
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (sec in 0..state.totalDurationSec.toInt() step 2) {
                            Text(
                                text = String.format("00:%02d", sec),
                                fontFamily = RobotoMonoFamily,
                                fontSize = 9.sp,
                                color = WsTextSecondary
                            )
                        }
                    }

                    // TRACK 1: VIDEO TRACK (Cyan #00D9FF left border)
                    TimelineTrackContainer(trackColor = TrackCyan) {
                        state.videoClips.forEach { clip ->
                            val startFrac = (clip.startSec / state.totalDurationSec).coerceIn(0f, 1f)
                            val widthFrac = (clip.durationSec / state.totalDurationSec).coerceIn(0.06f, 1f)
                            val isSelected = state.selectedTrackIndex == 0 && state.selectedClipId == clip.id
                            TimelineClipBlock(
                                startFraction = startFrac,
                                widthFraction = widthFrac,
                                totalWidthDp = timelineWidthDp,
                                leftBorderColor = TrackCyan,
                                backgroundColor = Color(0xFF0F2933),
                                isSelected = isSelected,
                                title = clip.name,
                                subtitle = "${String.format("%.1fs", clip.durationSec)} • ${clip.speed}x",
                                onClick = { onSelectClip(0, clip.id) },
                                onDragStartHandle = { deltaRatio ->
                                    onSelectClip(0, clip.id)
                                    onTrimSelected(deltaRatio * state.totalDurationSec, 0f)
                                },
                                onDragEndHandle = { deltaRatio ->
                                    onSelectClip(0, clip.id)
                                    onTrimSelected(0f, deltaRatio * state.totalDurationSec)
                                }
                            )
                        }
                    }

                    // TRACK 2: TEXT OVERLAY TRACK (Purple #7B00FF left border)
                    TimelineTrackContainer(trackColor = TrackPurple) {
                        state.textOverlays.forEach { txt ->
                            val startFrac = (txt.startSec / state.totalDurationSec).coerceIn(0f, 1f)
                            val widthFrac = (txt.durationSec / state.totalDurationSec).coerceIn(0.06f, 1f)
                            val isSelected = state.selectedTrackIndex == 1 && state.selectedClipId == txt.id
                            val fontBadge = if (!txt.customFontPath.isNullOrBlank()) "★ ${txt.fontName}" else txt.fontName
                            TimelineClipBlock(
                                startFraction = startFrac,
                                widthFraction = widthFrac,
                                totalWidthDp = timelineWidthDp,
                                leftBorderColor = TrackPurple,
                                backgroundColor = Color(0xFF231138),
                                isSelected = isSelected,
                                title = "T: ${txt.text}",
                                subtitle = "${String.format("%.1fs", txt.durationSec)} • $fontBadge",
                                onClick = { onSelectClip(1, txt.id) },
                                onDragStartHandle = { deltaRatio ->
                                    onSelectClip(1, txt.id)
                                    onTrimSelected(deltaRatio * state.totalDurationSec, 0f)
                                },
                                onDragEndHandle = { deltaRatio ->
                                    onSelectClip(1, txt.id)
                                    onTrimSelected(0f, deltaRatio * state.totalDurationSec)
                                }
                            )
                        }
                    }

                    // TRACK 3: STICKER & FX TRACK (Orange #FF8A00 left border)
                    TimelineTrackContainer(trackColor = TrackOrange) {
                        state.stickerFxClips.forEach { fx ->
                            val startFrac = (fx.startSec / state.totalDurationSec).coerceIn(0f, 1f)
                            val widthFrac = (fx.durationSec / state.totalDurationSec).coerceIn(0.06f, 1f)
                            val isSelected = state.selectedTrackIndex == 2 && state.selectedClipId == fx.id
                            TimelineClipBlock(
                                startFraction = startFrac,
                                widthFraction = widthFrac,
                                totalWidthDp = timelineWidthDp,
                                leftBorderColor = TrackOrange,
                                backgroundColor = Color(0xFF2E1D0C),
                                isSelected = isSelected,
                                title = fx.label,
                                subtitle = "${String.format("%.1fs", fx.durationSec)} • ${fx.badge}",
                                onClick = { onSelectClip(2, fx.id) },
                                onDragStartHandle = { deltaRatio ->
                                    onSelectClip(2, fx.id)
                                    onTrimSelected(deltaRatio * state.totalDurationSec, 0f)
                                },
                                onDragEndHandle = { deltaRatio ->
                                    onSelectClip(2, fx.id)
                                    onTrimSelected(0f, deltaRatio * state.totalDurationSec)
                                }
                            )
                        }
                    }

                    // TRACK 4: AUDIO / MUSIC TRACK (Green #00E676 left border)
                    TimelineTrackContainer(trackColor = TrackGreen) {
                        state.audioClips.forEach { aud ->
                            val startFrac = (aud.startSec / state.totalDurationSec).coerceIn(0f, 1f)
                            val widthFrac = (aud.durationSec / state.totalDurationSec).coerceIn(0.06f, 1f)
                            val isSelected = state.selectedTrackIndex == 3 && state.selectedClipId == aud.id
                            TimelineClipBlock(
                                startFraction = startFrac,
                                widthFraction = widthFrac,
                                totalWidthDp = timelineWidthDp,
                                leftBorderColor = TrackGreen,
                                backgroundColor = Color(0xFF0D291C),
                                isSelected = isSelected,
                                title = "♫ ${aud.trackName}",
                                subtitle = "${String.format("%.1fs", aud.durationSec)} • ${(aud.volume * 100).toInt()}% Vol",
                                onClick = { onSelectClip(3, aud.id) },
                                onDragStartHandle = { deltaRatio ->
                                    onSelectClip(3, aud.id)
                                    onTrimSelected(deltaRatio * state.totalDurationSec, 0f)
                                },
                                onDragEndHandle = { deltaRatio ->
                                    onSelectClip(3, aud.id)
                                    onTrimSelected(0f, deltaRatio * state.totalDurationSec)
                                }
                            )
                        }
                    }
                }

                // Vertical Cyan Playhead Indicator Line
                val playheadFraction = (state.playheadSec / state.totalDurationSec).coerceIn(0f, 1f)
                val playheadOffsetDp = timelineWidthDp * playheadFraction
                Box(
                    modifier = Modifier
                        .offset(x = playheadOffsetDp)
                        .width(2.5.dp)
                        .height(178.dp)
                        .background(WsCyan)
                )
                Box(
                    modifier = Modifier
                        .offset(x = playheadOffsetDp - 5.dp, y = 0.dp)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(WsCyan)
                )
            }
        }
    }
}

@Composable
private fun TrackTypeBadge(
    label: String,
    borderColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(WsSurfaceElevated)
            .border(1.dp, WsBorder, RoundedCornerShape(6.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(borderColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontFamily = RobotoMonoFamily,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = borderColor
        )
    }
}

@Composable
private fun TimelineTrackContainer(
    trackColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF15151D))
            .border(1.dp, trackColor.copy(alpha = 0.22f), RoundedCornerShape(6.dp))
    ) {
        content()
    }
}

@Composable
private fun TimelineClipBlock(
    startFraction: Float,
    widthFraction: Float,
    totalWidthDp: Dp,
    leftBorderColor: Color,
    backgroundColor: Color,
    isSelected: Boolean,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    onDragStartHandle: (Float) -> Unit,
    onDragEndHandle: (Float) -> Unit
) {
    val startOffset = totalWidthDp * startFraction
    val clipWidth = (totalWidthDp * widthFraction).coerceAtLeast(48.dp)

    Row(
        modifier = Modifier
            .offset(x = startOffset)
            .width(clipWidth)
            .height(34.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) Color.White else leftBorderColor.copy(alpha = 0.7f),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Distinct 5.dp Colored Left Border + Drag Handle for Start Trim
        Box(
            modifier = Modifier
                .width(7.dp)
                .fillMaxHeight()
                .background(leftBorderColor)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        onDragStartHandle(dragAmount / 800f)
                    }
                }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = WsTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                fontFamily = RobotoMonoFamily,
                fontSize = 8.sp,
                color = leftBorderColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Right Drag Handle for End Trim
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(Color.White.copy(alpha = 0.85f))
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            change.consume()
                            onDragEndHandle(dragAmount / 800f)
                        }
                    }
            )
        }
    }
}

@Composable
private fun TimelineMicroButton(
    label: String,
    accent: Color = WsTextPrimary,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(WsSurfaceHigh)
            .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 7.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontFamily = RobotoMonoFamily,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = accent
        )
    }
}
