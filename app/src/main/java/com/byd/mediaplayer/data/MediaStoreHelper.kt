package com.byd.mediaplayer.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.database.DocumentFile
import com.byd.mediaplayer.model.Song
import com.byd.mediaplayer.util.Logger

object MediaStoreHelper {

    private const val TAG = "MediaStoreHelper"

    private val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Logger.d(TAG, "使用Android 10+ MediaStore API")
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        Logger.d(TAG, "使用Legacy MediaStore API")
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA
    )

    fun querySongs(context: Context): List<Song> {
        Logger.d(TAG, "查询所有歌曲")
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        return querySongs(context, selection, null, sortOrder)
    }

    /**
     * 从指定目录查询歌曲（使用SAF）
     */
    fun querySongsFromDirectory(context: Context, directoryUri: Uri): List<Song> {
        Logger.d(TAG, "从目录查询歌曲: $directoryUri")
        val songs = mutableListOf<Song>()

        try {
            val documentFile = DocumentFile.fromTreeUri(context, directoryUri) ?: run {
                Logger.e(TAG, "无法创建DocumentFile")
                return songs
            }

            scanDirectoryForAudioFiles(context, documentFile, songs)

            Logger.i(TAG, "从目录共扫描 ${songs.size} 首歌曲")
        } catch (e: Exception) {
            Logger.e(TAG, "扫描目录失败: ${e.message}", e)
        }

        return songs
    }

    private fun scanDirectoryForAudioFiles(context: Context, dir: DocumentFile, songs: MutableList<Song>) {
        val files = dir.listFiles() ?: return
        for (i in files.indices) {
            val file = files[i]
            if (file.isDirectory) {
                scanDirectoryForAudioFiles(context, file, songs)
            } else if (file.isFile) {
                val name = file.name ?: continue
                if (name.endsWith(".mp3", true) ||
                    name.endsWith(".flac", true) ||
                    name.endsWith(".m4a", true) ||
                    name.endsWith(".wav", true) ||
                    name.endsWith(".ogg", true) ||
                    name.endsWith(".aac", true)) {

                    val uri = file.uri
                    val title = name.substringBeforeLast(".")
                    songs.add(
                        Song(
                            id = uri.hashCode().toLong(),
                            title = title,
                            artist = "未知艺术家",
                            album = "未知专辑",
                            duration = 0L,
                            uri = uri,
                            path = uri.toString()
                        )
                    )
                    Logger.v(TAG, "扫描到音频文件: $name")
                }
            }
        }
    }

    fun querySongsByArtist(context: Context, artist: String): List<Song> {
        Logger.d(TAG, "按艺术家查询: $artist")
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.ARTIST} = ?"
        val selectionArgs = arrayOf(artist)
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        return querySongs(context, selection, selectionArgs, sortOrder)
    }

    fun querySongsByAlbum(context: Context, album: String): List<Song> {
        Logger.d(TAG, "按专辑查询: $album")
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.ALBUM} = ?"
        val selectionArgs = arrayOf(album)
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        return querySongs(context, selection, selectionArgs, sortOrder)
    }

    fun searchSongs(context: Context, query: String): List<Song> {
        Logger.d(TAG, "搜索歌曲: $query")
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND (" +
                "${MediaStore.Audio.Media.TITLE} LIKE ? OR " +
                "${MediaStore.Audio.Media.ARTIST} LIKE ? OR " +
                "${MediaStore.Audio.Media.ALBUM} LIKE ?)"
        val selectionArgs = arrayOf("%$query%", "%$query%", "%$query%")
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        return querySongs(context, selection, selectionArgs, sortOrder)
    }

    private fun querySongs(
        context: Context,
        selection: String,
        selectionArgs: Array<String>?,
        sortOrder: String
    ): List<Song> {
        val songs = mutableListOf<Song>()
        Logger.d(TAG, "执行MediaStore查询, selection: $selection")

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            Logger.d(TAG, "查询返回 ${cursor.count} 行")
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn) ?: "未知标题"
                val artist = cursor.getString(artistColumn) ?: "未知艺术家"
                val album = cursor.getString(albumColumn) ?: "未知专辑"
                val duration = cursor.getLong(durationColumn)
                val path = cursor.getString(dataColumn) ?: ""

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                Logger.v(TAG, "解析歌曲: id=$id, title=$title, path=$path")

                songs.add(
                    Song(
                        id = id,
                        title = title,
                        artist = artist,
                        album = album,
                        duration = duration,
                        uri = contentUri,
                        path = path
                    )
                )
            }
        } ?: Logger.w(TAG, "查询cursor为null")

        Logger.i(TAG, "共解析 ${songs.size} 首歌曲")
        return songs
    }

    fun getAllArtists(context: Context): List<String> {
        Logger.d(TAG, "获取所有艺术家")
        val artists = mutableSetOf<String>()

        val projection = arrayOf(MediaStore.Audio.Media.ARTIST)
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.ARTIST} ASC"
        )?.use { cursor ->
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

            while (cursor.moveToNext()) {
                cursor.getString(artistColumn)?.let { artists.add(it) }
            }
        }

        Logger.i(TAG, "共 ${artists.size} 位艺术家")
        return artists.toList()
    }

    fun getAllAlbums(context: Context): List<String> {
        Logger.d(TAG, "获取所有专辑")
        val albums = mutableSetOf<String>()

        val projection = arrayOf(MediaStore.Audio.Media.ALBUM)
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.ALBUM} ASC"
        )?.use { cursor ->
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

            while (cursor.moveToNext()) {
                cursor.getString(albumColumn)?.let { albums.add(it) }
            }
        }

        Logger.i(TAG, "共 ${albums.size} 张专辑")
        return albums.toList()
    }
}