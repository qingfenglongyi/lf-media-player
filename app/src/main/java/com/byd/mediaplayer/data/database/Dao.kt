package com.byd.mediaplayer.data.database

import androidx.room.*
import com.byd.mediaplayer.model.Config
import com.byd.mediaplayer.model.Playlist
import com.byd.mediaplayer.model.PlaylistSong
import com.byd.mediaplayer.model.SongEntity
import kotlinx.coroutines.flow.Flow

/**
 * 播放列表数据访问接口
 * 提供播放列表的CRUD操作
 */
@Dao
interface PlaylistDao {
    /**
     * 获取所有播放列表（Flow形式，实时更新）
     * 按更新时间倒序排列
     */
    @Query("SELECT * FROM playlists ORDER BY updatedAt DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    /**
     * 获取所有播放列表（一次性查询）
     * 按更新时间倒序排列
     */
    @Query("SELECT * FROM playlists ORDER BY updatedAt DESC")
    suspend fun getAllPlaylistsOnce(): List<Playlist>

    /** 根据ID获取播放列表 */
    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistById(id: Long): Playlist?

    /** 根据名称获取播放列表 */
    @Query("SELECT * FROM playlists WHERE name = :name")
    suspend fun getPlaylistByName(name: String): Playlist?

    /** 插入播放列表（如果已存在则替换） */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long

    /** 更新播放列表 */
    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    /** 删除播放列表 */
    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    /** 根据ID删除播放列表 */
    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlaylistById(id: Long)

    // ============ 播放列表歌曲关联操作 ============

    /**
     * 获取播放列表中的所有歌曲（一次性查询）
     * 按position排序
     */
    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY position")
    suspend fun getPlaylistSongs(playlistId: Long): List<PlaylistSong>

    /**
     * 获取播放列表中的所有歌曲（Flow形式）
     * 按position排序，用于实时更新
     */
    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY position")
    fun getPlaylistSongsFlow(playlistId: Long): Flow<List<PlaylistSong>>

    /** 添加歌曲到播放列表 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSong(playlistSong: PlaylistSong)

    /** 从播放列表移除歌曲 */
    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun deletePlaylistSong(playlistId: Long, songId: Long)

    /** 清空播放列表中的所有歌曲 */
    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)

    /** 获取播放列表中的歌曲数量 */
    @Query("SELECT COUNT(*) FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun getPlaylistSongCount(playlistId: Long): Int
}

/**
 * 歌曲数据访问接口
 * 提供歌曲的查询、缓存和隐藏功能
 */
@Dao
interface SongDao {
    /** 获取所有歌曲（Flow形式，按最后播放时间倒序） */
    @Query("SELECT * FROM songs ORDER BY lastPlayedAt DESC")
    fun getAllSongs(): Flow<List<SongEntity>>

    /** 根据ID获取歌曲 */
    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: Long): SongEntity?

    /** 插入或替换歌曲 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity)

    /** 批量插入歌曲 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    /** 根据ID删除歌曲 */
    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun deleteSong(id: Long)

    /** 删除所有歌曲 */
    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()

    /** 搜索歌曲（按标题或艺术家） */
    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%'")
    fun searchSongs(query: String): Flow<List<SongEntity>>

    // ============ 歌曲隐藏功能 ============

    /** 获取所有已隐藏的歌曲ID */
    @Query("SELECT id FROM songs WHERE isHidden = 1")
    suspend fun getHiddenSongIds(): List<Long>

    /** 隐藏歌曲（标记为隐藏，不会删除文件） */
    @Query("UPDATE songs SET isHidden = 1 WHERE id = :id")
    suspend fun hideSong(id: Long)

    /** 取消隐藏歌曲 */
    @Query("UPDATE songs SET isHidden = 0 WHERE id = :id")
    suspend fun unhideSong(id: Long)
}

/**
 * 配置数据访问接口
 * 用于存储键值对形式的配置信息
 */
@Dao
interface ConfigDao {
    /** 根据键名获取配置 */
    @Query("SELECT * FROM config WHERE `key` = :key")
    suspend fun getConfig(key: String): Config?

    /** 插入或替换配置 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: Config)

    /** 根据键名删除配置 */
    @Query("DELETE FROM config WHERE `key` = :key")
    suspend fun deleteConfig(key: String)

    /** 获取所有配置 */
    @Query("SELECT * FROM config")
    suspend fun getAllConfigs(): List<Config>

    // ============ 便捷查询方法 ============

    /** 获取当前播放列表JSON（便捷方法） */
    @Query("SELECT value FROM config WHERE `key` = 'current_playlist'")
    suspend fun getCurrentPlaylist(): String?

    /** 获取当前播放歌曲ID（便捷方法） */
    @Query("SELECT CAST(value AS INTEGER) FROM config WHERE `key` = 'current_id'")
    suspend fun getCurrentId(): Long?

    /** 获取播放模式（便捷方法） */
    @Query("SELECT value FROM config WHERE `key` = 'play_mode'")
    suspend fun getPlayMode(): String?
}