# 本地音乐播放器

一个为比亚迪车机安卓设备设计的本地音乐播放器，支持播放本地音乐文件，具有现代化的用户界面和完整的播放功能。

## 功能特性

### 核心播放功能
- 播放/暂停控制
- 上一曲/下一曲切换
- 进度条拖拽
- 音量控制
- 播放模式切换（列表循环、单曲循环、随机播放）
- 歌词同步显示（LRC文件自动关联加载）

### 车机界面优化
- 为车机大屏优化的触控界面
- 大字体和大按钮设计，适合驾驶时操作
- 手势操作支持
- 夜间模式优化，保护驾驶者视力
- 简化操作流程，减少驾驶时分心

### 音乐格式支持
- 支持 MP3、WAV、FLAC 等常用音频格式

### 列表管理
- **隐藏列表面板**：播放列表、歌单、歌曲库以 Tab 形式管理
- **播放列表**：显示正在播放和即将播放的歌曲，支持清空和删除
- **歌单**：创建、重命名、删除歌单，将歌曲添加到歌单
- **歌曲库**：扫描本地音乐文件，支持按艺术家、专辑、流派分类浏览

### 自动功能
- **自动保存/加载**：扫描的歌曲自动保存到库
- **播放状态恢复**：记录上次播放的歌曲，启动后自动继续播放
- **转盘/歌词切换**：点击中间区域切换显示

## 播放列表管理

### 当前播放列表
- 显示正在播放和即将播放的歌曲列表
- 显示歌曲的基本信息（标题、艺术家）
- 支持清空播放列表
- 支持从播放列表删除歌曲

### 歌单
- 管理用户创建的自定义歌单
- 支持创建、重命名、删除歌单
- 支持将歌曲添加到不同的歌单
- 支持从歌单播放（顺序或随机）
- 支持查看歌单详情，对歌单内歌曲进行删除

### 本地歌曲库
- 扫描添加的音乐文件集合
- 支持文件名过滤，不重复添加同一歌曲
- 支持全选/取消/反选
- 支持搜索和分类浏览（按艺术家、专辑、流派）
- 支持添加到播放列表
- 支持添加到歌单

## 使用说明

### 基本操作
- 点击播放/暂停按钮控制播放
- 点击上一曲/下一曲按钮切换歌曲
- 拖拽进度条调整播放位置
- 点击音量按钮调整音量
- 点击播放模式按钮切换播放模式
- 点击播放控制区的"📋"按钮显示/隐藏列表面板
- 点击中间转盘/歌词区域切换显示

### 列表面板
- 点击播放控制区的"📋"按钮显示/隐藏列表面板
- 默认显示"播放列表"tab
- 可切换到"歌单"或"歌曲库"tab

### 键盘快捷键
- 空格键：播放/暂停
- 左箭头键：上一曲
- 右箭头键：下一曲
- M键：静音/取消静音

## 技术栈

| 组件 | 技术 |
|------|------|
| 开发语言 | Kotlin |
| 音视频播放 | ExoPlayer (Media3) |
| 媒体查询 | MediaStore API |
| 后台播放 | Foreground Service |
| 媒体控制 | MediaSessionCompat |
| 音频管理 | AudioManager |

### 架构
- **UI 层**：Jetpack Compose（车机优化的大屏触控界面）
- **业务层**：ViewModel + StateFlow
- **数据层**：Room 数据库（歌单、歌曲库存储）
- **播放层**：ExoPlayer + MediaSession

## 目录结构

```
lf-media-player/
├── app/
│   └── src/main/
│       ├── java/com/byd/mediaplayer/
│       │   ├── MainActivity.kt          # 主入口
│       │   ├── player/                   # 播放器相关
│       │   │   ├── PlayerService.kt      # Foreground Service
│       │   │   ├── PlayerManager.kt     # ExoPlayer 管理
│       │   │   └── MediaSessionManager.kt
│       │   ├── ui/                      # UI 层
│       │   │   ├── PlayerScreen.kt      # 播放界面
│       │   │   ├── PlaylistPanel.kt     # 播放列表面板
│       │   │   ├── LibraryScreen.kt     # 歌曲库
│       │   │   └── PlaylistScreen.kt    # 歌单管理
│       │   ├── data/                    # 数据层
│       │   │   ├── MusicRepository.kt  # 音乐仓库
│       │   │   ├── MediaStoreHelper.kt  # MediaStore 查询
│       │   │   └── database/
│       │   │       ├── AppDatabase.kt
│       │   │       ├── SongDao.kt
│       │   │       └── PlaylistDao.kt
│       │   ├── model/                   # 数据模型
│       │   │   ├── Song.kt
│       │   │   ├── Playlist.kt
│       │   │   └── PlayMode.kt
│       │   └── util/                    # 工具类
│       │       ├── LrcParser.kt         # 歌词解析
│       │       └── PreferencesManager.kt
│       ├── res/
│       │   ├── layout/
│       │   ├── values/
│       │   └── drawable/
│       └── AndroidManifest.xml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── .github/
    └── workflows/
        └── build.yml                    # GitHub Actions 编译配置
```

## 编译方式

### 本地编译

```bash
# 1. 克隆项目
git clone https://github.com/your-username/lf-media-player.git
cd lf-media-player

# 2. 设置 Gradle Wrapper
./gradlew wrapper

# 3. 编译 Debug APK
./gradlew assembleDebug

# 4. 输出路径
# app/build/outputs/apk/debug/app-debug.apk
```

### GitHub Actions 自动编译

推送代码到 main 分支或创建 Pull Request 时，GitHub Actions 自动编译：

```yaml
# .github/workflows/build.yml
name: Build APK

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - name: Setup Gradle
        run: ./gradlew wrapper
      - name: Build Debug APK
        run: ./gradlew assembleDebug
      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/app-debug.apk
```

编译产物：`app/build/outputs/apk/debug/app-debug.apk`

## 安全说明

所有文件处理均在设备本地进行，不会上传到任何服务器，确保用户隐私安全。

## 许可证

MIT License