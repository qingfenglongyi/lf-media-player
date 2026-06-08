package com.byd.mediaplayer.data

import android.content.Context
import com.byd.mediaplayer.data.database.AppDatabase
import com.byd.mediaplayer.model.Config
import com.byd.mediaplayer.model.Song
import com.byd.mediaplayer.model.SongEntity
import com.byd.mediaplayer.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * 音乐仓库类
 * 统一管理歌曲数据的来源：MediaStore和本地数据库
 *
 * 主要功能：
 * - 从MediaStore获取歌曲列表
 * - 管理已保存的歌曲（同步到数据库）
 * - 处理歌曲的隐藏/显示
 * - 保存和恢复播放列表
 *
 * 使用单例模式，通过 MusicRepository.getInstance(context) 获取实例
 */
class MusicRepository(private val context: Context) {

    private val TAG = "MusicRepository"

    // 数据库实例
    private val database = AppDatabase.getInstance(context)

    // 缓存的隐藏歌曲ID（用于快速过滤）
    private var hiddenSongIds: Set<Long> = emptySet()

    // ============ 歌曲查询方法 ============

    /**
     * 获取所有可见歌曲
     * 从MediaStore查询并过滤隐藏的歌曲
     *
     * @return 歌曲列表（排除已隐藏的）
     */
    suspend fun getAllSongs(): List<Song> = withContext(Dispatchers.IO) {
        Logger.d(TAG, "获取所有歌曲")
        // 从MediaStore查询歌曲
        val songs = MediaStoreHelper.querySongs(context)

        // 加载隐藏列表
        hiddenSongIds = database.songDao().getHiddenSongIds().toSet()
        Logger.d(TAG, "隐藏歌曲数量: ${hiddenSongIds.size}")

        // 过滤隐藏的歌曲
        val visibleSongs = songs.filter { it.id !in hiddenSongIds }
        Logger.i(TAG, "获取到 ${visibleSongs.size} 首可见歌曲 (共${songs.size}首)")

        // 同步歌曲到数据库（确保有记录用于跟踪隐藏状态）
        songs.forEach { song ->
            try {
                database.songDao().insertSong(song.toEntity())
            } catch (e: Exception) {
                // 忽略插入失败（已存在的记录）
            }
        }

        visibleSongs
    }

    /**
     * 按艺术家查询歌曲
     * @param artist 艺术家名称
     * @return 该艺术家的可见歌曲
     */
    suspend fun getSongsByArtist(artist: String): List<Song> = withContext(Dispatchers.IO) {
        Logger.d(TAG, "按艺术家查询: $artist")
        val songs = MediaStoreHelper.querySongsByArtist(context, artist)
        val filteredSongs = songs.filter { it.id !in hiddenSongIds }
        Logger.d(TAG, "艺术家 $artist 有 ${filteredSongs.size} 首可见歌曲")
        filteredSongs
    }

    /**
     * 按专辑查询歌曲
     * @param album 专辑名称
     * @return 该专辑的可见歌曲
     */
    suspend fun getSongsByAlbum(album: String): List<Song> = withContext(Dispatchers.IO) {
        Logger.d(TAG, "按专辑查询: $album")
        val songs = MediaStoreHelper.querySongsByAlbum(context, album)
        val filteredSongs = songs.filter { it.id !in hiddenSongIds }
        Logger.d(TAG, "专辑 $album 有 ${filteredSongs.size} 首可见歌曲")
        filteredSongs
    }

    /**
     * 搜索歌曲
     * @param query 搜索关键词
     * @return 匹配的可见歌曲
     */
    suspend fun searchSongs(query: String): List<Song> = withContext(Dispatchers.IO) {
        Logger.d(TAG, "搜索歌曲: $query")
        val songs = MediaStoreHelper.searchSongs(context, query)
        val filteredSongs = songs.filter { it.id !in hiddenSongIds }
        Logger.d(TAG, "搜索结果: ${filteredSongs.size} 首可见歌曲")
        filteredSongs
    }

    // ============ 歌曲库管理方法 ============

    /** 保存歌曲到本地库（同步到数据库） */
    suspend fun saveSongToLibrary(song: Song) = withContext(Dispatchers.IO) {
        Logger.d(TAG, "保存歌曲到本地库: ${song.title}")
        database.songDao().insertSong(song.toEntity())
    }

    /** 获取已保存的歌曲（Flow形式） */
    suspend fun getSavedSongs(): Flow<List<Song>> = withContext(Dispatchers.IO) {
        Logger.d(TAG, "获取已保存的歌曲")
        database.songDao().getAllSongs().map { entities ->
            entities.filter { !it.isHidden }.map { it.toSong() }
        }
    }

    /** 从本地库删除歌曲 */
    suspend fun deleteSongFromLibrary(songId: Long) = withContext(Dispatchers.IO) {
        Logger.d(TAG, "从本地库删除歌曲: id=$songId")
        database.songDao().deleteSong(songId)
    }

    // ============ 歌曲隐藏/显示方法 ============

