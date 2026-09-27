package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RobotoMonoFamily
import com.example.ui.theme.TrackGreen
import com.example.ui.theme.TrackOrange
import com.example.ui.theme.WsBackground
import com.example.ui.theme.WsBorder
import com.example.ui.theme.WsCardGradient
import com.example.ui.theme.WsCyan
import com.example.ui.theme.WsPrimaryGradient
import com.example.ui.theme.WsPurple
import com.example.ui.theme.WsSurface
import com.example.ui.theme.WsSurfaceElevated
import com.example.ui.theme.WsTextPrimary
import com.example.ui.theme.WsTextSecondary

data class WanasAiFeatureItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val icon: ImageVector,
    val accentColor: Color,
    val demoActionLabel: String,
    val sampleCaptionText: String
)

@Composable
fun WanasAiScreen(
    onSendCaptionToTimeline: (String) -> Unit,
    onOpenPhotoBgRemover: () -> Unit
) {
    var previewStatus by remember { mutableStateOf<String?>(null) }

    val aiFeatures = listOf(
        WanasAiFeatureItem(
            id = "text_to_video",
            title = "Text to Video",
            subtitle = "Generate 4K cinematic B-roll clips from natural language prompts with Wanas Gen-3 Engine.",
            badgeText = "COMING SOON",
            icon = Icons.Default.MovieCreation,
            accentColor = WsCyan,
            demoActionLabel = "Load AI Prompt Template on Timeline",
            sampleCaptionText = "AI SCENE: NEO-TOKYO 4K"
        ),
        WanasAiFeatureItem(
            id = "auto_captions",
            title = "Auto Captions",
            subtitle = "Word-by-word dynamic subtitles synced to voice tracks with custom .TTF/.OTF font support.",
            badgeText = "COMING SOON",
            icon = Icons.Default.ClosedCaption,
            accentColor = WsPurple,
            demoActionLabel = "Generate Sample Caption to Video",
            sampleCaptionText = "AUTO CAPTION • WSEDITOR PRO"
        ),
        WanasAiFeatureItem(
            id = "ai_voice",
            title = "AI Voice",
            subtitle = "Ultra-realistic studio voiceovers, dubbing & neural vocal cloning in 28+ languages.",
            badgeText = "COMING SOON",
            icon = Icons.Default.RecordVoiceOver,
            accentColor = TrackOrange,
            demoActionLabel = "Add AI Voiceover Tag to Video",
            sampleCaptionText = "🎙 WANAS AI VOICEOVER"
        ),
        WanasAiFeatureItem(
            id = "bg_remover",
            title = "Background Remover",
            subtitle = "Zero-greenscreen 60fps video & photo subject isolation with neon rim lighting.",
            badgeText = "COMING SOON",
            icon = Icons.Default.AutoFixHigh,
            accentColor = TrackGreen,
            demoActionLabel = "Try Photo BG Remover Now",
            sampleCaptionText = "AI CUTOUT ACTIVE"
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WsBackground)
            .testTag("wanas_ai_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(WsCardGradient)
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .border(1.5.dp, WsCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Wanas AI",
                                tint = WsCyan,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Wanas AI Lab",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Color.White
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(Color.Black.copy(alpha = 0.65f))
                                        .border(1.dp, WsCyan, RoundedCornerShape(50))
                                        .padding(horizontal = 10.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "COMING SOON",
                                        fontFamily = RobotoMonoFamily,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WsCyan
                                    )
                                }
                            }
                            Text(
                                text = "wseditor.com • Next-Gen Neural Video Suite",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Text(
                        text = "Cloud neural rendering for Text to Video, Auto Captions, AI Voice & Video Background Removal is coming soon. Try the interactive timeline integration previews below!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        previewStatus?.let { msg ->
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(WsCyan.copy(alpha = 0.16f))
                        .border(1.dp, WsCyan, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "✨ $msg",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WsCyan
                    )
                }
            }
        }

        // 4 Requested AI Cards with AI Icon & COMING SOON Badge
        items(aiFeatures, key = { it.id }) { feature ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(WsSurface)
                    .border(1.dp, WsBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp)
                    .testTag("wanas_ai_card_${feature.id}"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(feature.accentColor.copy(alpha = 0.16f))
                                .border(1.dp, feature.accentColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = feature.icon,
                                contentDescription = feature.title,
                                tint = feature.accentColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = feature.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = WsTextPrimary
                            )
                            Text(
                                text = "Wanas Neural Engine v2",
                                fontFamily = RobotoMonoFamily,
                                fontSize = 10.sp,
                                color = feature.accentColor
                            )
                        }
                    }

                    // Coming Soon Badge with AI Icon
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(WsSurfaceElevated)
                            .border(1.dp, WsPrimaryGradient, RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Icon",
                            tint = WsCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = feature.badgeText,
                            fontFamily = RobotoMonoFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WsCyan
                        )
                    }
                }

                Text(
                    text = feature.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WsTextSecondary
                )

                Button(
                    onClick = {
                        if (feature.id == "bg_remover") {
                            onOpenPhotoBgRemover()
                        } else {
                            previewStatus = "Added '${feature.sampleCaptionText}' layer to Video Editor!"
                            onSendCaptionToTimeline(feature.sampleCaptionText)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WsSurfaceElevated,
                        contentColor = WsCyan
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, feature.accentColor.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                        .testTag("btn_ai_preview_${feature.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = feature.accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = feature.demoActionLabel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = WsTextPrimary
                    )
                }
            }
        }
    }
}
