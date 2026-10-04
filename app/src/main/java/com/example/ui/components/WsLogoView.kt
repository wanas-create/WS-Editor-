package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WsElectricBlue
import com.example.ui.theme.WsElectricCyan
import com.example.ui.theme.WsMediumGray
import com.example.ui.theme.WsTextPrimary

/**
 * Custom-drawn Interlocked WS Monogram.
 * The right spine of 'W' merges fluidly into the top curve of 'S'.
 */
@Composable
fun WsInterlockedLogo(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    color: Color = WsElectricCyan,
    strokeWidthDp: Dp = 3.5.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokePx = strokeWidthDp.toPx()
        val capStroke = Stroke(
            width = strokePx,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        // Draw 'W'
        val pathW = Path().apply {
            moveTo(w * 0.12f, h * 0.22f)
            lineTo(w * 0.25f, h * 0.78f)
            lineTo(w * 0.38f, h * 0.38f)
            lineTo(w * 0.50f, h * 0.78f)
            // Interlock seamlessly into 'S'
            cubicTo(
                w * 0.54f, h * 0.65f,
                w * 0.58f, h * 0.30f,
                w * 0.70f, h * 0.25f
            )
        }
        drawPath(pathW, color = color, style = capStroke)

        // Draw 'S' merging with the W hook
        val pathS = Path().apply {
            moveTo(w * 0.86f, h * 0.32f)
            cubicTo(
                w * 0.82f, h * 0.22f,
                w * 0.68f, h * 0.22f,
                w * 0.62f, h * 0.32f
            )
            cubicTo(
                w * 0.54f, h * 0.44f,
                w * 0.88f, h * 0.54f,
                w * 0.82f, h * 0.70f
            )
            cubicTo(
                w * 0.76f, h * 0.82f,
                w * 0.58f, h * 0.82f,
                w * 0.50f, h * 0.72f
            )
        }
        drawPath(pathS, color = color, style = capStroke)

        // Subtle accent dot connecting the geometric harmony
        drawCircle(
            color = color,
            radius = strokePx * 0.6f,
            center = Offset(w * 0.86f, h * 0.32f)
        )
    }
}

/**
 * Full WS Brand Header with interlocked logo, title and "POWERED BY WS SERIES"
 */
@Composable
fun WsBrandHeader(
    modifier: Modifier = Modifier,
    isLarge: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            WsInterlockedLogo(
                size = if (isLarge) 54.dp else 36.dp,
                strokeWidthDp = if (isLarge) 4.5.dp else 3.2.dp,
                color = WsElectricCyan
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "WS",
                fontSize = if (isLarge) 32.sp else 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = WsTextPrimary,
                fontFamily = FontFamily.SansSerif
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "POWERED BY WS SERIES",
            fontSize = if (isLarge) 11.sp else 9.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.8.sp,
            color = WsMediumGray
        )
    }
}