    /** 从库中隐藏歌曲（不删除文件，只是不再显示） */
    suspend fun hideSong(songId: Long) = withContext(Dispatchers.IO) {
        Logger.i(TAG, "隐藏歌曲: id=$songId")
        database.songDao().hideSong(songId)
        hiddenSongIds = hiddenSongIds + songId
    }

    /** 取消隐藏歌曲 */
    suspend fun unhideSong(songId: Long) = withContext(Dispatchers.IO) {
        Logger.i(TAG, "取消隐藏歌曲: id=$songId")
        database.songDao().unhideSong(songId)
        hiddenSongIds = hiddenSongIds - songId
    }

    /** 批量隐藏歌曲 */
    suspend fun hideSongs(songIds: List<Long>) = withContext(Dispatchers.IO) {
        Logger.i(TAG, "批量隐藏歌曲: ${songIds.size}首")
        songIds.forEach { songId ->
            database.songDao().hideSong(songId)
        }
        hiddenSongIds = hiddenSongIds + songIds.toSet()
    }

    // ============ 播放列表持久化方法 ============

    /**
     * 保存当前播放列表到数据库
     * 用于下次启动时恢复播放状态
     *
     * @param songs 当前播放列表
     * @param currentIndex 当前播放索引
     * @param playMode 当前播放模式
     */
    suspend fun saveCurrentPlaylist(songs: List<Song>, currentIndex: Int, playMode: String) = withContext(Dispatchers.IO) {
        Logger.d(TAG, "保存当前播放列表: ${songs.size}首, index=$currentIndex, mode=$playMode")
        try {
            // 将播放列表保存为JSON格式：[{songId: xxx, position: 0}, ...]
            val playlistJson = JSONArray()
            songs.forEachIndexed { index, song ->
                val obj = JSONObject().apply {
                    put("songId", song.id)
                    put("position", index)
                }
                playlistJson.put(obj)
            }
            database.configDao().insertConfig(Config("current_playlist", playlistJson.toString()))

            // 保存当前索引
            database.configDao().insertConfig(Config("current_index", currentIndex.toString()))

            // 保存播放模式
            database.configDao().insertConfig(Config("play_mode", playMode))

            Logger.i(TAG, "播放列表保存成功")
        } catch (e: Exception) {
            Logger.e(TAG, "保存播放列表失败", e)
        }
    }

    /**
     * 从数据库恢复播放列表
     *
     * @param allSongs 全部可用歌曲列表（用于过滤已删除的歌曲）
     * @return 恢复的(播放列表, 当前索引, 播放模式)，如果无保存数据返回null
     */
    suspend fun restoreCurrentPlaylist(allSongs: List<Song>): Triple<List<Song>, Int, String>? = withContext(Dispatchers.IO) {
        Logger.d(TAG, "尝试恢复播放列表")
        try {
            // 获取保存的播放列表配置
            val playlistConfig = database.configDao().getConfig("current_playlist") ?: run {
                Logger.d(TAG, "没有保存的播放列表")
                return@withContext null
            }

            val indexConfig = database.configDao().getConfig("current_index")
            val modeConfig = database.configDao().getConfig("play_mode")

            // 解析JSON获取歌曲ID列表
            val jsonArray = JSONArray(playlistConfig.value)
            val songIds = mutableListOf<Long>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                songIds.add(obj.getLong("songId"))
            }

            // 根据songId从allSongs中恢复播放列表
            val restoredPlaylist = songIds.mapNotNull { songId ->
                allSongs.find { it.id == songId }
            }.filter { it.id !in hiddenSongIds }  // 过滤隐藏歌曲

            // 恢复索引和播放模式
            val restoredIndex = indexConfig?.value?.toIntOrNull()?.coerceIn(0, restoredPlaylist.size - 1) ?: 0
            val playMode = modeConfig?.value ?: "LIST_LOOP"

            Logger.i(TAG, "播放列表恢复成功: ${restoredPlaylist.size}首, index=$restoredIndex, mode=$playMode")
            Triple(restoredPlaylist, restoredIndex, playMode)
        } catch (e: Exception) {
            Logger.e(TAG, "恢复播放列表失败", e)
            null
        }
    }

    // ============ 类型转换方法 ============

    /** Song转换为SongEntity */
    private fun Song.toEntity() = SongEntity(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        path = path,
        isHidden = id in hiddenSongIds
    )

    /** SongEntity转换为Song */
    private fun SongEntity.toSong() = Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        uri = android.net.Uri.parse("content://media/audio/media/$id"),
        path = path
    )

    // ============ 单例模式 ============

    companion object {
        // 单例实例
        @Volatile
        private var INSTANCE: MusicRepository? = null

        /**
         * 获取MusicRepository单例实例
         *
         * @param context Android上下文
         * @return MusicRepository实例
         */
        fun getInstance(context: Context): MusicRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MusicRepository(context.applicationContext).also {
                    INSTANCE ?: it
                }
            }
        }
    }
}