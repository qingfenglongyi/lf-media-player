package com.byd.mediaplayer.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.byd.mediaplayer.MainActivity
import com.byd.mediaplayer.R
import com.byd.mediaplayer.util.Logger

class PlayerService : Service() {

    private lateinit var playerManager: PlayerManager

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }

    override fun onCreate() {
        super.onCreate()
        Logger.d(TAG, "PlayerService onCreate")
        playerManager = PlayerManager(this)
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        Logger.d(TAG, "PlayerService onBind")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                Logger.i(TAG, "收到播放指令")
                playerManager.play()
            }
            ACTION_PAUSE -> {
                Logger.i(TAG, "收到暂停指令")
                playerManager.pause()
            }
            ACTION_NEXT -> {
                Logger.i(TAG, "收到下一曲指令")
                playerManager.playNext()
            }
            ACTION_PREVIOUS -> {
                Logger.i(TAG, "收到上一曲指令")
                playerManager.playPrevious()
            }
        }
        return START_NOT_STICKY
    }

    fun getPlayerManager(): PlayerManager = playerManager

    fun startForegroundService() {
        Logger.d(TAG, "启动前台服务")
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    fun updateNotification(song: com.byd.mediaplayer.model.Song, isPlaying: Boolean) {
        Logger.d(TAG, "更新通知: ${song.title}, isPlaying: $isPlaying")
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, createNotification(song, isPlaying))
    }

    private fun createNotificationChannel() {
        Logger.d(TAG, "创建通知通道")
        val channel = NotificationChannel(
            CHANNEL_ID,
            "音乐播放",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "音乐播放通知"
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(
        song: com.byd.mediaplayer.model.Song? = null,
        isPlaying: Boolean = false
    ): Notification {
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause,
                "暂停",
                createPendingIntent(ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                "播放",
                createPendingIntent(ACTION_PLAY)
            )
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(song?.title ?: "未播放")
            .setContentText(song?.artist ?: "比亚迪音乐播放器")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(contentIntent)
            .addAction(android.R.drawable.ic_media_previous, "上一曲", createPendingIntent(ACTION_PREVIOUS))
            .addAction(playPauseAction)
            .addAction(android.R.drawable.ic_media_next, "下一曲", createPendingIntent(ACTION_NEXT))
            .setStyle(
                MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setOngoing(isPlaying)
            .build()
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, PlayerService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onDestroy() {
        Logger.d(TAG, "PlayerService onDestroy")
        playerManager.release()
        super.onDestroy()
    }

    companion object {
        const val TAG = "PlayerService"
        const val CHANNEL_ID = "lf_media_player_channel"
        const val NOTIFICATION_ID = 1

        const val ACTION_PLAY = "com.byd.mediaplayer.PLAY"
        const val ACTION_PAUSE = "com.byd.mediaplayer.PAUSE"
        const val ACTION_NEXT = "com.byd.mediaplayer.NEXT"
        const val ACTION_PREVIOUS = "com.byd.mediaplayer.PREVIOUS"
    }
}