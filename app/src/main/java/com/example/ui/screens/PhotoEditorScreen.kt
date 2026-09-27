package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FaceRetouchingNatural
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.PhotoTool
import com.example.data.VideoFilterPreset
import com.example.ui.PhotoEditorUiState
import com.example.ui.theme.RobotoMonoFamily
import com.example.ui.theme.TrackGreen
import com.example.ui.theme.WsBackground
import com.example.ui.theme.WsBorder
import com.example.ui.theme.WsCyan
import com.example.ui.theme.WsPrimaryGradient
import com.example.ui.theme.WsPurple
import com.example.ui.theme.WsSurface
import com.example.ui.theme.WsSurfaceElevated
import com.example.ui.theme.WsTextPrimary
import com.example.ui.theme.WsTextSecondary

@Composable
fun PhotoEditorScreen(
    state: PhotoEditorUiState,
    filterPresets: List<VideoFilterPreset>,
    onPickPhotoFromGallery: () -> Unit,
    onSelectTool: (PhotoTool) -> Unit,
    onSelectFilter: (String) -> Unit,
    onUpdateBeauty: (Float?, Float?, Float?) -> Unit,
    onToggleBgRemove: (Boolean?, String?) -> Unit,
    onUpdateAdjustments: (Float?, Float?, Float?, Float?) -> Unit,
    onReset: () -> Unit,
    onSavePhoto: () -> Unit
) {
    val activeFilter = filterPresets.firstOrNull { it.id == state.activeFilterId } ?: filterPresets.first()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WsBackground)
            .verticalScroll(scrollState)
            .padding(14.dp)
            .testTag("photo_editor_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "WS Photo Studio",
                    style = MaterialTheme.typography.headlineMedium,
                    color = WsTextPrimary
                )
                Text(
                    text = "RAW Color Grading • AI Beauty • Smart Cutout",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WsTextSecondary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(WsSurfaceElevated)
                        .border(1.dp, WsBorder, RoundedCornerShape(10.dp))
                        .clickable { onPickPhotoFromGallery() }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("btn_pick_photo_gallery")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Pick Photo",
                            tint = WsCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Gallery",
                            style = MaterialTheme.typography.labelSmall,
                            color = WsTextPrimary
                        )
                    }
                }

                Button(
                    onClick = onSavePhoto,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WsCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("btn_save_photo")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Export Photo",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SAVE 4K",
                        fontFamily = RobotoMonoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        state.photoSavedBanner?.let { banner ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(TrackGreen.copy(alpha = 0.16f))
                    .border(1.dp, TrackGreen, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "✓ $banner",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TrackGreen
                )
            }
        }

        // Main Photo Canvas Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black)
                .border(1.5.dp, WsPrimaryGradient, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            // If BG Remove is active, render custom studio backdrop behind portrait
            if (state.isBgRemoved) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    when (state.bgBackdropMode) {
                        "Neon Cyber Grid" -> {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    listOf(Color(0xFF041824), Color(0xFF1E0638))
                                )
                            )
                            val step = 28.dp.toPx()
                            var x = 0f
                            while (x < size.width) {
                                drawLine(
                                    color = WsCyan.copy(alpha = 0.25f),
                                    start = Offset(x, 0f),
                                    end = Offset(x, size.height),
                                    strokeWidth = 1.dp.toPx()
                                )
                                x += step
                            }
                            var y = 0f
                            while (y < size.height) {
                                drawLine(
                                    color = WsPurple.copy(alpha = 0.25f),
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = 1.dp.toPx()
                                )
                                y += step
                            }
                        }
                        "Purple Studio Glow" -> {
                            drawRect(
                                brush = Brush.radialGradient(
                                    listOf(WsPurple.copy(alpha = 0.75f), Color(0xFF090414))
                                )
                            )
                        }
                        else -> {
                            drawRect(color = Color(0xFF101014))
                        }
                    }
                }
            }

            val combinedContrast = (state.contrast * activeFilter.contrast).coerceIn(0.4f, 2.2f)
            val colorMatrix = ColorMatrix().apply {
                setToScale(combinedContrast, combinedContrast, combinedContrast, 1f)
            }

            if (!state.selectedPhotoUri.isNullOrBlank()) {
                AsyncImage(
                    model = Uri.parse(state.selectedPhotoUri),
                    contentDescription = "Selected Photo",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(colorMatrix),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (state.isBgRemoved) 18.dp else 0.dp)
                        .clip(RoundedCornerShape(if (state.isBgRemoved) 90.dp else 0.dp))
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.img_sample_portrait_photo),
                    contentDescription = "Studio Portrait Sample",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(colorMatrix),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (state.isBgRemoved) 18.dp else 0.dp)
                        .clip(RoundedCornerShape(if (state.isBgRemoved) 90.dp else 0.dp))
                )
            }

            // Beauty Neon Rim & Filter Tint Overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (activeFilter.tintColorHex != 0L) {
                    drawRect(color = Color(activeFilter.tintColorHex))
                }
                if (state.beautyNeonRim > 0.05f) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                WsCyan.copy(alpha = state.beautyNeonRim * 0.25f),
                                WsPurple.copy(alpha = state.beautyNeonRim * 0.2f),
                                Color.Transparent
                            )
                        ),
                        radius = size.minDimension * 0.65f
                    )
                }
            }

            // Status Badges on Canvas
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "LUT: ${activeFilter.name}",
                        fontFamily = RobotoMonoFamily,
                        fontSize = 10.sp,
                        color = WsCyan
                    )
                }
                if (state.isBgRemoved) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(WsPurple.copy(alpha = 0.85f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "BG REMOVED • ${state.bgBackdropMode}",
                            fontFamily = RobotoMonoFamily,
                            fontSize = 10.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // Reset button at bottom right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .border(1.dp, WsBorder, RoundedCornerShape(8.dp))
                    .clickable { onReset() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Photo Edits",
                        tint = WsTextPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Reset",
                        fontFamily = RobotoMonoFamily,
                        fontSize = 10.sp,
                        color = WsTextPrimary
                    )
                }
            }
        }

        // 4 Photo Tools Selector: Filters, Beauty, BG Remove, Adjust
        val tools: List<Pair<PhotoTool, ImageVector>> = listOf(
            PhotoTool.FILTERS to Icons.Default.FilterVintage,
            PhotoTool.BEAUTY to Icons.Default.FaceRetouchingNatural,
            PhotoTool.BG_REMOVE to Icons.Default.AutoFixHigh,
            PhotoTool.ADJUST to Icons.Default.Tune
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tools.forEach { (tool, icon) ->
                val selected = state.activeTool == tool
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selected) WsCyan else WsSurface)
                        .border(1.dp, if (selected) WsCyan else WsBorder, RoundedCornerShape(14.dp))
                        .clickable { onSelectTool(tool) }
                        .padding(vertical = 10.dp)
                        .testTag("photo_tool_${tool.name.lowercase()}"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = tool.label,
                        tint = if (selected) Color.Black else WsCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = tool.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) Color.Black else WsTextPrimary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Active Photo Tool Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(WsSurface)
                .border(1.dp, WsBorder, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (state.activeTool) {
                PhotoTool.FILTERS -> {
                    Text(
                        text = "Photo Filters & Neon LUTs",
                        style = MaterialTheme.typography.titleMedium,
                        color = WsTextPrimary
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filterPresets, key = { it.id }) { filter ->
                            val selected = filter.id == state.activeFilterId
                            Column(
                                modifier = Modifier
                                    .width(88.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) WsCyan.copy(alpha = 0.2f) else WsSurfaceElevated)
                                    .border(1.dp, if (selected) WsCyan else WsBorder, RoundedCornerShape(12.dp))
                                    .clickable { onSelectFilter(filter.id) }
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (filter.tintColorHex == 0L) Color.Gray
                                            else Color(filter.tintColorHex).copy(alpha = 0.85f)
                                        )
                                )
                                Text(
                                    text = filter.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) WsCyan else WsTextPrimary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                PhotoTool.BEAUTY -> {
                    Text(
                        text = "Portrait Retouch & Neon Rim Glow",
                        style = MaterialTheme.typography.titleMedium,
                        color = WsTextPrimary
                    )
                    PhotoSliderRow(
                        label = "Skin Smooth",
                        value = state.beautySmooth,
                        valueRange = 0f..1f,
                        onValueChange = { onUpdateBeauty(it, null, null) }
                    )
                    PhotoSliderRow(
                        label = "Neon Rim Glow",
                        value = state.beautyNeonRim,
                        valueRange = 0f..1f,
                        onValueChange = { onUpdateBeauty(null, it, null) }
                    )
                    PhotoSliderRow(
                        label = "Eye & Detail Crispness",
                        value = state.beautySharpness,
                        valueRange = 0f..1f,
                        onValueChange = { onUpdateBeauty(null, null, it) }
                    )
                }

                PhotoTool.BG_REMOVE -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "AI Subject Cutout (BG Remove)",
                                style = MaterialTheme.typography.titleMedium,
                                color = WsTextPrimary
                            )
                            Text(
                                text = "Isolate portrait & apply studio backdrop",
                                style = MaterialTheme.typography.bodyMedium,
                                color = WsTextSecondary
                            )
                        }
                        Switch(
                            checked = state.isBgRemoved,
                            onCheckedChange = { onToggleBgRemove(it, null) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = WsCyan
                            )
                        )
                    }

                    val backdrops = listOf("Neon Cyber Grid", "Purple Studio Glow", "Solid Obsidian")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        backdrops.forEach { mode ->
                            val active = state.bgBackdropMode == mode && state.isBgRemoved
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (active) WsCyan.copy(alpha = 0.2f) else WsSurfaceElevated)
                                    .border(1.dp, if (active) WsCyan else WsBorder, RoundedCornerShape(10.dp))
                                    .clickable { onToggleBgRemove(true, mode) }
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mode,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) WsCyan else WsTextPrimary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                PhotoTool.ADJUST -> {
                    Text(
                        text = "Pro Exposure & Color Adjustments",
                        style = MaterialTheme.typography.titleMedium,
                        color = WsTextPrimary
                    )
                    PhotoSliderRow(
                        label = "Contrast",
                        value = state.contrast,
                        valueRange = 0.5f..1.8f,
                        onValueChange = { onUpdateAdjustments(null, it, null, null) }
                    )
                    PhotoSliderRow(
                        label = "Saturation",
                        value = state.saturation,
                        valueRange = 0f..2.0f,
                        onValueChange = { onUpdateAdjustments(null, null, it, null) }
                    )
                    PhotoSliderRow(
                        label = "Brightness",
                        value = state.brightness,
                        valueRange = -0.5f..0.5f,
                        onValueChange = { onUpdateAdjustments(it, null, null, null) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoSliderRow(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = WsTextPrimary
            )
            Text(
                text = String.format("%.2f", value),
                fontFamily = RobotoMonoFamily,
                fontSize = 11.sp,
                color = WsCyan
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = WsCyan,
                activeTrackColor = WsCyan,
                inactiveTrackColor = WsBorder
            )
        )
    }
}
