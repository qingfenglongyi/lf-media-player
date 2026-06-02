package com.byd.mediaplayer.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.byd.mediaplayer.model.Song

@Composable
fun VinylView(
    song: Song?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // 旋转动画
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val currentRotation = if (isPlaying) rotation else 0f

    Box(
        modifier = modifier
            .size(280.dp)
            .clip(CircleShape)
            .background(Color(0xFF1A1A2E))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // 外圈
        Box(
            modifier = Modifier
                .size(260.dp)
                .rotate(currentRotation)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF2A2A4E),
                            Color(0xFF1A1A2E),
                            Color(0xFF0D0D1A)
                        )
                    )
                )
                .border(8.dp, Color(0xFF3A3A5E), CircleShape)
        ) {
            // 唱片纹路
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(-currentRotation)
            ) {
                repeat(8) { i ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF2A2A4E).copy(alpha = 0.5f))
                            .align(Alignment.Center)
                    )
                }
            }
        }

        // 中心标签
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00D4AA),
                            Color(0xFF00A088)
                        )
                    )
                )
                .border(4.dp, Color.White.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "♪",
                    color = Color.White,
                    modifier = Modifier.size(40.dp)
                )
                if (song != null) {
                    Text(
                        text = song.title.take(6),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private val Int.sp get() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)