package com.byd.mediaplayer.data

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import com.byd.mediaplayer.model.Song
import com.byd.mediaplayer.util.Logger

/**
 * MediaStore帮助类
 * 负责从Android系统MediaStore查询音频文件信息
 *
 * 支持两种查询方式：
 * 1. 系统MediaStore查询（需要READ_EXTERNAL_STORAGE权限）
 * 2. SAF(Storage Access Framework)目录扫描（Android 10+推荐）
 */
object MediaStoreHelper {

    private const val TAG = "MediaStoreHelper"

    // MediaStore集合URI，根据Android版本选择不同API
    private val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10+ 使用可变的外部内容URI
        Logger.d(TAG, "使用Android 10+ MediaStore API")
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        // Android 9及以下使用固定的外部内容URI
        Logger.d(TAG, "使用Legacy MediaStore API")
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    // 查询投影：指定需要查询的列
    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,        // 音频ID
        MediaStore.Audio.Media.TITLE,      // 标题
        MediaStore.Audio.Media.ARTIST,    // 艺术家
        MediaStore.Audio.Media.ALBUM,     // 专辑
        MediaStore.Audio.Media.DURATION,  // 时长
        MediaStore.Audio.Media.DATA       // 文件路径（Android 9及以下）
    )

    // ============ 公开查询方法 ============

    /**
     * 查询所有音乐文件
     * @param context Android上下文
     * @return 歌曲列表
     */
    fun querySongs(context: Context): List<Song> {
        Logger.d(TAG, "查询所有歌曲")
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"  // 只查询音乐文件
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"       // 按标题升序
        return querySongs(context, selection, null, sortOrder)
    }

    /**
     * 从指定目录查询歌曲（使用SAF）
     * 适用于Android 10+的Scoped Storage限制
     *
     * @param context Android上下文
     * @param directoryUri SAF选择的目录URI
     * @return 歌曲列表
     */
    fun querySongsFromDirectory(context: Context, directoryUri: Uri): List<Song> {
        Logger.d(TAG, "从目录查询歌曲: $directoryUri")
        val songs = mutableListOf<Song>()

        try {
            // 从URI创建DocumentFile
            val documentFile = DocumentFile.fromTreeUri(context, directoryUri) ?: run {
                Logger.e(TAG, "无法创建DocumentFile")
                return songs
            }

            // 递归扫描目录
            scanDirectoryForAudioFiles(context, documentFile, songs)

            Logger.i(TAG, "从目录共扫描 ${songs.size} 首歌曲")
        } catch (e: Exception) {
            Logger.e(TAG, "扫描目录失败: ${e.message}", e)
        }

        return songs
    }

    /**
     * 递归扫描目录查找音频文件
     *
     * @param context Android上下文
     * @param dir 要扫描的目录
     * @param songs 收集结果的列表
     */
    private fun scanDirectoryForAudioFiles(context: Context, dir: DocumentFile, songs: MutableList<Song>) {
        val files: Array<DocumentFile>? = dir.listFiles() ?: return
        if (files == null) return

        for (file in files) {
            if (file.isDirectory) {
                // 递归处理子目录
                scanDirectoryForAudioFiles(context, file, songs)
            } else if (file.isFile) {
                val name = file.name ?: continue
                // 检查是否为支持的音频格式
                if (name.endsWith(".mp3", true) ||
                    name.endsWith(".flac", true) ||
                    name.endsWith(".m4a", true) ||
                    name.endsWith(".wav", true) ||
                    name.endsWith(".ogg", true) ||
                    name.endsWith(".aac", true)) {

                    val uri = file.uri
                    // 使用文件名作为默认标题
                    val title = name.substringBeforeLast(".")

                    // 使用MediaMetadataRetriever获取音频元数据
                    var artist = "未知艺术家"
                    var album = "未知专辑"
                    var duration = 0L

                    try {
                        val retriever = MediaMetadataRetriever()
                        retriever.setDataSource(context, uri)
                        artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "未知艺术家"
                        album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "未知专辑"
                        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        duration = durationStr?.toLongOrNull() ?: 0L
                        retriever.release()
                    } catch (e: Exception) {
                        Logger.w(TAG, "无法获取音频文件元数据: $name, ${e.message}")
                    }

                    // 添加到歌曲列表
                    songs.add(
                        Song(
                            id = uri.hashCode().toLong(),  // 使用URI的hashCode作为ID
                            title = title,
                            artist = artist,
                            album = album,
                            duration = duration,
                            uri = uri,
                            path = uri.toString()
                        )
                    )
                    Logger.v(TAG, "扫描到音频文件: $name, 艺术家: $artist, 专辑: $album, 时长: $duration")
                }
            }
        }
    }

    /**
     * 按艺术家查询歌曲
     * @param context Android上下文
     * @param artist 艺术家名称
     * @return 该艺术家的歌曲列表
     */
    fun querySongsByArtist(context: Context, artist: String): List<Song> {
        Logger.d(TAG, "按艺术家查询: $artist")
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.ARTIST} = ?"
        val selectionArgs = arrayOf(artist)
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        return querySongs(context, selection, selectionArgs, sortOrder)
    }

    /**
     * 按专辑查询歌曲
     * @param context Android上下文
     * @param album 专辑名称
     * @return 该专辑的歌曲列表
     */
    fun querySongsByAlbum(context: Context, album: String): List<Song> {
        Logger.d(TAG, "按专辑查询: $album")
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.ALBUM} = ?"
        val selectionArgs = arrayOf(album)
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        return querySongs(context, selection, selectionArgs, sortOrder)
    }

    /**
     * 搜索歌曲
     * 在标题、艺术家、专辑中搜索
     *
     * @param context Android上下文
     * @param query 搜索关键词
     * @return 匹配的歌曲列表
     */
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

    /**
     * 执行具体的MediaStore查询
     *
     * @param context Android上下文
     * @param selection WHERE条件
     * @param selectionArgs 条件参数
     * @param sortOrder 排序方式
     * @return 歌曲列表
     */
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
            // 获取列索引
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

                // 构建内容URI
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

    /**
     * 获取所有艺术家列表
     * @param context Android上下文
     * @return 不重复的艺术家名称列表
     */
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

    /**
     * 获取所有专辑列表
     * @param context Android上下文
     * @return 不重复的专辑名称列表
     */
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