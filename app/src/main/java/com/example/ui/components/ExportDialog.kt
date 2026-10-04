package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.media.MediaExportEngine
import com.example.model.ExportConfig
import com.example.ui.theme.WsAccentGold
import com.example.ui.theme.WsAccentGreen
import com.example.ui.theme.WsBorder
import com.example.ui.theme.WsBorderGlow
import com.example.ui.theme.WsButtonDark
import com.example.ui.theme.WsElectricBlue
import com.example.ui.theme.WsElectricCyan
import com.example.ui.theme.WsMediumGray
import com.example.ui.theme.WsSurfaceCardElevated
import com.example.ui.theme.WsTextPrimary
import com.example.ui.theme.WsTextSecondary
import java.io.File

@Composable
fun ExportDialog(
    isVideo: Boolean,
    isPro: Boolean,
    isExporting: Boolean,
    exportProgress: Int,
    exportedFile: File?,
    onDismiss: () -> Unit,
    onStartExport: (ExportConfig) -> Unit,
    onWatchRewardedFor4k: (() -> Unit) -> Unit
) {
    val context = LocalContext.current
    var selectedRes by remember { mutableStateOf("1080p") }
    var selectedFps by remember { mutableStateOf(30) }
    var selectedFormat by remember { mutableStateOf(if (isVideo) "MP4" else "JPG") }

    Dialog(onDismissRequest = {
        if (!isExporting) onDismiss()
    }) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = WsSurfaceCardElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, WsBorderGlow),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (exportedFile != null) "Export Complete" else "Export Media",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = WsTextPrimary
                    )
                    if (!isExporting) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = WsTextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (exportedFile != null) {
                    // Export Success State
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(WsButtonDark, CircleShape)
                                .border(1.5.dp, WsElectricCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = WsAccentGreen,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Rendered Successfully!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = WsTextPrimary
                        )
                        Text(
                            text = "${exportedFile.name} • ${(exportedFile.length() / 1024)} KB",
                            fontSize = 12.sp,
                            color = WsTextSecondary
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Share Button
                        Button(
                            onClick = {
                                val mimeType = if (isVideo) "video/mp4" else if (selectedFormat == "PNG") "image/png" else "image/jpeg"
                                val shareIntent = MediaExportEngine.createShareIntent(context, exportedFile, mimeType)
                                context.startActivity(android.content.Intent.createChooser(shareIntent, "Share with WS"))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WsElectricBlue,
                                contentColor = WsTextPrimary
                            )
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Share to Other Apps", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WsTextPrimary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder)
                        ) {
                            Text(text = "Done", fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else if (isExporting) {
                    // Rendering In Progress State
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        CircularProgressIndicator(
                            progress = { exportProgress / 100f },
                            modifier = Modifier.size(72.dp),
                            color = WsElectricCyan,
                            strokeWidth = 6.dp,
                            trackColor = WsButtonDark
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Rendering $selectedRes at ${selectedFps} FPS...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = WsTextPrimary
                        )
                        Text(
                            text = "$exportProgress%",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = WsElectricCyan
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { exportProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = WsElectricBlue,
                            trackColor = WsButtonDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Processing video frames & audio mastering",
                            fontSize = 11.sp,
                            color = WsTextSecondary
                        )
                    }
                } else {
                    // Export Settings Options
                    if (isVideo) {
                        Text(
                            text = "RESOLUTION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = WsTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("480p", "720p", "1080p", "4K").forEach { res ->
                                val isSelected = selectedRes == res
                                val is4k = res == "4K"
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            if (is4k && !isPro) {
                                                onWatchRewardedFor4k { selectedRes = "4K" }
                                            } else {
                                                selectedRes = res
                                            }
                                        },
                                    color = if (isSelected) WsElectricBlue else WsButtonDark,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) WsElectricCyan else WsBorder
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = res,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (isSelected) WsTextPrimary else WsTextSecondary
                                            )
                                            if (is4k && !isPro) {
                                                Text(
                                                    text = "PRO / AD",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) WsAccentGold else WsAccentGold.copy(alpha = 0.8f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "FRAME RATE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = WsTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(24, 30, 60).forEach { fps ->
                                val isSelected = selectedFps == fps
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedFps = fps },
                                    color = if (isSelected) WsElectricBlue else WsButtonDark,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) WsElectricCyan else WsBorder
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$fps FPS",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) WsTextPrimary else WsTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Photo Resolution
                        Text(
                            text = "IMAGE RESOLUTION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = WsTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("720p", "1080p", "4K").forEach { res ->
                                val isSelected = selectedRes == res
                                val is4k = res == "4K"
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            if (is4k && !isPro) {
                                                onWatchRewardedFor4k { selectedRes = "4K" }
                                            } else {
                                                selectedRes = res
                                            }
                                        },
                                    color = if (isSelected) WsElectricBlue else WsButtonDark,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) WsElectricCyan else WsBorder
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = if (res == "4K") "4K Ultra" else if (res == "1080p") "1080p HD" else "720p Web",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.5.sp,
                                                color = if (isSelected) WsTextPrimary else WsTextSecondary
                                            )
                                            if (is4k && !isPro) {
                                                Text(
                                                    text = "PRO / AD",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) WsAccentGold else WsAccentGold.copy(alpha = 0.8f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Photo Format
                        Text(
                            text = "IMAGE FORMAT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = WsTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("JPG", "PNG").forEach { fmt ->
                                val isSelected = selectedFormat == fmt
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { selectedFormat = fmt },
                                    color = if (isSelected) WsElectricBlue else WsButtonDark,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) WsElectricCyan else WsBorder
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (fmt == "JPG") "JPG (High Quality)" else "PNG (Lossless)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSelected) WsTextPrimary else WsTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val w = when (selectedRes) {
                                "4K" -> 3840
                                "1080p" -> 1920
                                "720p" -> 1280
                                else -> 854
                            }
                            val h = when (selectedRes) {
                                "4K" -> 2160
                                "1080p" -> 1080
                                "720p" -> 720
                                else -> 480
                            }
                            val config = ExportConfig(
                                resolutionLabel = selectedRes,
                                width = w,
                                height = h,
                                fps = selectedFps,
                                format = selectedFormat
                            )
                            onStartExport(config)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WsElectricBlue,
                            contentColor = WsTextPrimary
                        )
                    ) {
                        Text(
                            text = if (isVideo) "Export Video ($selectedRes • ${selectedFps}fps)" else "Export $selectedFormat Photo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = WsTextPrimary
                        )
                    }
                }
            }
        }
    }
}
