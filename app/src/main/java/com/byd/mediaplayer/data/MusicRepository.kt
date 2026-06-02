package com.byd.mediaplayer.data

import android.content.Context
import com.byd.mediaplayer.data.database.AppDatabase
import com.byd.mediaplayer.model.Song
import com.byd.mediaplayer.model.SongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MusicRepository(private val context: Context) {

    private val database = AppDatabase.getInstance(context)

    suspend fun getAllSongs(): List<Song> = withContext(Dispatchers.IO) {
        MediaStoreHelper.querySongs(context)
    }

    suspend fun getSongsByArtist(artist: String): List<Song> = withContext(Dispatchers.IO) {
        MediaStoreHelper.querySongsByArtist(context, artist)
    }

    suspend fun getSongsByAlbum(album: String): List<Song> = withContext(Dispatchers.IO) {
        MediaStoreHelper.querySongsByAlbum(context, album)
    }

    suspend fun searchSongs(query: String): List<Song> = withContext(Dispatchers.IO) {
        MediaStoreHelper.searchSongs(context, query)
    }

    suspend fun saveSongToLibrary(song: Song) = withContext(Dispatchers.IO) {
        database.songDao().insertSong(song.toEntity())
    }

    suspend fun getSavedSongs(): Flow<List<Song>> = withContext(Dispatchers.IO) {
        database.songDao().getAllSongs().map { entities ->
            entities.map { it.toSong() }
        }
    }

    suspend fun deleteSongFromLibrary(songId: Long) = withContext(Dispatchers.IO) {
        database.songDao().deleteSong(songId)
    }

    private fun Song.toEntity() = SongEntity(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        path = path
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
                    INSTANCE = it
                }
            }
        }
    }
}