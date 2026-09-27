package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FaceRetouchingNatural
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CustomFontEntity
import com.example.data.VideoProjectEntity
import com.example.ui.theme.RobotoMonoFamily
import com.example.ui.theme.TrackGreen
import com.example.ui.theme.TrackOrange
import com.example.ui.theme.WsBackground
import com.example.ui.theme.WsBorder
import com.example.ui.theme.WsCardGradient
import com.example.ui.theme.WsCyan
import com.example.ui.theme.WsDanger
import com.example.ui.theme.WsPrimaryGradient
import com.example.ui.theme.WsPurple
import com.example.ui.theme.WsSurface
import com.example.ui.theme.WsSurfaceElevated
import com.example.ui.theme.WsTextPrimary
import com.example.ui.theme.WsTextSecondary

data class QuickToolItem(
    val name: String,
    val icon: ImageVector,
    val accent: Color
)

@Composable
fun HomeScreen(
    projects: List<VideoProjectEntity>,
    customFonts: List<CustomFontEntity>,
    isProUnlocked: Boolean = false,
    activeProPlanSummary: String? = null,
    onOpenProPaywall: () -> Unit = {},
    onCreateNewProject: () -> Unit,
    onPickVideoFromGallery: () -> Unit,
    onQuickToolClick: (String) -> Unit,
    onOpenProject: (VideoProjectEntity) -> Unit,
    onDeleteProject: (Int) -> Unit,
    onUploadCustomFontClick: () -> Unit
) {
    val quickTools = listOf(
        QuickToolItem("Trim", Icons.Default.ContentCut, WsCyan),
        QuickToolItem("Filter", Icons.Default.FilterVintage, WsPurple),
        QuickToolItem("Text", Icons.Default.Title, WsCyan),
        QuickToolItem("Music", Icons.Default.MusicNote, TrackGreen),
        QuickToolItem("Speed", Icons.Default.Speed, TrackOrange),
        QuickToolItem("Effect", Icons.Default.Flare, WsPurple),
        QuickToolItem("Beauty", Icons.Default.FaceRetouchingNatural, Color(0xFFFF4D94)),
        QuickToolItem("AI", Icons.Default.AutoAwesome, WsCyan)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WsBackground)
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Hi Master Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon),
                        contentDescription = "WS-Editor Studio Icon",
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, WsPrimaryGradient, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Hi Master",
                                style = MaterialTheme.typography.headlineMedium,
                                color = WsTextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(WsPrimaryGradient)
                                    .clickable { onOpenProPaywall() }
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                                    .testTag("home_pro_badge_button")
                            ) {
                                Text(
                                    text = if (isProUnlocked) "PRO ACTIVE" else "GET PRO",
                                    fontFamily = RobotoMonoFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color.Black
                                )
                            }
                        }
                        Text(
                            text = activeProPlanSummary ?: "wseditor.com • Pro from $1.99/mo (~Rs 100 PK)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WsTextSecondary
                        )
                    }
                }

                // Quick Custom Font Upload Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(WsSurfaceElevated)
                        .border(1.dp, WsCyan.copy(alpha = 0.5f), RoundedCornerShape(50))
                        .clickable { onUploadCustomFontClick() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("home_upload_font_chip")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FontDownload,
                            contentDescription = "Upload Custom Font",
                            tint = WsCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "+Font (${customFonts.size})",
                            fontFamily = RobotoMonoFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WsCyan
                        )
                    }
                }
            }
        }

        // Pro Pricing Banner Card ($1.99/mo • $9.99/yr • $19.99 Lifetime • PK Rs 100/Rs 1100)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(WsSurface)
                    .border(1.dp, WsPrimaryGradient, RoundedCornerShape(16.dp))
                    .clickable { onOpenProPaywall() }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .testTag("home_pro_pricing_banner"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isProUnlocked) "WS-Editor Pro Unlocked" else "Upgrade to WS-Editor Pro",
                        style = MaterialTheme.typography.titleMedium,
                        color = WsCyan
                    )
                    Text(
                        text = activeProPlanSummary
                            ?: "$1.99/mo (~Rs 100 PK) • $9.99/yr Best Value • $19.99 Lifetime",
                        fontFamily = RobotoMonoFamily,
                        fontSize = 10.sp,
                        color = WsTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(WsPrimaryGradient)
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = if (isProUnlocked) "MANAGE" else "VIEW PLANS",
                        fontFamily = RobotoMonoFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // 2. Create New Project Big Gradient Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(WsCardGradient)
                    .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(22.dp))
                    .clickable { onCreateNewProject() }
                    .testTag("create_new_project_card")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_sample_cyberpunk_video),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = 0.25f,
                    modifier = Modifier.matchParentSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.45f))
                                .border(1.dp, WsCyan, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create New Project",
                                tint = WsCyan,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.Black.copy(alpha = 0.55f))
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "4 TRACKS • 4K 60FPS",
                                fontFamily = RobotoMonoFamily,
                                fontSize = 11.sp,
                                color = WsCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Create New Project",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White
                        )
                        Text(
                            text = "Multi-layer VN timeline, custom .TTF/.OTF fonts, speed ramps & 4K export",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.88f)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onCreateNewProject,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WsCyan,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_new_studio_project")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "New Timeline",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onPickVideoFromGallery,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black.copy(alpha = 0.65f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .testTag("btn_pick_gallery_video_home")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = WsCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pick Video",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // 3. Quick Tools Grid (Trim, Filter, Text, Music, Speed, Effect, Beauty, AI)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Tools",
                        style = MaterialTheme.typography.titleLarge,
                        color = WsTextPrimary
                    )
                    Text(
                        text = "VN Pro Shortcuts",
                        style = MaterialTheme.typography.labelSmall,
                        color = WsCyan
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    quickTools.chunked(4).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowItems.forEach { item ->
                                QuickToolCard(
                                    item = item,
                                    onClick = { onQuickToolClick(item.name) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. My Projects Header + List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Projects (${projects.size})",
                    style = MaterialTheme.typography.titleLarge,
                    color = WsTextPrimary
                )
                Text(
                    text = "Tap to edit in VN Timeline",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WsTextSecondary
                )
            }
        }

        items(projects, key = { it.id }) { project ->
            ProjectRowCard(
                project = project,
                onOpen = { onOpenProject(project) },
                onDelete = { onDeleteProject(project.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuickToolCard(
    item: QuickToolItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(WsSurface)
            .border(1.dp, WsBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 6.dp)
            .testTag("quick_tool_${item.name.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(item.accent.copy(alpha = 0.16f))
                .border(1.dp, item.accent.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.name,
                tint = item.accent,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = item.name,
            style = MaterialTheme.typography.labelLarge,
            color = WsTextPrimary,
            maxLines = 1
        )
    }
}

@Composable
private fun ProjectRowCard(
    project: VideoProjectEntity,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WsSurface)
            .border(1.dp, WsBorder, RoundedCornerShape(16.dp))
            .clickable { onOpen() }
            .padding(12.dp)
            .testTag("project_card_${project.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 88.dp, height = 62.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, WsCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_sample_cyberpunk_video),
                contentDescription = project.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(WsCyan.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Open Project",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = project.title,
                style = MaterialTheme.typography.titleMedium,
                color = WsTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(WsCyan.copy(alpha = 0.16f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = project.resolution,
                        fontFamily = RobotoMonoFamily,
                        fontSize = 10.sp,
                        color = WsCyan,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${String.format("%.1fs", project.durationSec)} • ${project.aspectRatio} • ${project.clipsCount} clips",
                    style = MaterialTheme.typography.labelSmall,
                    color = WsTextSecondary
                )
            }

            Text(
                text = "Audio: ${project.musicTrackName}",
                style = MaterialTheme.typography.bodyMedium,
                color = WsTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 11.sp
            )
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.testTag("delete_project_${project.id}")
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Delete Project",
                tint = WsDanger
            )
        }
    }
}
