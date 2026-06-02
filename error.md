
lf-media-player
Repository navigation
Code
Issues
Actions
Build APK
Initial commit: 最小音乐播放器 with ExoPlayer #1
Annotations
1 error and 1 warning
Build Debug APK
Process completed with exit code 1.
Complete job
Node.js 20 actions are deprecated. The following actions are running on Node.js 20 and may not work as expected: actions/checkout@v4, actions/setup-java@v4. Actions will be forced to run with Node.js 24 by default starting June 16th, 2026. Node.js 20 will be removed from the runner on September 16th, 2026. Please check if updated versions of these actions are available that support Node.js 24. To opt into Node.js 24 now, set the FORCE_JAVASCRIPT_ACTIONS_TO_NODE24=true environment variable on the runner or in your workflow file. Once Node.js 24 becomes the default, you can temporarily opt out by setting ACTIONS_ALLOW_USE_UNSECURE_NODE_VERSION=true. For more information see: https://github.blog/changelog/2025-09-19-deprecation-of-node-20-on-github-actions-runners/
build
failed 8 minutes ago in 2m 11s
1s
1s
0s
1m 4s
1m 2s
Run ./gradlew assembleDebug
  ./gradlew assembleDebug
  shell: /usr/bin/bash -e {0}
  env:
    JAVA_HOME: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
    JAVA_HOME_17_X64: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
> Task :app:preBuild UP-TO-DATE
> Task :app:preDebugBuild UP-TO-DATE
> Task :app:mergeDebugNativeDebugMetadata NO-SOURCE
> Task :app:checkKotlinGradlePluginConfigurationErrors

> Task :app:checkDebugAarMetadata
WARNING: [Processor] Library '/home/runner/.gradle/caches/modules-2/files-2.1/androidx.media3/media3-ui/1.2.0/65a0bff9352c8805515d6bcbee6d24e19a6c3d06/media3-ui-1.2.0.aar' contains references to both AndroidX and old support library. This seems like the library is partially migrated. Jetifier will try to rewrite the library anyway.
 Example of androidX reference: 'androidx/media3/ui/PlayerNotificationManager'
 Example of support library reference: 'android/support/v4/media/session/MediaSessionCompat$Token'
WARNING: [Processor] Library '/home/runner/.gradle/caches/modules-2/files-2.1/androidx.media3/media3-session/1.2.0/56908a8e279d1309f131b639d8c323a7b7d75034/media3-session-1.2.0.aar' contains references to both AndroidX and old support library. This seems like the library is partially migrated. Jetifier will try to rewrite the library anyway.
 Example of androidX reference: 'androidx/media3/session/MediaBrowserImplLegacy$1'
 Example of support library reference: 'android/support/v4/media/MediaBrowserCompat$MediaItem'

> Task :app:generateDebugResValues
> Task :app:mapDebugSourceSetPaths
> Task :app:generateDebugResources
> Task :app:packageDebugResources
> Task :app:mergeDebugResources
> Task :app:createDebugCompatibleScreenManifests
> Task :app:extractDeepLinksDebug
> Task :app:parseDebugLocalResources
> Task :app:processDebugMainManifest
> Task :app:processDebugManifest
> Task :app:javaPreCompileDebug
> Task :app:mergeDebugShaders
> Task :app:compileDebugShaders NO-SOURCE
> Task :app:generateDebugAssets UP-TO-DATE
> Task :app:mergeDebugAssets
> Task :app:compressDebugAssets
> Task :app:desugarDebugFileDependencies
> Task :app:mergeDebugJniLibFolders
> Task :app:checkDebugDuplicateClasses
> Task :app:mergeDebugNativeLibs NO-SOURCE
> Task :app:processDebugManifestForPackage
> Task :app:mergeExtDexDebug
> Task :app:stripDebugDebugSymbols NO-SOURCE
> Task :app:mergeLibDexDebug
> Task :app:processDebugResources FAILED
> Task :app:validateSigningDebug

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':app:processDebugResources'.
> A failure occurred while executing com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask$TaskAction
   > Android resource linking failed
     com.byd.mediaplayer.app-main-53:/mipmap-mdpi/ic_launcher.xml: error: <adaptive-icon> elements require a sdk version of at least 26.
     com.byd.mediaplayer.app-main-53:/mipmap-mdpi/ic_launcher_round.xml: error: <adaptive-icon> elements require a sdk version of at least 26.
     com.byd.mediaplayer.app-main-53:/mipmap-hdpi/ic_launcher.xml: error: <adaptive-icon> elements require a sdk version of at least 26.
     com.byd.mediaplayer.app-main-53:/mipmap-hdpi/ic_launcher_round.xml: error: <adaptive-icon> elements require a sdk version of at least 26.
     com.byd.mediaplayer.app-main-53:/mipmap-xhdpi/ic_launcher.xml: error: <adaptive-icon> elements require a sdk version of at least 26.
     com.byd.mediaplayer.app-main-53:/mipmap-xhdpi/ic_launcher_round.xml: error: <adaptive-icon> elements require a sdk version of at least 26.
     com.byd.mediaplayer.app-main-53:/mipmap-xxhdpi/ic_launcher.xml: error: <adaptive-icon> elements require a sdk version of at least 26.
     com.byd.mediaplayer.app-main-53:/mipmap-xxhdpi/ic_launcher_round.xml: error: <adaptive-icon> elements require a sdk version of at least 26.
     com.byd.mediaplayer.app-main-53:/mipmap-xxxhdpi/ic_launcher.xml: error: <adaptive-icon> elements require a sdk version of at least 26.
     com.byd.mediaplayer.app-main-53:/mipmap-xxxhdpi/ic_launcher_round.xml: error: <adaptive-icon> elements require a sdk version of at least 26.
     error: failed linking file resources.


* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
23 actionable tasks: 23 executed
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.

BUILD FAILED in 1m 1s
Error: Process completed with exit code 1.