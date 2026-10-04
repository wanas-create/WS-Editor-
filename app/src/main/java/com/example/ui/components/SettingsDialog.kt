package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.WsAccentGold
import com.example.ui.theme.WsBorder
import com.example.ui.theme.WsButtonDark
import com.example.ui.theme.WsElectricBlue
import com.example.ui.theme.WsElectricCyan
import com.example.ui.theme.WsMediumGray
import com.example.ui.theme.WsSurfaceCardElevated
import com.example.ui.theme.WsTextPrimary
import com.example.ui.theme.WsTextSecondary

@Composable
fun SettingsDialog(
    isPro: Boolean,
    onTogglePro: () -> Unit,
    onDismiss: () -> Unit,
    onClearCache: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = WsSurfaceCardElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WS Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = WsTextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = WsTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // WS Pro Membership Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = WsAccentGold)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "WS Pro Status",
                                fontWeight = FontWeight.SemiBold,
                                color = WsTextPrimary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (isPro) "Unlocked (All Features)" else "Free Tier (AdMob Supported)",
                                fontSize = 12.sp,
                                color = WsTextSecondary
                            )
                        }
                    }
                    Switch(
                        checked = isPro,
                        onCheckedChange = { onTogglePro() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = WsTextPrimary,
                            checkedTrackColor = WsElectricBlue,
                            uncheckedThumbColor = WsMediumGray,
                            uncheckedTrackColor = WsButtonDark
                        )
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = WsBorder)

                // AdMob Integration Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = WsElectricCyan)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Monetization Engine",
                            fontWeight = FontWeight.SemiBold,
                            color = WsTextPrimary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Google AdMob SDK Ready • Safe Non-disruptive",
                            fontSize = 12.sp,
                            color = WsTextSecondary
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = WsBorder)

                // Clear Cache
                OutlinedButton(
                    onClick = onClearCache,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WsTextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WsBorder)
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp), tint = WsTextSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Clear Render Cache & Exports", fontSize = 13.sp, color = WsTextPrimary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Build & Version Info
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "WS Version 1.0.0",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WsTextPrimary
                    )
                    Text(
                        text = "POWERED BY WS SERIES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = WsMediumGray
                    )
                }
            }
        }
    }
}
