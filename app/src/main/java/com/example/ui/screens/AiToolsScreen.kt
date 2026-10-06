package com.example.ui.screens

import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.media.MediaExportEngine
import com.example.model.SubtitleItem
import com.example.ui.components.AdMobBannerPlaceholder
import com.example.ui.components.WsInterlockedLogo
import com.example.ui.components.WsProDialog
import com.example.ui.theme.*
import com.example.viewmodel.ScreenState
import com.example.viewmodel.WsEditorViewModel
import java.io.File
import java.util.Locale

enum class AiToolCategory(val id: Int, val title: String, val subtitle: String, val icon: ImageVector) {
    ALL(0, "All Tools", "Complete Studio Suite", Icons.Default.AutoAwesome),
    IMAGE_GENERATOR(1, "AI Image Generator", "Text-to-Image Synthesis", Icons.Default.AutoAwesome),
    BG_REMOVER(2, "AI Background Remover", "Subject Cutout to Transparent PNG", Icons.Default.ContentCut),
    BG_GENERATOR(3, "AI Background Generator", "Prompt-Powered Studio Backdrops", Icons.Default.Image),
    AUTO_CAPTIONS(4, "AI Auto Captions", "Synchronized Reel & Video Subtitles", Icons.Default.Subtitles),
    VOICE_TOOLS(5, "AI Voice Tools", "Text-to-Speech & Vocal Timbre", Icons.Default.GraphicEq),
    VIDEO_TOOLS(6, "AI Video Tools", "Image-to-Video & Motion Physics", Icons.Default.Videocam),
    PHOTO_ENHANCER(7, "AI Photo Enhancer", "Neural HDR & Ultra Sharpness", Icons.Default.HighQuality)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiToolsScreen(viewModel: WsEditorViewModel) {
    val context = LocalContext.current
    var activeTool by remember { mutableStateOf(AiToolCategory.ALL) }
    var showProDialog by remember { mutableStateOf(false) }

    val isPro by viewModel.monetization.isProUser.collectAsState()
    val isProcessing by viewModel.aiProcessing.collectAsState()
    val aiProgress by viewModel.aiProgress.collectAsState()
    val aiError by viewModel.aiError.collectAsState()

    BackHandler {
        viewModel.navigateTo(ScreenState.HOME)
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
                    IconButton(onClick = { viewModel.navigateTo(ScreenState.HOME) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = WsTextSecondary
                        )
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WsInterlockedLogo(size = 28.dp, strokeWidthDp = 2.8.dp, color = WsElectricCyan)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AI Studio",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = WsTextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(WsElectricBlue.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .border(1.dp, WsElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "NEURAL 4K",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp,
                                color = WsElectricCyan
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showProDialog = true },
                        color = if (isPro) WsElectricBlue.copy(alpha = 0.2f) else WsSurfaceCardElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isPro) WsElectricCyan else WsBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.WorkspacePremium,
                                contentDescription = "WS Pro",
                                tint = if (isPro) WsAccentGold else WsElectricCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPro) "PRO ACTIVE" else "PRO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPro) WsAccentGold else WsTextPrimary
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (!isPro) {
                AdMobBannerPlaceholder()
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            val contentWidth = maxWidth.coerceAtMost(640.dp)

            LazyColumn(
                modifier = Modifier
                    .width(contentWidth)
                    .fillMaxHeight(),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Tool Selection Horizontal Bar
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "AI TOOLS SUITE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = WsTextSecondary
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(AiToolCategory.entries) { cat ->
                                val isSelected = activeTool == cat
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { activeTool = cat },
                                    color = if (isSelected) WsElectricBlue else WsSurfaceCard,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) WsElectricCyan else WsBorder
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = cat.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) WsTextPrimary else WsElectricCyan,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = cat.title,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) WsTextPrimary else WsTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Global Error State with Retry
                if (aiError != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = WsSurfaceCardElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WsAccentRed.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = WsAccentRed, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = aiError!!,
                                        fontSize = 12.5.sp,
                                        color = WsTextPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(
                                        onClick = { viewModel.retryLastAiAction() },
                                        colors = ButtonDefaults.buttonColors(containerColor = WsElectricBlue),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(13.dp), tint = WsTextPrimary)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Retry", color = WsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    TextButton(onClick = { viewModel.clearAiError() }) {
                                        Text("Dismiss", color = WsTextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Global Processing Indicator
                if (isProcessing) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = WsSurfaceCardElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WsBorderGlow),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = WsElectricCyan,
                                            strokeWidth = 2.5.dp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Processing with WS Neural Engine...",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WsTextPrimary
                                        )
                                    }
                                    Text(
                                        text = "$aiProgress%",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = WsElectricCyan
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { aiProgress / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = WsElectricBlue,
                                    trackColor = WsButtonDark
                                )
                            }
                        }
                    }
                }

                // 2. ACTIVE TOOL CARD WORKSPACE (Dashboard: All separate cards or filtered)
                if (activeTool == AiToolCategory.ALL) {
                    item { AiImageGeneratorCard(viewModel, isProcessing) }
                    item { AiBackgroundRemoverCard(viewModel, isProcessing) }
                    item { AiBackgroundGeneratorCard(viewModel, isProcessing) }
                    item { AiAutoCaptionsCard(viewModel, isProcessing) }
                    item { AiVoiceToolsCard(viewModel, isProcessing) }
                    item { AiVideoToolsCard(viewModel, isProcessing) }
                    item { AiPhotoEnhancerCard(viewModel, isProcessing) }
                } else {
                    item {
                        when (activeTool) {
                            AiToolCategory.IMAGE_GENERATOR -> AiImageGeneratorCard(viewModel, isProcessing)
                            AiToolCategory.BG_REMOVER -> AiBackgroundRemoverCard(viewModel, isProcessing)
                            AiToolCategory.BG_GENERATOR -> AiBackgroundGeneratorCard(viewModel, isProcessing)
                            AiToolCategory.AUTO_CAPTIONS -> AiAutoCaptionsCard(viewModel, isProcessing)
                            AiToolCategory.VOICE_TOOLS -> AiVoiceToolsCard(viewModel, isProcessing)
                            AiToolCategory.VIDEO_TOOLS -> AiVideoToolsCard(viewModel, isProcessing)
                            AiToolCategory.PHOTO_ENHANCER -> AiPhotoEnhancerCard(viewModel, isProcessing)
                            AiToolCategory.ALL -> {}
                        }
                    }
                }
            }
        }
    }

    if (showProDialog) {
        WsProDialog(
            isPro = isPro,
            onDismiss = { showProDialog = false },
            onUpgrade = {
                viewModel.monetization.upgradeToPro()
                showProDialog = false
                Toast.makeText(context, "Welcome to WS Pro!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// ====================================================================
// 1. AI IMAGE GENERATOR CARD
// ====================================================================
@Composable
private fun AiImageGeneratorCard(viewModel: WsEditorViewModel, isProcessing: Boolean) {
    val context = LocalContext.current
    var prompt by remember { mutableStateOf("") }
    var selectedCount by remember { mutableIntStateOf(1) }
    val generatedImages by viewModel.aiGeneratedImages.collectAsState()
    var previewImage by remember { mutableStateOf<String?>(null) }

    val promptIdeas = listOf(
        "Futuristic cybernetic city with neon rain",
        "Cinematic portrait with soft studio rim lighting",
        "Hypercar speeding on wet mountain curves",
        "Minimalist aesthetic product on black matte stone"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WsPureWhite),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, WsBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ToolCardHeader(
                title = "AI Image Generator",
                badge = "MULTIPLE VARIATIONS",
                subtitle = "Synthesize ultra-detailed concept artwork from text prompts."
            )

            AiPromptInputField(
                value = prompt,
                onValueChange = { prompt = it },
                placeholderText = "Describe anything to generate in high-res...",
                testTag = "ai_image_prompt_input",
                onClear = { prompt = "" }
            )

            // Prompt suggestions
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(promptIdeas) { idea ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { prompt = idea },
                        color = WsOffWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder)
                    ) {
                        Text(
                            text = idea,
                            fontSize = 11.sp,
                            color = WsBlack,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Image Count Selector (1, 2, 4)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("VARIATIONS COUNT", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = WsMediumGray)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(1, 2, 4).forEach { count ->
                        val isSel = selectedCount == count
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedCount = count },
                            color = if (isSel) WsBlack else WsOffWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) WsBlack else WsBorder)
                        ) {
                            Text(
                                text = "$count Image${if (count > 1) "s" else ""}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) WsPureWhite else WsBlack,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val p = if (prompt.isNotBlank()) prompt else "Cinematic 4K artwork"
                    viewModel.generateMultipleAiImages(p, selectedCount)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WsBlack),
                enabled = !isProcessing
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate $selectedCount AI Artwork${if (selectedCount > 1) "s" else ""}", fontWeight = FontWeight.Bold)
            }

            // Generated Images Grid
            if (generatedImages.isNotEmpty()) {
                Text("GENERATED RESULTS (TAP TO PREVIEW)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WsMediumGray)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    generatedImages.forEachIndexed { idx, path ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black)
                                .border(1.dp, WsBorder, RoundedCornerShape(12.dp))
                                .clickable { previewImage = path }
                        ) {
                            AsyncImage(
                                model = path,
                                contentDescription = "AI Result $idx",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }

    // High-res Image Preview Dialog with Save & Export
    if (previewImage != null) {
        Dialog(onDismissRequest = { previewImage = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = WsPureWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, WsBlack),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("AI Image Preview", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = WsBlack)
                        IconButton(onClick = { previewImage = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = WsBlack)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black)
                    ) {
                        AsyncImage(
                            model = previewImage,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val file = File(previewImage!!)
                                val uri = Uri.fromFile(file)
                                viewModel.createPhotoProject(uri, "AI Artwork Project")
                                previewImage = null
                                Toast.makeText(context, "Saved to Project! Opening Photo Editor...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WsBlack)
                        ) {
                            Text("Save to Project", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = {
                                val file = File(previewImage!!)
                                val shareIntent = MediaExportEngine.createShareIntent(context, file, "image/png")
                                context.startActivity(android.content.Intent.createChooser(shareIntent, "Share AI Artwork"))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WsBlack)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share / Export", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// ====================================================================
// 2. AI BACKGROUND REMOVER CARD
// ====================================================================
@Composable
private fun AiBackgroundRemoverCard(viewModel: WsEditorViewModel, isProcessing: Boolean) {
    val context = LocalContext.current
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val cutoutUri by viewModel.aiBgCutoutUri.collectAsState()

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WsPureWhite),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, WsBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ToolCardHeader(
                title = "AI Background Remover",
                badge = "TRANSPARENT PNG",
                subtitle = "Instant neural foreground detection with smooth transparent edges."
            )

            // Photo picker button
            OutlinedButton(
                onClick = { galleryPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WsBlack)
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (selectedPhotoUri != null) "Photo Imported • Tap to Change" else "Import Photo from Gallery")
            }

            if (selectedPhotoUri != null) {
                Button(
                    onClick = { viewModel.removePhotoBackground(selectedPhotoUri!!) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WsBlack),
                    enabled = !isProcessing
                ) {
                    Icon(Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Remove Background Now", fontWeight = FontWeight.Bold)
                }
            }

            // Cutout Result over Checkered Canvas
            if (cutoutUri != null) {
                Text("TRANSPARENT CUTOUT RESULT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WsMediumGray)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, WsBorder, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CheckeredBackgroundPattern()
                    AsyncImage(
                        model = cutoutUri,
                        contentDescription = "Cutout Result",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val file = File(cutoutUri!!)
                            val uri = Uri.fromFile(file)
                            viewModel.createPhotoProject(uri, "Cutout Photo Project")
                            Toast.makeText(context, "Saved to Projects!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WsBlack)
                    ) {
                        Text("Save as Project", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val file = File(cutoutUri!!)
                            val shareIntent = MediaExportEngine.createShareIntent(context, file, "image/png")
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Transparent PNG"))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WsBlack)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export PNG", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ====================================================================
// 3. AI BACKGROUND GENERATOR CARD
// ====================================================================
@Composable
private fun AiBackgroundGeneratorCard(viewModel: WsEditorViewModel, isProcessing: Boolean) {
    val context = LocalContext.current
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var bgPrompt by remember { mutableStateOf("") }
    val generatedBgUri by viewModel.aiBgGeneratedUri.collectAsState()
    var showOriginal by remember { mutableStateOf(false) }

    val presetBgIdeas = listOf(
        "Luxury penthouse with sunset skyline",
        "Neon futuristic cyber room",
        "Minimalist aesthetic white studio",
        "Tropical sunset palm beach"
    )

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WsPureWhite),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, WsBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ToolCardHeader(
                title = "AI Background Generator",
                badge = "PROMPT BACKDROP",
                subtitle = "Replace ordinary backgrounds with bespoke AI-generated scenery."
            )

            OutlinedButton(
                onClick = { galleryPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WsBlack)
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (selectedPhotoUri != null) "Photo Imported (Ready)" else "Import Subject Photo")
            }

            AiPromptInputField(
                value = bgPrompt,
                onValueChange = { bgPrompt = it },
                placeholderText = "e.g. Sunset penthouse, neon city, tropical beach...",
                testTag = "ai_bg_prompt_input",
                onClear = { bgPrompt = "" }
            )

            // Preset ideas
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(presetBgIdeas) { idea ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { bgPrompt = idea },
                        color = WsOffWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder)
                    ) {
                        Text(
                            text = idea,
                            fontSize = 11.sp,
                            color = WsBlack,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            if (selectedPhotoUri != null) {
                Button(
                    onClick = {
                        val p = if (bgPrompt.isNotBlank()) bgPrompt else "Modern architectural studio"
                        viewModel.generatePhotoBackground(selectedPhotoUri!!, p)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WsBlack),
                    enabled = !isProcessing
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate & Composite Background", fontWeight = FontWeight.Bold)
                }
            }

            // Preview with Before / After Toggle
            if (generatedBgUri != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("PREVIEW RESULT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WsMediumGray)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(WsOffWhite)
                            .border(1.dp, WsBorder, RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (!showOriginal) WsBlack else Color.Transparent)
                                .clickable { showOriginal = false }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("AI New BG", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (!showOriginal) WsPureWhite else WsBlack)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (showOriginal) WsBlack else Color.Transparent)
                                .clickable { showOriginal = true }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("Original", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (showOriginal) WsPureWhite else WsBlack)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = if (showOriginal) selectedPhotoUri else generatedBgUri,
                        contentDescription = "Background Result",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val file = File(generatedBgUri!!)
                            val uri = Uri.fromFile(file)
                            viewModel.createPhotoProject(uri, "AI Backdrop Project")
                            Toast.makeText(context, "Saved to Projects!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WsBlack)
                    ) {
                        Text("Apply & Save Project", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val file = File(generatedBgUri!!)
                            val shareIntent = MediaExportEngine.createShareIntent(context, file, "image/png")
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share AI Composite"))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WsBlack)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export / Share", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ====================================================================
// 4. AI AUTO CAPTIONS CARD
// ====================================================================
@Composable
private fun AiAutoCaptionsCard(viewModel: WsEditorViewModel, isProcessing: Boolean) {
    val context = LocalContext.current
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var captionStyle by remember { mutableStateOf("Viral Dynamic") }
    var captionSize by remember { mutableFloatStateOf(20f) }
    var captionPosition by remember { mutableStateOf("Bottom") }
    val captionsList by viewModel.aiCaptionsList.collectAsState()

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WsPureWhite),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, WsBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ToolCardHeader(
                title = "AI Auto Captions",
                badge = "SPEECH SYNC",
                subtitle = "Generate word-by-word synchronized animated subtitles for reels & clips."
            )

            OutlinedButton(
                onClick = { videoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WsBlack)
            ) {
                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (selectedVideoUri != null) "Video Selected (Ready)" else "Import Video from Gallery")
            }

            // Style and Position controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("CAPTION POSITION", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = WsMediumGray)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Bottom", "Center", "Top").forEach { pos ->
                        val isSel = captionPosition == pos
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { captionPosition = pos },
                            color = if (isSel) WsBlack else WsOffWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) WsBlack else WsBorder)
                        ) {
                            Text(
                                text = pos,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) WsPureWhite else WsBlack,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Text("CAPTION FONT SIZE: ${captionSize.toInt()}sp", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = WsMediumGray)
            Slider(
                value = captionSize,
                onValueChange = { captionSize = it },
                valueRange = 14f..36f,
                colors = SliderDefaults.colors(thumbColor = WsBlack, activeTrackColor = WsBlack)
            )

            // Caption Style Selector
            Text("CAPTION PRESET STYLE", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = WsMediumGray)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Viral Dynamic", "Cinematic Sub", "Yellow Glow", "Bold Box").forEach { style ->
                    val isSel = captionStyle == style
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { captionStyle = style },
                        color = if (isSel) WsBlack else WsOffWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) WsBlack else WsBorder)
                    ) {
                        Text(
                            text = style,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) WsPureWhite else WsBlack,
                            modifier = Modifier.padding(vertical = 6.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.generateAutoCaptionsForVideo(12000L, captionStyle)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WsBlack),
                enabled = !isProcessing
            ) {
                Icon(Icons.Default.Subtitles, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Synced Captions", fontWeight = FontWeight.Bold)
            }

            // Editable Caption Lines List
            if (captionsList.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("EDITABLE CAPTIONS (${captionsList.size} SEGMENTS)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WsMediumGray)
                    TextButton(onClick = { viewModel.addAiCaptionItem() }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = WsBlack)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Line", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WsBlack)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    captionsList.forEach { cap ->
                        var lineText by remember(cap.id) { mutableStateOf(cap.text) }

                        Surface(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)),
                            color = WsOffWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${cap.startTimelineMs / 1000}s",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WsMediumGray,
                                    modifier = Modifier.width(32.dp)
                                )
                                OutlinedTextField(
                                    value = lineText,
                                    onValueChange = {
                                        lineText = it
                                        viewModel.updateAiCaptionItem(cap.id, it)
                                    },
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    textStyle = TextStyle(color = Color(0xFF0F172A), fontSize = 13.sp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color(0xFF0F172A),
                                        unfocusedTextColor = Color(0xFF0F172A),
                                        focusedContainerColor = Color(0xFFFFFFFF),
                                        unfocusedContainerColor = Color(0xFFF8FAFC),
                                        cursorColor = WsElectricBlue,
                                        focusedBorderColor = WsElectricBlue,
                                        unfocusedBorderColor = Color(0xFFCBD5E1)
                                    )
                                )
                                IconButton(onClick = { viewModel.deleteAiCaptionItem(cap.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = WsAccentRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        if (selectedVideoUri != null) {
                            viewModel.createVideoProject(listOf(selectedVideoUri!!), "Subtitled Video Project")
                        } else {
                            viewModel.navigateTo(ScreenState.VIDEO_EDITOR)
                        }
                        Toast.makeText(context, "Captions Applied to Video Timeline!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WsBlack)
                ) {
                    Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Apply to Video Editor Timeline", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ====================================================================
// 5. AI VOICE TOOLS CARD
// ====================================================================
@Composable
private fun AiVoiceToolsCard(viewModel: WsEditorViewModel, isProcessing: Boolean) {
    val context = LocalContext.current
    var speechText by remember { mutableStateOf("") }
    var selectedPersona by remember { mutableStateOf("Cinematic Narrator") }
    var voicePitch by remember { mutableFloatStateOf(1.0f) }
    var voiceSpeed by remember { mutableFloatStateOf(1.0f) }

    val generatedAudioUri by viewModel.aiVoiceAudioUri.collectAsState()
    var isAudioPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // Live Voice Recorder State
    var isRecording by remember { mutableStateOf(false) }
    var recordedFile by remember { mutableStateOf<File?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WsPureWhite),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, WsBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ToolCardHeader(
                title = "AI Voice Tools",
                badge = "TTS & VOICEOVER",
                subtitle = "Generate ultra-realistic narrator speech or record crisp studio voiceovers."
            )

            // Persona selection
            Text("NARRATOR VOICE PERSONA", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = WsMediumGray)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(listOf("Cinematic Narrator", "Studio Host", "Calm Guide", "Tech Reviewer", "Energetic Creator")) { persona ->
                    val isSel = selectedPersona == persona
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedPersona = persona },
                        color = if (isSel) WsBlack else WsOffWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) WsBlack else WsBorder)
                    ) {
                        Text(
                            text = persona,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) WsPureWhite else WsBlack,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            AiPromptInputField(
                value = speechText,
                onValueChange = { speechText = it },
                placeholderText = "Enter script for AI narrator to speak aloud...",
                testTag = "ai_speech_prompt_input",
                onClear = { speechText = "" }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("PITCH: ${String.format(Locale.US, "%.1f", voicePitch)}x", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = WsMediumGray)
                    Slider(
                        value = voicePitch,
                        onValueChange = { voicePitch = it },
                        valueRange = 0.7f..1.4f,
                        colors = SliderDefaults.colors(thumbColor = WsBlack, activeTrackColor = WsBlack)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("SPEED: ${String.format(Locale.US, "%.1f", voiceSpeed)}x", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = WsMediumGray)
                    Slider(
                        value = voiceSpeed,
                        onValueChange = { voiceSpeed = it },
                        valueRange = 0.7f..1.4f,
                        colors = SliderDefaults.colors(thumbColor = WsBlack, activeTrackColor = WsBlack)
                    )
                }
            }

            // Generate Speech Button
            Button(
                onClick = {
                    val txt = if (speechText.isNotBlank()) speechText else "Welcome to WS Series Professional Video Editor"
                    viewModel.generateSpeechAudio(txt, selectedPersona, voicePitch, voiceSpeed)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WsBlack),
                enabled = !isProcessing
            ) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate AI Speech Audio", fontWeight = FontWeight.Bold)
            }

            // Generated Speech Player Bar
            if (generatedAudioUri != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                    color = WsOffWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (isAudioPlaying) {
                                        mediaPlayer?.stop()
                                        mediaPlayer?.release()
                                        mediaPlayer = null
                                        isAudioPlaying = false
                                    } else {
                                        try {
                                            mediaPlayer = MediaPlayer().apply {
                                                setDataSource(generatedAudioUri!!)
                                                prepare()
                                                start()
                                                setOnCompletionListener {
                                                    isAudioPlaying = false
                                                }
                                            }
                                            isAudioPlaying = true
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Playback error", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = WsBlack
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(selectedPersona, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = WsBlack)
                                Text("44.1 kHz Master Audio", fontSize = 10.sp, color = WsMediumGray)
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.addAudioToVideoTimeline(generatedAudioUri!!, "AI $selectedPersona")
                                Toast.makeText(context, "Voice Added to Video Timeline!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WsBlack)
                        ) {
                            Text("Add to Timeline", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Live Microphone Recording Strip
            Text("OR RECORD DIRECT MIC VOICEOVER", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = WsMediumGray)

            val micPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    viewModel.voiceRecorder.startRecording(
                        onStarted = { isRecording = true },
                        onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                    )
                } else {
                    Toast.makeText(context, "Microphone permission is required to record audio", Toast.LENGTH_SHORT).show()
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (!isRecording) {
                            micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                        } else {
                            val file = viewModel.voiceRecorder.stopRecording()
                            isRecording = false
                            recordedFile = file
                            if (file != null) {
                                Toast.makeText(context, "Recording Saved!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isRecording) WsAccentRed else WsBlack)
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isRecording) "Stop Recording (Live)" else "Record Mic Voiceover")
                }

                if (recordedFile != null) {
                    Button(
                        onClick = {
                            viewModel.addAudioToVideoTimeline(recordedFile!!.absolutePath, "Mic Voiceover")
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WsBlack),
                        modifier = Modifier.height(46.dp)
                    ) {
                        Text("Add to Video", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ====================================================================
// 6. AI VIDEO TOOLS CARD
// ====================================================================
@Composable
private fun AiVideoToolsCard(viewModel: WsEditorViewModel, isProcessing: Boolean) {
    val context = LocalContext.current
    var motionPrompt by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableIntStateOf(4) }
    var selectedAspect by remember { mutableStateOf("16:9") }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val generatedVideoUri by viewModel.aiVideoGeneratedUri.collectAsState()

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WsPureWhite),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, WsBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ToolCardHeader(
                title = "AI Video Tools",
                badge = "IMAGE TO VIDEO",
                subtitle = "Breathe cinematic camera motion and physics into static frames."
            )

            OutlinedButton(
                onClick = { galleryPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WsBlack)
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (selectedPhotoUri != null) "Keyframe Photo Imported" else "Import Static Photo for Motion")
            }

            AiPromptInputField(
                value = motionPrompt,
                onValueChange = { motionPrompt = it },
                placeholderText = "e.g. Slow cinematic zoom-in, drone pan, wave ripple...",
                testTag = "ai_motion_prompt_input",
                onClear = { motionPrompt = "" }
            )

            // Duration & Aspect Ratio Pickers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("DURATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WsMediumGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(3, 5, 8).forEach { dur ->
                            val isSel = selectedDuration == dur
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { selectedDuration = dur },
                                color = if (isSel) WsBlack else WsOffWhite,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) WsBlack else WsBorder)
                            ) {
                                Text(
                                    text = "${dur}s",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) WsPureWhite else WsBlack,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Column {
                    Text("RATIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WsMediumGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("16:9", "9:16").forEach { ratio ->
                            val isSel = selectedAspect == ratio
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { selectedAspect = ratio },
                                color = if (isSel) WsBlack else WsOffWhite,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) WsBlack else WsBorder)
                            ) {
                                Text(
                                    text = ratio,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) WsPureWhite else WsBlack,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val p = if (motionPrompt.isNotBlank()) motionPrompt else "Cinematic dynamic motion"
                    viewModel.generateAiVideo(selectedPhotoUri, p, selectedDuration, selectedAspect)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WsBlack),
                enabled = !isProcessing
            ) {
                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate AI Motion Video", fontWeight = FontWeight.Bold)
            }

            // Generated Video Result Strip
            if (generatedVideoUri != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                    color = WsOffWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Motion Video Generated", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = WsBlack)
                            Text("MP4 • ${selectedDuration}s", fontSize = 10.sp, color = WsMediumGray)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.importGeneratedVideoToEditor(generatedVideoUri!!)
                                    Toast.makeText(context, "Imported to Video Editor!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WsBlack)
                            ) {
                                Text("Import to Video Editor", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val file = File(generatedVideoUri!!)
                                    val shareIntent = MediaExportEngine.createShareIntent(context, file, "video/mp4")
                                    context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Motion Video"))
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = WsBlack)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Export / Share", fontSize = 11.5.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ====================================================================
// 7. AI PHOTO ENHANCER CARD
// ====================================================================
@Composable
private fun AiPhotoEnhancerCard(viewModel: WsEditorViewModel, isProcessing: Boolean) {
    val context = LocalContext.current
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val enhancedUri by viewModel.aiEnhancedImageUri.collectAsState()
    var showBefore by remember { mutableStateOf(false) }

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WsPureWhite),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, WsBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ToolCardHeader(
                title = "AI Photo Enhancer",
                badge = "HDR & CLARITY",
                subtitle = "Deep neural resolution recovery, texture synthesis and micro-contrast boost."
            )

            OutlinedButton(
                onClick = { galleryPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WsBlack)
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (selectedPhotoUri != null) "Photo Imported • Ready to Enhance" else "Import Photo from Gallery")
            }

            if (selectedPhotoUri != null) {
                Button(
                    onClick = { viewModel.enhancePhoto(selectedPhotoUri!!) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WsBlack),
                    enabled = !isProcessing
                ) {
                    Icon(Icons.Default.HighQuality, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enhance Quality & Sharpness", fontWeight = FontWeight.Bold)
                }
            }

            // Before / After Preview
            if (enhancedUri != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("BEFORE / AFTER COMPARISON", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WsMediumGray)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(WsOffWhite)
                            .border(1.dp, WsBorder, RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (!showBefore) WsBlack else Color.Transparent)
                                .clickable { showBefore = false }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("Enhanced", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (!showBefore) WsPureWhite else WsBlack)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (showBefore) WsBlack else Color.Transparent)
                                .clickable { showBefore = true }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("Original", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (showBefore) WsPureWhite else WsBlack)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = if (showBefore) selectedPhotoUri else enhancedUri,
                        contentDescription = "Enhanced Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val file = File(enhancedUri!!)
                            val uri = Uri.fromFile(file)
                            viewModel.createPhotoProject(uri, "AI Enhanced Photo")
                            Toast.makeText(context, "Saved to Projects!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WsBlack)
                    ) {
                        Text("Save to Project", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val file = File(enhancedUri!!)
                            val shareIntent = MediaExportEngine.createShareIntent(context, file, "image/png")
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Enhanced Image"))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WsBlack)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export / Share", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolCardHeader(title: String, badge: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = WsBlack)
            Box(
                modifier = Modifier
                    .background(Color(0xFF26282B), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = badge, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = WsAccentGold, letterSpacing = 0.5.sp)
            }
        }
        Text(text = subtitle, fontSize = 12.sp, color = WsMediumGray)
    }
}

@Composable
private fun CheckeredBackgroundPattern() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val squareSize = 24f
        val numCols = (size.width / squareSize).toInt() + 1
        val numRows = (size.height / squareSize).toInt() + 1
        val lightColor = Color(0xFFF1F3F5)
        val darkColor = Color(0xFFE2E8F0)

        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                val color = if ((row + col) % 2 == 0) lightColor else darkColor
                drawRect(
                    color = color,
                    topLeft = androidx.compose.ui.geometry.Offset(col * squareSize, row * squareSize),
                    size = androidx.compose.ui.geometry.Size(squareSize, squareSize)
                )
            }
        }
    }
}

@Composable
private fun AiPromptInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier,
    minLines: Int = 3,
    maxLines: Int = 6,
    testTag: String = "ai_prompt_input",
    onClear: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            placeholder = {
                Text(
                    text = placeholderText,
                    color = Color(0xFF64748B),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            textStyle = TextStyle(
                color = Color(0xFF0F172A),
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 21.sp
            ),
            shape = RoundedCornerShape(12.dp),
            minLines = minLines,
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            ),
            trailingIcon = if (value.isNotEmpty()) {
                {
                    IconButton(
                        onClick = {
                            if (onClear != null) onClear() else onValueChange("")
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear Prompt",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF0F172A),
                unfocusedTextColor = Color(0xFF0F172A),
                focusedContainerColor = Color(0xFFFFFFFF),
                unfocusedContainerColor = Color(0xFFF8FAFC),
                cursorColor = WsElectricBlue,
                focusedBorderColor = WsElectricBlue,
                unfocusedBorderColor = Color(0xFFCBD5E1),
                selectionColors = TextSelectionColors(
                    handleColor = WsElectricBlue,
                    backgroundColor = Color(0x330070F3)
                )
            )
        )
        if (value.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${value.length} characters",
                    fontSize = 10.5.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
