package com.byd.mediaplayer.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.byd.mediaplayer.model.Config
import com.byd.mediaplayer.model.Playlist
import com.byd.mediaplayer.model.PlaylistSong
import com.byd.mediaplayer.model.SongEntity

/**
 * Room数据库抽象类
 * 管理应用程序的本地数据库，包含播放列表、歌曲、配置等数据
 *
 * 数据库版本：2
 * 使用fallbackToDestructiveMigration()在版本升级时自动重建数据库
 */
@Database(
    entities = [Playlist::class, PlaylistSong::class, SongEntity::class, Config::class],
    version = 2,
    exportSchema = false  // 不导出数据库schema
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * 获取播放列表数据访问对象
     */
    abstract fun playlistDao(): PlaylistDao

    /**
     * 获取歌曲数据访问对象
     */
    abstract fun songDao(): SongDao

    /**
     * 获取配置数据访问对象
     */
    abstract fun configDao(): ConfigDao

    companion object {
        // 单例模式：确保整个应用只有一个数据库实例
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * 获取数据库单例实例
         *
         * @param context Android上下文
         * @return 数据库实例
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // 首次创建数据库实例
                val instance = Room.databaseBuilder(
                    context.applicationContext,  // 使用applicationContext避免内存泄漏
                    AppDatabase::class.java,      // 数据库类
                    "lf_media_player_db"         // 数据库文件名
                )
                    // 版本升级时删除旧数据库再创建新数据库
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}