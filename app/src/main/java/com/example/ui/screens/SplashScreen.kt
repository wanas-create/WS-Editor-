package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BebasNeueFamily
import com.example.ui.theme.RobotoMonoFamily
import com.example.ui.theme.TrackGreen
import com.example.ui.theme.TrackOrange
import com.example.ui.theme.WsBackground
import com.example.ui.theme.WsCyan
import com.example.ui.theme.WsPrimaryGradient
import com.example.ui.theme.WsPurple
import com.example.ui.theme.WsSurfaceElevated
import com.example.ui.theme.WsTextPrimary
import com.example.ui.theme.WsTextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1750L)
        onFinished()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val glowSweep by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glow_sweep"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WsBackground)
            .clickable { onFinished() }
            .testTag("splash_screen_root"),
        contentAlignment = Alignment.Center
    ) {
        // Ambient background glow canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(WsCyan.copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(size.width * 0.3f, size.height * 0.38f),
                    radius = size.minDimension * 0.6f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(WsPurple.copy(alpha = 0.22f), Color.Transparent),
                    center = Offset(size.width * 0.7f, size.height * 0.62f),
                    radius = size.minDimension * 0.65f
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(132.dp)
                    .scale(pulseScale)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(WsCyan, WsPurple, WsCyan)
                        ),
                        startAngle = glowSweep,
                        sweepAngle = 280f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.img_app_icon),
                    contentDescription = "WS-Editor Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(104.dp)
                        .clip(CircleShape)
                        .border(2.dp, WsPrimaryGradient, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "WS-EDITOR",
                fontFamily = BebasNeueFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                letterSpacing = 4.sp,
                color = WsTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(WsSurfaceElevated)
                    .border(1.dp, WsPrimaryGradient, RoundedCornerShape(50))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "WSEDITOR.COM • VN PRO STUDIO",
                    fontFamily = RobotoMonoFamily,
                    fontSize = 11.sp,
                    color = WsCyan,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Animated 4-track mini timeline indicator
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val trackColors = listOf(WsCyan, WsPurple, TrackOrange, TrackGreen)
                val trackWidths = listOf(140.dp, 110.dp, 95.dp, 125.dp)
                trackColors.forEachIndexed { index, color ->
                    Box(
                        modifier = Modifier
                            .width(trackWidths[index])
                            .height(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(color.copy(alpha = 0.85f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tap anywhere to enter studio",
                style = MaterialTheme.typography.bodyMedium,
                color = WsTextSecondary
            )
        }
    }
}
