package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.MainTab
import com.example.data.PhotoTool
import com.example.data.VideoTool
import com.example.ui.WsEditorViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PhotoEditorScreen
import com.example.ui.screens.ProPaywallScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VideoEditorScreen
import com.example.ui.screens.WanasAiScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.WsBackground
import com.example.ui.theme.WsCyan
import com.example.ui.theme.WsSurface
import com.example.ui.theme.WsTextPrimary
import com.example.ui.theme.WsTextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                WsEditorApp()
            }
        }
    }
}

@Composable
fun WsEditorApp(
    viewModel: WsEditorViewModel = viewModel(
        factory = WsEditorViewModel.provideFactory(LocalContext.current)
    )
) {
    val showSplash by viewModel.showSplash.collectAsStateWithLifecycle()
    val showProPaywall by viewModel.showProPaywall.collectAsStateWithLifecycle()
    val isProUnlocked by viewModel.isProUnlocked.collectAsStateWithLifecycle()
    val activeProPlanSummary by viewModel.activeProPlanSummary.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val customFonts by viewModel.customFonts.collectAsStateWithLifecycle()
    val videoState by viewModel.videoState.collectAsStateWithLifecycle()
    val photoState by viewModel.photoState.collectAsStateWithLifecycle()

    // Zero-permission Android Photo Picker for Videos
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.onVideoPickedFromGallery(uri)
        }
    }

    // Zero-permission Android Photo Picker for Photos
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.onPhotoPickedFromGallery(uri)
        }
    }

    // Device Document Picker for Custom Font Uploads (.ttf / .otf)
    val customFontPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.uploadCustomFontFromDeviceUri(uri)
            viewModel.selectTab(MainTab.VIDEO)
            viewModel.selectVideoTool(VideoTool.TEXT)
        }
    }

    val launchFontPicker: () -> Unit = {
        customFontPickerLauncher.launch(
            arrayOf(
                "font/ttf",
                "font/otf",
                "application/x-font-ttf",
                "application/x-font-otf",
                "application/font-sfnt",
                "application/octet-stream",
                "*/*"
            )
        )
    }

    if (showSplash) {
        SplashScreen(onFinished = { viewModel.finishSplash() })
    } else if (showProPaywall) {
        BackHandler {
            viewModel.closeProPaywall()
        }
        ProPaywallScreen(
            isProUnlocked = isProUnlocked,
            activePlanSummary = activeProPlanSummary,
            onUnlockPlan = { plan, countryCode ->
                viewModel.unlockProPlan(
                    planTitle = plan.title,
                    priceText = "${plan.primaryPriceText} ${plan.periodText}",
                    countryCode = countryCode
                )
            },
            onClose = { viewModel.closeProPaywall() }
        )
    } else {
        if (currentTab != MainTab.HOME) {
            BackHandler {
                viewModel.selectTab(MainTab.HOME)
            }
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(WsBackground),
            containerColor = WsBackground,
            bottomBar = {
                NavigationBar(
                    containerColor = WsSurface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    val tabs = listOf(
                        Triple(MainTab.HOME, "Home", Pair(Icons.Filled.Home, Icons.Outlined.Home)),
                        Triple(MainTab.VIDEO, "Video", Pair(Icons.Filled.Videocam, Icons.Outlined.Videocam)),
                        Triple(MainTab.PHOTO, "Photo", Pair(Icons.Filled.PhotoCamera, Icons.Outlined.PhotoCamera)),
                        Triple(MainTab.WANAS_AI, "Wanas AI", Pair(Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome))
                    )

                    tabs.forEach { (tab, label, icons) ->
                        val selected = currentTab == tab
                        NavigationBarItem(
                            selected = selected,
                            onClick = { viewModel.selectTab(tab) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) icons.first else icons.second,
                                    contentDescription = label
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = WsCyan,
                                indicatorColor = WsCyan,
                                unselectedIconColor = WsTextSecondary,
                                unselectedTextColor = WsTextSecondary
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(WsBackground)
            ) {
                Crossfade(targetState = currentTab, label = "main_tab_crossfade") { tab ->
                    when (tab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                projects = projects,
                                customFonts = customFonts,
                                isProUnlocked = isProUnlocked,
                                activeProPlanSummary = activeProPlanSummary,
                                onOpenProPaywall = { viewModel.openProPaywall() },
                                onCreateNewProject = { viewModel.createNewProject() },
                                onPickVideoFromGallery = {
                                    videoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                },
                                onQuickToolClick = { toolName -> viewModel.openQuickTool(toolName) },
                                onOpenProject = { project -> viewModel.openExistingProject(project) },
                                onDeleteProject = { id -> viewModel.deleteProject(id) },
                                onUploadCustomFontClick = {
                                    viewModel.selectTab(MainTab.VIDEO)
                                    viewModel.selectVideoTool(VideoTool.TEXT)
                                    launchFontPicker()
                                }
                            )
                        }

                        MainTab.VIDEO -> {
                            VideoEditorScreen(
                                state = videoState,
                                customFonts = customFonts,
                                filterPresets = viewModel.filterPresets,
                                speedPresets = viewModel.speedPresets,
                                musicLibrary = viewModel.musicLibrary,
                                fxLibrary = viewModel.fxLibrary,
                                onPickVideoFromGallery = {
                                    videoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                },
                                onUploadCustomFontFromDevice = launchFontPicker,
                                onImportSampleCustomFont = { assetName, display ->
                                    viewModel.importSampleCustomFont(assetName, display)
                                },
                                onDeleteCustomFont = { font -> viewModel.deleteCustomFont(font) },
                                onTogglePlayback = { viewModel.togglePlayback() },
                                onSeek = { sec -> viewModel.seekPlayhead(sec) },
                                onCycleAspectRatio = { viewModel.cycleAspectRatio() },
                                onSelectTool = { tool -> viewModel.selectVideoTool(tool) },
                                onSelectClip = { trackIdx, clipId ->
                                    viewModel.selectTimelineClip(trackIdx, clipId)
                                },
                                onSplitAtPlayhead = { viewModel.splitClipAtPlayhead() },
                                onTrimSelected = { dStart, dEnd ->
                                    viewModel.trimSelectedClip(dStart, dEnd)
                                },
                                onDeleteSelected = { viewModel.deleteSelectedClip() },
                                onSetSpeed = { spd -> viewModel.setPlaybackSpeed(spd) },
                                onSetVolume = { vol -> viewModel.setMasterVolume(vol) },
                                onSetFilter = { fid -> viewModel.setVideoFilter(fid) },
                                onAddTextOverlay = { viewModel.addTextOverlay() },
                                onUpdateTextOverlay = { txt, col, sz, glow, box, ox, oy ->
                                    viewModel.updateSelectedTextOverlay(txt, col, sz, glow, box, ox, oy)
                                },
                                onSelectBuiltInFont = { fid, name ->
                                    viewModel.applyBuiltInFontToSelectedText(fid, name)
                                },
                                onSelectCustomFont = { customFont ->
                                    viewModel.applyCustomFontToSelectedText(customFont)
                                },
                                onAddStickerOrFx = { label, badge, isFx ->
                                    viewModel.addStickerOrFx(label, badge, isFx)
                                },
                                onSelectMusicTrack = { title, artist, dur ->
                                    viewModel.selectMusicTrack(title, artist, dur)
                                },
                                onShowExportDialog = { show -> viewModel.showExportDialog(show) },
                                onStartExport4K = { res -> viewModel.startExport4K(res) }
                            )
                        }

                        MainTab.PHOTO -> {
                            PhotoEditorScreen(
                                state = photoState,
                                filterPresets = viewModel.filterPresets,
                                onPickPhotoFromGallery = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                onSelectTool = { tool -> viewModel.selectPhotoTool(tool) },
                                onSelectFilter = { fid -> viewModel.setPhotoFilter(fid) },
                                onUpdateBeauty = { sm, rim, sh ->
                                    viewModel.updatePhotoBeauty(sm, rim, sh)
                                },
                                onToggleBgRemove = { en, mode ->
                                    viewModel.togglePhotoBgRemove(en, mode)
                                },
                                onUpdateAdjustments = { b, c, s, w ->
                                    viewModel.updatePhotoAdjustments(b, c, s, w)
                                },
                                onReset = { viewModel.resetPhotoEdits() },
                                onSavePhoto = { viewModel.saveEditedPhoto() }
                            )
                        }

                        MainTab.WANAS_AI -> {
                            WanasAiScreen(
                                onSendCaptionToTimeline = { caption ->
                                    viewModel.addTextOverlay(caption)
                                    viewModel.selectTab(MainTab.VIDEO)
                                },
                                onOpenPhotoBgRemover = {
                                    viewModel.togglePhotoBgRemove(true, "Neon Cyber Grid")
                                    viewModel.selectPhotoTool(PhotoTool.BG_REMOVE)
                                    viewModel.selectTab(MainTab.PHOTO)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
