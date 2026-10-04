package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ProjectEntity
import com.example.ui.components.AdMobBannerPlaceholder
import com.example.ui.components.SettingsDialog
import com.example.ui.components.WsInterlockedLogo
import com.example.ui.components.WsProDialog
import com.example.ui.theme.WsAccentGold
import com.example.ui.theme.WsBackground
import com.example.ui.theme.WsBorder
import com.example.ui.theme.WsBorderGlow
import com.example.ui.theme.WsButtonDark
import com.example.ui.theme.WsElectricBlue
import com.example.ui.theme.WsElectricCyan
import com.example.ui.theme.WsMediumGray
import com.example.ui.theme.WsSurfaceCard
import com.example.ui.theme.WsSurfaceCardElevated
import com.example.ui.theme.WsTextPrimary
import com.example.ui.theme.WsTextSecondary
import com.example.viewmodel.ScreenState
import com.example.viewmodel.WsEditorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: WsEditorViewModel) {
    val recentProjects by viewModel.recentProjects.collectAsState()
    val draftProjects by viewModel.draftProjects.collectAsState()
    val deletedProjects by viewModel.deletedProjects.collectAsState()
    val isPro by viewModel.monetization.isProUser.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Recents, 1: Drafts, 2: Trash
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showProDialog by remember { mutableStateOf(false) }

    // Rename project dialog state
    var projectToRename by remember { mutableStateOf<ProjectEntity?>(null) }
    var renameText by remember { mutableStateOf("") }

    // Gallery Pickers
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.createVideoProject(uris)
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.createPhotoProject(uri)
        }
    }

    val importMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.createVideoProject(uris, "Imported Project")
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
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WsInterlockedLogo(size = 28.dp, strokeWidthDp = 2.8.dp, color = WsElectricCyan)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "WS Studio",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp,
                            color = WsTextPrimary
                        )
                    }
                },
                actions = {
                    // Pro Badge Button
                    Surface(
                        modifier = Modifier
                            .padding(end = 6.dp)
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
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPro) "PRO" else "UPGRADE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = if (isPro) WsAccentGold else WsTextPrimary
                            )
                        }
                    }

                    // Settings Button
                    Surface(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { showSettingsDialog = true }
                            .testTag("settings_button"),
                        color = WsSurfaceCardElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = WsTextSecondary,
                                modifier = Modifier.size(20.dp)
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
                // 1. HERO SECTION: Large New Project Card & Import Media
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Main Hero: New Project Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(116.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    videoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                    )
                                }
                                .testTag("new_project_button"),
                            colors = CardDefaults.cardColors(containerColor = WsSurfaceCardElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WsBorderGlow),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF161A28),
                                                Color(0xFF10131E)
                                            )
                                        )
                                    )
                                    .padding(horizontal = 20.dp, vertical = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "New Project",
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = (-0.3).sp,
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
                                                    text = "4K PRO",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 0.5.sp,
                                                    color = WsElectricCyan
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Multi-track video and photo timeline",
                                            fontSize = 12.5.sp,
                                            color = WsTextSecondary
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(WsElectricBlue, WsElectricCyan)
                                                ),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Create New Project",
                                            tint = WsTextPrimary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Secondary Strip: Import Media direct from gallery
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    importMediaLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                    )
                                }
                                .testTag("import_media_button"),
                            color = WsSurfaceCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(WsButtonDark, RoundedCornerShape(8.dp))
                                            .border(1.dp, WsBorder, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.AddPhotoAlternate,
                                            contentDescription = null,
                                            tint = WsElectricCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Import Media",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WsTextPrimary
                                        )
                                        Text(
                                            text = "Pick videos or photos directly from gallery",
                                            fontSize = 11.sp,
                                            color = WsTextSecondary
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .background(WsButtonDark, RoundedCornerShape(6.dp))
                                        .border(1.dp, WsBorder, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "GALLERY",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp,
                                        color = WsElectricCyan
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. CREATIVE EDITING MODES: Video Editor, Photo Editor, AI Tools
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "CREATE & EDIT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = WsTextSecondary,
                            modifier = Modifier.padding(start = 2.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            EditorModeTile(
                                modifier = Modifier.weight(1f),
                                title = "Video Editor",
                                subtitle = "Timeline & Audio",
                                icon = Icons.Default.Movie,
                                accentColor = WsElectricBlue,
                                onClick = {
                                    videoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                    )
                                }
                            )
                            EditorModeTile(
                                modifier = Modifier.weight(1f),
                                title = "Photo Editor",
                                subtitle = "Filters & Retouch",
                                icon = Icons.Default.Image,
                                accentColor = WsElectricCyan,
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            )
                            EditorModeTile(
                                modifier = Modifier.weight(1f),
                                title = "AI Tools",
                                subtitle = "Smart Studio",
                                icon = Icons.Default.AutoAwesome,
                                accentColor = WsElectricCyan,
                                isGlow = true,
                                onClick = {
                                    viewModel.navigateTo(ScreenState.AI_TOOLS)
                                }
                            )
                        }
                    }
                }

                // 3. AI TOOLS SPOTLIGHT SECTION
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = WsElectricCyan,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AI TOOLS SUITE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = WsTextSecondary
                                )
                            }
                            Text(
                                text = "VIEW ALL →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WsElectricBlue,
                                modifier = Modifier.clickable { viewModel.navigateTo(ScreenState.AI_TOOLS) }
                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                AiSpotlightCard(
                                    title = "AI Image Gen",
                                    subtitle = "Text to 4K Art",
                                    icon = Icons.Default.AutoAwesome,
                                    onClick = { viewModel.navigateTo(ScreenState.AI_TOOLS) }
                                )
                            }
                            item {
                                AiSpotlightCard(
                                    title = "BG Remover",
                                    subtitle = "1-Click Cutout",
                                    icon = Icons.Default.ContentCut,
                                    onClick = { viewModel.navigateTo(ScreenState.AI_TOOLS) }
                                )
                            }
                            item {
                                AiSpotlightCard(
                                    title = "Auto Captions",
                                    subtitle = "Speech to Sync",
                                    icon = Icons.Default.Subtitles,
                                    onClick = { viewModel.navigateTo(ScreenState.AI_TOOLS) }
                                )
                            }
                            item {
                                AiSpotlightCard(
                                    title = "AI Voice Tools",
                                    subtitle = "TTS & Voiceover",
                                    icon = Icons.Default.GraphicEq,
                                    onClick = { viewModel.navigateTo(ScreenState.AI_TOOLS) }
                                )
                            }
                        }
                    }
                }

                // 4. RECENT PROJECTS SECTION
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Projects",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = WsTextPrimary,
                                modifier = Modifier.padding(start = 2.dp)
                            )

                            // Clean Pill Selector for Tabs
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ProjectTabPill(
                                    label = "Recent (${recentProjects.size})",
                                    isSelected = selectedTab == 0,
                                    onClick = { selectedTab = 0 }
                                )
                                ProjectTabPill(
                                    label = "Drafts (${draftProjects.size})",
                                    isSelected = selectedTab == 1,
                                    onClick = { selectedTab = 1 }
                                )
                                ProjectTabPill(
                                    label = "Trash (${deletedProjects.size})",
                                    isSelected = selectedTab == 2,
                                    onClick = { selectedTab = 2 }
                                )
                            }
                        }
                    }
                }

                // Projects List or Empty State
                val currentList = when (selectedTab) {
                    0 -> recentProjects
                    1 -> draftProjects
                    else -> deletedProjects
                }

                if (currentList.isEmpty()) {
                    item {
                        EmptyProjectsState(
                            tabIndex = selectedTab,
                            onImportMedia = {
                                importMediaLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                )
                            }
                        )
                    }
                } else {
                    items(currentList, key = { it.id }) { project ->
                        ProjectCard(
                            project = project,
                            isTrash = selectedTab == 2,
                            onClick = {
                                if (selectedTab != 2) {
                                    viewModel.openExistingProject(project)
                                }
                            },
                            onRename = {
                                projectToRename = project
                                renameText = project.title
                            },
                            onDuplicate = {
                                viewModel.duplicateProject(project.id)
                            },
                            onDelete = {
                                if (selectedTab == 2) {
                                    viewModel.permanentlyDeleteProject(project.id)
                                } else {
                                    viewModel.softDeleteProject(project.id)
                                }
                            },
                            onRestore = {
                                viewModel.restoreProject(project.id)
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Dialogs
    if (showSettingsDialog) {
        SettingsDialog(
            isPro = isPro,
            onTogglePro = {
                if (isPro) viewModel.monetization.downgradeToFree() else viewModel.monetization.upgradeToPro()
            },
            onDismiss = { showSettingsDialog = false },
            onClearCache = {
                showSettingsDialog = false
            }
        )
    }

    if (showProDialog) {
        WsProDialog(
            isPro = isPro,
            onDismiss = { showProDialog = false },
            onUpgrade = { viewModel.monetization.upgradeToPro() }
        )
    }

    // Rename Dialog
    projectToRename?.let { project ->
        AlertDialog(
            containerColor = WsSurfaceCardElevated,
            titleContentColor = WsTextPrimary,
            textContentColor = WsTextPrimary,
            onDismissRequest = { projectToRename = null },
            title = { Text("Rename Project", fontWeight = FontWeight.Bold, color = WsTextPrimary) },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("Project Name", color = WsTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = WsTextPrimary,
                        unfocusedTextColor = WsTextPrimary,
                        focusedBorderColor = WsElectricBlue,
                        unfocusedBorderColor = WsBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (renameText.isNotBlank()) {
                            viewModel.renameProject(project.id, renameText.trim())
                        }
                        projectToRename = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WsElectricBlue,
                        contentColor = WsTextPrimary
                    )
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToRename = null }) {
                    Text("Cancel", color = WsTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun ProjectTabPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        targetValue = if (isSelected) WsElectricBlue else WsSurfaceCard,
        label = "pillBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) WsTextPrimary else WsTextSecondary,
        label = "pillText"
    )

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = bg,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) WsElectricCyan else WsBorder
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun EditorModeTile(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color = WsElectricBlue,
    isGlow: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = WsSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isGlow) WsBorderGlow else WsBorder
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(WsButtonDark, RoundedCornerShape(10.dp))
                    .border(1.dp, WsBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(19.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = WsTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.5.sp,
                color = WsTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AiSpotlightCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(135.dp)
            .height(82.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = WsSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, WsBorderGlow),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(WsElectricBlue.copy(alpha = 0.2f), RoundedCornerShape(7.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = WsElectricCyan,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .background(WsButtonDark, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text("AI", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = WsElectricCyan)
                }
            }
            Column {
                Text(
                    text = title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = WsTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 9.5.sp,
                    color = WsTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EmptyProjectsState(tabIndex: Int, onImportMedia: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WsSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 36.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(WsButtonDark, CircleShape)
                    .border(1.dp, WsBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (tabIndex == 2) Icons.Default.DeleteOutline else Icons.Default.VideoLibrary,
                    contentDescription = null,
                    tint = WsElectricCyan,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = when (tabIndex) {
                    1 -> "No drafts found"
                    2 -> "Trash is empty"
                    else -> "No projects yet"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = WsTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when (tabIndex) {
                    1 -> "Unsaved edits automatically appear here."
                    2 -> "Deleted projects stay here for restoration."
                    else -> "Import media from your gallery to start editing."
                },
                fontSize = 12.sp,
                color = WsTextSecondary
            )
            if (tabIndex != 2) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onImportMedia,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WsElectricBlue,
                        contentColor = WsTextPrimary
                    )
                ) {
                    Icon(
                        Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Import Media",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectCard(
    project: ProjectEntity,
    isTrash: Boolean,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onRestore: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("MMM d • HH:mm", Locale.getDefault()) }
    val formattedDate = dateFormatter.format(Date(project.updatedAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("project_item_${project.id}"),
        colors = CardDefaults.cardColors(containerColor = WsSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail / Fallback
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(WsButtonDark)
                    .border(1.dp, WsBorder, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!project.thumbnailUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = project.thumbnailUri,
                        contentDescription = "Project Thumbnail",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = if (project.type == "VIDEO") Icons.Default.Movie else Icons.Default.Image,
                        contentDescription = null,
                        tint = WsElectricCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Type badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(3.dp)
                        .background(Color(0xDD090A0E), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = if (project.type == "VIDEO") project.aspectRatio else "PHOTO",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = WsElectricCyan
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Project Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = WsTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = WsTextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "•", fontSize = 11.sp, color = WsTextSecondary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${project.resolution} ${project.fps}fps",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = WsElectricCyan
                    )
                    if (project.isDraft) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Draft",
                            fontSize = 10.sp,
                            color = WsAccentGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Context Menu Button
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = WsTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(WsSurfaceCardElevated)
                ) {
                    if (isTrash) {
                        DropdownMenuItem(
                            text = { Text("Restore Project", color = WsTextPrimary) },
                            leadingIcon = { Icon(Icons.Default.Restore, contentDescription = null, tint = WsElectricCyan) },
                            onClick = {
                                onRestore()
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Permanently", color = Color(0xFFFF5252)) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFF5252)) },
                            onClick = {
                                onDelete()
                                showMenu = false
                            }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text("Rename", color = WsTextPrimary) },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = WsTextSecondary) },
                            onClick = {
                                onRename()
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Duplicate", color = WsTextPrimary) },
                            leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null, tint = WsTextSecondary) },
                            onClick = {
                                onDuplicate()
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Move to Trash", color = Color(0xFFFF5252)) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFF5252)) },
                            onClick = {
                                onDelete()
                                showMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}
