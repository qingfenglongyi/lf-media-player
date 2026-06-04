# 运行时错误

## 已修复

- ~~播放和暂停按键点击时图标没有切换~~ - 已修复：添加PlayerListener监听器
- ~~标题没有随歌曲变化而变化~~ - 已修复：PlayerListener会更新currentSong
- ~~进度条时间不对~~ - 已修复：LaunchedEffect轮询更新位置
- ~~播放模式无法切换，点击后图标没有变化~~ - 已修复：onPlayModeChange直接更新playMode状态

## 功能清单

### 播放器核心功能
- [x] 播放/暂停
- [x] 上一曲/下一曲
- [x] 进度条拖动
- [x] 播放模式切换（列表循环、单曲循环、随机）
- [x] 音量控制

### 歌曲库功能
- [x] 显示所有本地歌曲
- [x] 按艺术家分类浏览
- [x] 按专辑分类浏览
- [x] 搜索歌曲
- [x] 多选歌曲添加到播放队列
- [x] 多选歌曲添加到歌单

### 歌单功能
- [x] 创建歌单
- [x] 删除歌单
- [x] 查看歌单详情
- [x] 从歌单中删除歌曲
- [x] 添加歌曲到歌单

### 歌词功能
- 歌词显示需要满足以下条件：
  1. 音乐文件所在目录需要有同名.lrc文件（如：音乐.mp3 需要 音乐.lrc）
  2. 歌词文件需要和音乐文件在同一目录
  3. 日志中会显示歌词查找过程的详细信息

### 日志功能
- 日志目录位于：`/storage/emulated/0/Documents/logs/`
- 每天一个日志文件，格式：`app_YYYY-MM-DD.log`
- 日志级别：VERBOSE, DEBUG, INFO, WARN, ERROR
- 详细日志模块：MediaStoreHelper, MusicRepository, LrcParser, Lyrics, PlayerManager, PlayerService, MainActivity

## 已知限制
- 播放列表不支持删除单个歌曲（只能清空）