package com.byd.mediaplayer.data.database

import androidx.room.*
import com.byd.mediaplayer.model.Config
import com.byd.mediaplayer.model.Playlist
import com.byd.mediaplayer.model.PlaylistSong
import com.byd.mediaplayer.model.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY updatedAt DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Query("SELECT * FROM playlists ORDER BY updatedAt DESC")
    suspend fun getAllPlaylistsOnce(): List<Playlist>

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistById(id: Long): Playlist?

    @Query("SELECT * FROM playlists WHERE name = :name")
    suspend fun getPlaylistByName(name: String): Playlist?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlaylistById(id: Long)

    // Playlist songs
    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY position")
    suspend fun getPlaylistSongs(playlistId: Long): List<PlaylistSong>

    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY position")
    fun getPlaylistSongsFlow(playlistId: Long): Flow<List<PlaylistSong>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSong(playlistSong: PlaylistSong)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun deletePlaylistSong(playlistId: Long, songId: Long)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)

    @Query("SELECT COUNT(*) FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun getPlaylistSongCount(playlistId: Long): Int
}

@Dao
interface SongDao {
    @Query("SELECT * FROM songs ORDER BY lastPlayedAt DESC")
    fun getAllSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: Long): SongEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun deleteSong(id: Long)

    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()

    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%'")
    fun searchSongs(query: String): Flow<List<SongEntity>>

    // Hidden songs (从库中移除但不删除文件)
    @Query("SELECT id FROM songs WHERE isHidden = 1")
    suspend fun getHiddenSongIds(): List<Long>

    @Query("UPDATE songs SET isHidden = 1 WHERE id = :id")
    suspend fun hideSong(id: Long)

    @Query("UPDATE songs SET isHidden = 0 WHERE id = :id")
    suspend fun unhideSong(id: Long)
}

@Dao
interface ConfigDao {
    @Query("SELECT * FROM config WHERE `key` = :key")
    suspend fun getConfig(key: String): Config?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: Config)

    @Query("DELETE FROM config WHERE `key` = :key")
    suspend fun deleteConfig(key: String)

    @Query("SELECT * FROM config")
    suspend fun getAllConfigs(): List<Config>

    // 便捷方法
    @Query("SELECT value FROM config WHERE `key` = 'current_playlist'")
    suspend fun getCurrentPlaylist(): String?

    @Query("INSERT OR REPLACE INTO config (`key`, value, updatedAt) VALUES ('current_playlist', :json, :updatedAt)")
    suspend fun setCurrentPlaylist(json: String, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT value FROM config WHERE `key` = 'current_id'")
    suspend fun getCurrentId(): Long?

    @Query("INSERT OR REPLACE INTO config (`key`, value, updatedAt) VALUES ('current_id', :id, :updatedAt)")
    suspend fun setCurrentId(id: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT value FROM config WHERE `key` = 'play_mode'")
    suspend fun getPlayMode(): String?

    @Query("INSERT OR REPLACE INTO config (`key`, value, updatedAt) VALUES ('play_mode', :mode, :updatedAt)")
    suspend fun setPlayMode(mode: String, updatedAt: Long = System.currentTimeMillis())
}