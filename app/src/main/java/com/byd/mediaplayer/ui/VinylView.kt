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

/**
 * 黑胶唱片视图组件
 * 显示旋转的黑胶唱片动画，点击可切换到歌词视图
 *
 * @param song 当前播放的歌曲（用于显示歌曲信息）
 * @param isPlaying 是否正在播放（播放时唱片旋转，暂停时停止）
 * @param modifier 修饰符
 * @param onClick 点击事件（切换到歌词视图）
 */
@Composable
fun VinylView(
    song: Song?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // 旋转动画：持续10秒完成一圈
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),  // 线性旋转，10秒一圈
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // 播放时使用旋转角度，暂停时保持当前角度
    val currentRotation = if (isPlaying) rotation else 0f

    Box(
        modifier = modifier
            .size(280.dp)
            .clip(CircleShape)                    // 裁剪为圆形
            .background(Color(0xFF1A1A2E))        // 深色背景
            .clickable(onClick = onClick),         // 点击切换到歌词
        contentAlignment = Alignment.Center
    ) {
        // 外圈（唱片主体）
        Box(
            modifier = Modifier
                .size(260.dp)
                .rotate(currentRotation)           // 旋转动画
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        // 径向渐变：中心亮，边缘暗
                        colors = listOf(
                            Color(0xFF2A2A4E),  // 中心
                            Color(0xFF1A1A2E),  // 中间
                            Color(0xFF0D0D1A)   // 边缘
                        )
                    )
                )
                .border(8.dp, Color(0xFF3A3A5E), CircleShape)  // 外圈边框
        ) {
            // 唱片纹路（与唱片反向旋转产生视觉效果）
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(-currentRotation)
            ) {
                // 绘制8条放射状纹路
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

        // 中心标签区域
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00D4AA),  // 主题色
                            Color(0xFF00A088)   // 深绿
                        )
                    )
                )
                .border(4.dp, Color.White.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 音符图标
                Text(
                    text = "♪",
                    color = Color.White,
                    modifier = Modifier.size(40.dp)
                )
                // 显示歌曲标题（最多6个字符）
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

// 扩展属性：Int转换为sp
private val Int.sp get() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)