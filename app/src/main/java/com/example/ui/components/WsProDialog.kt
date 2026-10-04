package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.WsAccentGold
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

/**
 * AdMob-Ready Banner Slot.
 * Clean, non-intrusive container that integrates standard AdMob Banner.
 */
@Composable
fun AdMobBannerPlaceholder(
    modifier: Modifier = Modifier,
    adUnitId: String = "ca-app-pub-3940256099942544/6300978111"
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        color = WsSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(WsButtonDark, RoundedCornerShape(4.dp))
                        .border(1.dp, WsBorder, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AD",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = WsTextSecondary
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "WS Series Performance Engine",
                    fontSize = 12.sp,
                    color = WsTextPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "AdMob Ready",
                fontSize = 11.sp,
                color = WsElectricBlue,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * WS Pro Subscription Upgrade Dialog.
 */
@Composable
fun WsProDialog(
    isPro: Boolean,
    onDismiss: () -> Unit,
    onUpgrade: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = WsSurfaceCardElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, WsBorderGlow),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WsInterlockedLogo(size = 28.dp, strokeWidthDp = 2.8.dp, color = WsElectricCyan)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "WS PRO",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = WsTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(WsAccentGold.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .border(1.dp, WsAccentGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "VIP",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = WsAccentGold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = WsTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Unlock the Full Creative AI Studio",
                    fontSize = 14.sp,
                    color = WsTextSecondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                ProFeatureItem(
                    icon = Icons.Default.HighQuality,
                    title = "Ultra HD 4K & 60 FPS Export",
                    subtitle = "Master-quality exports with zero bitrate limits"
                )
                ProFeatureItem(
                    icon = Icons.Default.AutoAwesome,
                    title = "Unlimited AI Tools",
                    subtitle = "Auto captions, BG removal & neural image generator"
                )
                ProFeatureItem(
                    icon = Icons.Default.Speed,
                    title = "Zero Ad Interruptions",
                    subtitle = "Completely ad-free workflow & priority rendering"
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onUpgrade()
                        onDismiss()
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
                    Text(
                        text = if (isPro) "Active WS Pro Member" else "Upgrade to WS Pro • $4.99/mo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Cancel anytime. POWERED BY WS SERIES",
                    fontSize = 11.sp,
                    color = WsMediumGray
                )
            }
        }
    }
}

@Composable
private fun ProFeatureItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(WsButtonDark, RoundedCornerShape(10.dp))
                .border(1.dp, WsBorder, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = WsElectricCyan,
                modifier = Modifier.size(19.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = WsTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = WsTextSecondary
            )
        }
    }
}
