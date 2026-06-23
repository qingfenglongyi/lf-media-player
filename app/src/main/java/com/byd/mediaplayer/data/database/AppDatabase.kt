package com.byd.mediaplayer.data.database

import android.content.Context
import android.os.Environment
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.byd.mediaplayer.model.Config
import com.byd.mediaplayer.model.Playlist
import com.byd.mediaplayer.model.PlaylistSong
import com.byd.mediaplayer.model.SongEntity
import com.byd.mediaplayer.util.Logger
import java.io.File

@Database(
    entities = [Playlist::class, PlaylistSong::class, SongEntity::class, Config::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun playlistDao(): PlaylistDao
    abstract fun songDao(): SongDao
    abstract fun configDao(): ConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val TAG = "AppDatabase"
        private const val DB_NAME = "lf_media_player_db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val dbPath = resolveDbPath(context.applicationContext)

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    dbPath
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /** 解析数据库路径：优先外部存储，失败则回退内部存储 */
        private fun resolveDbPath(context: Context): String {
            val dbDir = File(
                Environment.getExternalStorageDirectory(),
                "lf_media_player"
            )
            if ((dbDir.exists() || dbDir.mkdirs()) && dbDir.canWrite()) {
                val externalPath = File(dbDir, DB_NAME).absolutePath
                // 如果外部路径无数据，尝试从旧Documents路径或内部路径迁移
                if (!File(externalPath).exists()) {
                    val migrated = migrateFromOldPath(externalPath)
                            || migrateInternalDbIfNeeded(context, externalPath)
                    if (migrated) {
                        Logger.i(TAG, "数据库迁移到外部存储成功")
                    }
                }
                Logger.i(TAG, "使用外部存储数据库: $externalPath")
                return externalPath
            }
            Logger.w(TAG, "外部存储不可用，回退到内部存储")
            return DB_NAME
        }

        /** 从旧Documents路径迁移数据库 */
        private fun migrateFromOldPath(externalDbPath: String): Boolean {
            val oldDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "lf_media_player"
            )
            val oldDbFile = File(oldDir, DB_NAME)
            if (!oldDbFile.exists()) return false

            try {
                for (suffix in arrayOf("", "-wal", "-shm")) {
                    val src = File(oldDir, "$DB_NAME$suffix")
                    val dst = File("$externalDbPath$suffix")
                    if (src.exists()) {
                        src.copyTo(dst, overwrite = false)
                    }
                }
                Logger.i(TAG, "数据库从Documents路径迁移成功")
                return true
            } catch (e: Exception) {
                Logger.e(TAG, "从Documents路径迁移失败: ${e.message}")
                return false
            }
        }

        /** 首次使用外部路径时，从内部存储复制旧数据库 */
        private fun migrateInternalDbIfNeeded(context: Context, externalDbPath: String) {
            val externalDbFile = File(externalDbPath)
            if (externalDbFile.exists()) return

            val internalDbFile = context.getDatabasePath(DB_NAME)
            if (!internalDbFile.exists()) return

            try {
                val internalDir = File(internalDbFile.parent!!)
                for (suffix in arrayOf("", "-wal", "-shm")) {
                    val src = File(internalDir, "$DB_NAME$suffix")
                    val dst = File("$externalDbPath$suffix")
                    if (src.exists()) {
                        src.copyTo(dst, overwrite = false)
                    }
                }
                Logger.i(TAG, "数据库从内部存储迁移到外部存储成功")
            } catch (e: Exception) {
                Logger.e(TAG, "数据库迁移失败: ${e.message}")
            }
        }
    }
}
