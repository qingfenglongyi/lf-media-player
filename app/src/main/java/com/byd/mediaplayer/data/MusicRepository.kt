package com.byd.mediaplayer.data

import android.content.Context
import com.byd.mediaplayer.data.database.AppDatabase
import com.byd.mediaplayer.model.Song
import com.byd.mediaplayer.model.SongEntity
import com.byd.mediaplayer.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MusicRepository(private val context: Context) {

    private val TAG = "MusicRepository"
    private val database = AppDatabase.getInstance(context)

    // 缓存的隐藏歌曲ID
    private var hiddenSongIds: Set<Long> = emptySet()

    suspend fun getAllSongs(): List<Song> = withContext(Dispatchers.IO) {
        Logger.d(TAG, "获取所有歌曲")
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

    suspend fun getSongsByArtist(artist: String): List<Song> = withContext(Dispatchers.IO) {
        Logger.d(TAG, "按艺术家查询: $artist")
        val songs = MediaStoreHelper.querySongsByArtist(context, artist)
        val filteredSongs = songs.filter { it.id !in hiddenSongIds }
        Logger.d(TAG, "艺术家 $artist 有 ${filteredSongs.size} 首可见歌曲")
        filteredSongs
    }

    suspend fun getSongsByAlbum(album: String): List<Song> = withContext(Dispatchers.IO) {
        Logger.d(TAG, "按专辑查询: $album")
        val songs = MediaStoreHelper.querySongsByAlbum(context, album)
        val filteredSongs = songs.filter { it.id !in hiddenSongIds }
        Logger.d(TAG, "专辑 $album 有 ${filteredSongs.size} 首可见歌曲")
        filteredSongs
    }

    suspend fun searchSongs(query: String): List<Song> = withContext(Dispatchers.IO) {
        Logger.d(TAG, "搜索歌曲: $query")
        val songs = MediaStoreHelper.searchSongs(context, query)
        val filteredSongs = songs.filter { it.id !in hiddenSongIds }
        Logger.d(TAG, "搜索结果: ${filteredSongs.size} 首可见歌曲")
        filteredSongs
    }

    suspend fun saveSongToLibrary(song: Song) = withContext(Dispatchers.IO) {
        Logger.d(TAG, "保存歌曲到本地库: ${song.title}")
        database.songDao().insertSong(song.toEntity())
    }

    suspend fun getSavedSongs(): Flow<List<Song>> = withContext(Dispatchers.IO) {
        Logger.d(TAG, "获取已保存的歌曲")
        database.songDao().getAllSongs().map { entities ->
            entities.filter { !it.isHidden }.map { it.toSong() }
        }
    }

    suspend fun deleteSongFromLibrary(songId: Long) = withContext(Dispatchers.IO) {
        Logger.d(TAG, "从本地库删除歌曲: id=$songId")
        database.songDao().deleteSong(songId)
    }

    // 从库中隐藏歌曲（不删除文件）
    suspend fun hideSong(songId: Long) = withContext(Dispatchers.IO) {
        Logger.i(TAG, "隐藏歌曲: id=$songId")
        database.songDao().hideSong(songId)
        hiddenSongIds = hiddenSongIds + songId
    }

    // 取消隐藏歌曲
    suspend fun unhideSong(songId: Long) = withContext(Dispatchers.IO) {
        Logger.i(TAG, "取消隐藏歌曲: id=$songId")
        database.songDao().unhideSong(songId)
        hiddenSongIds = hiddenSongIds - songId
    }

    // 批量隐藏歌曲
    suspend fun hideSongs(songIds: List<Long>) = withContext(Dispatchers.IO) {
        Logger.i(TAG, "批量隐藏歌曲: ${songIds.size}首")
        songIds.forEach { songId ->
            database.songDao().hideSong(songId)
        }
        hiddenSongIds = hiddenSongIds + songIds.toSet()
    }

    private fun Song.toEntity() = SongEntity(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        path = path,
        isHidden = id in hiddenSongIds
    )

    private fun SongEntity.toSong() = Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        uri = android.net.Uri.parse("content://media/audio/media/$id"),
        path = path
    )

    companion object {
        @Volatile
        private var INSTANCE: MusicRepository? = null

        fun getInstance(context: Context): MusicRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MusicRepository(context.applicationContext).also {
                    INSTANCE ?: it
                }
            }
        }
    }
}