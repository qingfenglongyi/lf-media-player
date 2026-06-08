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

/**
 * 播放器前台服务
 * 作为Android服务运行，即使应用在后台也能保持播放
 * 负责创建通知栏控件，支持媒体按钮控制
 *
 * 主要功能：
 * - 以前台服务方式运行，在通知栏显示播放控制
 * - 处理来自通知栏和系统媒体按钮的播放控制指令
 * - 维护PlayerManager生命周期
 */
class PlayerService : Service() {

    /** 播放器管理器实例 */
    private lateinit var playerManager: PlayerManager

    /** Binder用于绑定服务 */
    private val binder = LocalBinder()

    /**
     * LocalBinder内部类
     * 允许Activity绑定服务获取PlayerService实例
     */
    inner class LocalBinder : Binder() {
        /** 获取服务实例 */
        fun getService(): PlayerService = this@PlayerService
    }

    /** 服务创建时初始化 */
    override fun onCreate() {
        super.onCreate()
        Logger.d(TAG, "PlayerService onCreate")
        // 初始化播放器管理器
        playerManager = PlayerManager(this)
        // 创建通知通道（Android 8.0+需要）
        createNotificationChannel()
    }

    /** 绑定服务时返回Binder */
    override fun onBind(intent: Intent?): IBinder {
        Logger.d(TAG, "PlayerService onBind")
        return binder
    }

    /**
     * 处理服务启动命令
     * 根据Intent的action执行相应操作
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            // 播放
            ACTION_PLAY -> {
                Logger.i(TAG, "收到播放指令")
                playerManager.play()
            }
            // 暂停
            ACTION_PAUSE -> {
                Logger.i(TAG, "收到暂停指令")
                playerManager.pause()
            }
            // 下一曲
            ACTION_NEXT -> {
                Logger.i(TAG, "收到下一曲指令")
                playerManager.playNext()
            }
            // 上一曲
            ACTION_PREVIOUS -> {
                Logger.i(TAG, "收到上一曲指令")
                playerManager.playPrevious()
            }
        }
        // 非粘性服务，被杀死后不会自动重启
        return START_NOT_STICKY
    }

    /** 获取PlayerManager实例 */
    fun getPlayerManager(): PlayerManager = playerManager

    /**
     * 启动前台服务
     * 在开始播放时调用，显示持续通知
     */
    fun startForegroundService() {
        Logger.d(TAG, "启动前台服务")
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+需要指定前台服务类型
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    /**
     * 更新通知栏
     * @param song 当前播放的歌曲
     * @param isPlaying 是否正在播放
     */
    fun updateNotification(song: com.byd.mediaplayer.model.Song, isPlaying: Boolean) {
        Logger.d(TAG, "更新通知: ${song.title}, isPlaying: $isPlaying")
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, createNotification(song, isPlaying))
    }

    /** 创建通知通道（Android 8.0+） */
    private fun createNotificationChannel() {
        Logger.d(TAG, "创建通知通道")
        val channel = NotificationChannel(
            CHANNEL_ID,
            "音乐播放",  // 通道名称
            NotificationManager.IMPORTANCE_LOW  // 低优先级，不弹出声音
        ).apply {
            description = "音乐播放通知"  // 通道描述
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * 创建通知栏
     * @param song 当前播放的歌曲（可选）
     * @param isPlaying 是否正在播放
     * @return 通知对象
     */
    private fun createNotification(
        song: com.byd.mediaplayer.model.Song? = null,
        isPlaying: Boolean = false
    ): Notification {
        // 创建点击通知打开Activity的Intent
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        // 根据播放状态选择播放或暂停按钮图标
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

        // 构建通知
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(song?.title ?: "未播放")
            .setContentText(song?.artist ?: "比亚迪音乐播放器")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(contentIntent)  // 点击打开主界面
            .addAction(android.R.drawable.ic_media_previous, "上一曲", createPendingIntent(ACTION_PREVIOUS))
            .addAction(playPauseAction)
            .addAction(android.R.drawable.ic_media_next, "下一曲", createPendingIntent(ACTION_NEXT))
            // 使用媒体样式，在锁屏和通知栏显示媒体控件
            .setStyle(
                MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)  // 在紧凑视图显示3个操作按钮
            )
            .setOngoing(isPlaying)  // 正在播放时持续显示
            .build()
    }

    /**
     * 创建PendingIntent用于通知栏按钮
     * @param action 操作类型
     * @return PendingIntent
     */
    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, PlayerService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            action.hashCode(),  // 请求码
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** 服务销毁时释放资源 */
    override fun onDestroy() {
        Logger.d(TAG, "PlayerService onDestroy")
        playerManager.release()
        super.onDestroy()
    }

    companion object {
        const val TAG = "PlayerService"
        const val CHANNEL_ID = "lf_media_player_channel"  // 通知通道ID
        const val NOTIFICATION_ID = 1                      // 通知ID

        // Action常量，用于标识通知栏按钮操作
        const val ACTION_PLAY = "com.byd.mediaplayer.PLAY"
        const val ACTION_PAUSE = "com.byd.mediaplayer.PAUSE"
        const val ACTION_NEXT = "com.byd.mediaplayer.NEXT"
        const val ACTION_PREVIOUS = "com.byd.mediaplayer.PREVIOUS"
    }
}