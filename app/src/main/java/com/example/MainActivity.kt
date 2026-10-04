package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AiToolsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PhotoEditorScreen
import com.example.ui.screens.VideoEditorScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.WsBackground
import com.example.viewmodel.ScreenState
import com.example.viewmodel.WsEditorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = WsBackground
                ) {
                    val editorViewModel: WsEditorViewModel = viewModel()
                    MainAppNavHost(viewModel = editorViewModel)
                }
            }
        }
    }
}

@Composable
fun MainAppNavHost(viewModel: WsEditorViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            if (targetState == ScreenState.HOME) {
                (slideInHorizontally { -it } + fadeIn()) togetherWith (slideOutHorizontally { it } + fadeOut())
            } else {
                (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it } + fadeOut())
            }
        },
        label = "ScreenTransition"
    ) { screen ->
        when (screen) {
            ScreenState.HOME -> HomeScreen(viewModel = viewModel)
            ScreenState.VIDEO_EDITOR -> VideoEditorScreen(viewModel = viewModel)
            ScreenState.PHOTO_EDITOR -> PhotoEditorScreen(viewModel = viewModel)
            ScreenState.AI_TOOLS -> AiToolsScreen(viewModel = viewModel)
            ScreenState.SETTINGS -> HomeScreen(viewModel = viewModel)
        }
    }
}
