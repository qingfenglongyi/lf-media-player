# 文档说明
每次都检查bug列表里的"复现"和没有状态的bug进行分析解决。

## Bug 列表
（所有已知bug已修复）

## 已修复
- 【已修复】播放列表是空的但还会播放歌曲 - 在PlayerScreen中添加空列表检查
- 【已修复】歌单列表为空 - 在PlayerScreen中添加playlists参数并传递实际歌单列表
- 【已修复】歌曲库没有歌曲时无法返回 - ArtistSongsContent和AlbumSongsContent空状态时添加点击返回功能
- 【已修复】卸载重装后自动播放 - 添加首次启动检测isFirstLaunch，卸载后首次启动不恢复播放状态
- 【已修复】搜索功能使用系统MediaStore - 改用SAF目录扫描进行本地搜索
- 【已修复】列表默认不显示复选框，点击选择按键后再显示复选框。复选框的显示要与选择/取消选择按键状态同步
- 【已修复】退出应用再进入后，自动回复上次播放的歌曲时标题显示不正确
- 【已修复】播放列表和本地歌曲库中的菜单图标更换一下，不要和列表图标一样
- 【已修复】从播放列表中删除歌曲失败
- 【已修复】清空播放列表失败（UI同步问题）
- 【已修复】没有显示歌词（编码检测问题）- 改用多编码尝试选择最佳结果
- 【已修复】歌曲库中点击艺术家或专辑后无法返回
- 【已修复】歌词时间标签解析失败（parseTimeTag中split("[:.]")应使用Regex）
- 【已修复】艺术家里点击的歌曲和播放的歌曲不一样（过滤后列表索引查找错误）
- 【已修复】清空播放列表后歌曲库消失 - 添加librarySongs变量分离数据源
- 【已修复】Logger日志初始化问题 - 确保logDir始终创建
- 【已修复】歌词显示乱码 - 改进编码检测逻辑，使用CJK汉字比例检测
- 【已修复】歌曲结束后不继续播放 - STATE_ENDED时自动播放下一首
- 【已修复】标题居中显示，占界面2/3宽度，过长时水平滚动
- 【已修复】列表菜单图标"⋮"改为文字"操作"
- 【已修复】隐藏"已选择0首"，只在有选中项时显示
- 【已修复】创建歌单后列表不刷新 - 使用first()替代collect()立即获取歌单
- 【已修复】清空播放列表后UI不更新 - 同步更新playlist状态变量
- 【已修复】创建了歌单，但列表不显示 - 添加getAllPlaylistsOnce()直接获取数据
- 【已修复】歌曲库点击艺术家后无法返回 - 添加LaunchedEffect处理selectedArtist/selectedAlbum为null时重置viewState
- 【已修复】清空播放列表后退出APP再启动，播放列表又有了 - 在onClearPlaylist中添加saveCurrentPlaylist保存空列表状态
- 【已修复】设置音乐目录扫描到的歌曲没有艺术家、专辑和时长信息 - 使用MediaMetadataRetriever获取音频元数据
- 【已修复】首次启动自动搜索本地音乐 - 只有设置音乐目录后才自动加载歌曲
- 【已修复】歌单删除后列表不刷新 - 在onDeletePlaylist中添加刷新逻辑和getAllPlaylistsOnce()
- 【已修复】歌曲库界面显示异常 - 修复PlaylistPanel中viewState初始化逻辑，使用LaunchedEffect同时检查selectedArtist和selectedAlbum