package com.byd.mediaplayer.ui

import android.media.AudioManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VolumeControl(
    currentVolume: Float,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    var isMuted by remember { mutableStateOf(false) }
    var previousVolume by remember { mutableFloatStateOf(currentVolume) }

    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()

    Row(
        modifier = modifier
            .background(Color(0xFF2A2A4E), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 静音按钮
        Text(
            text = if (isMuted) "🔇" else "🔊",
            fontSize = 24.sp,
            modifier = Modifier
                .padding(end = 8.dp)
        )

        Slider(
            value = if (isMuted) 0f else currentVolume,
            onValueChange = {
                isMuted = false
                onVolumeChange(it)
            },
            valueRange = 0f..maxVolume,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF00D4AA),
                activeTrackColor = Color(0xFF00D4AA),
                inactiveTrackColor = Color.Gray
            )
        )

        // 音量百分比
        Text(
            text = "${((currentVolume / maxVolume) * 100).toInt()}%",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.width(40.dp)
        )
    }

    // 监听静音切换
    LaunchedEffect(isMuted) {
        if (isMuted) {
            previousVolume = currentVolume
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                0,
                0
            )
        } else if (previousVolume > 0) {
            val volume = (previousVolume * maxVolume).toInt()
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
            onVolumeChange(previousVolume)
        }
    }
}

@Composable
fun VolumeControlButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = "🔊",
            fontSize = 28.sp,
            color = Color.White
        )
    }
}