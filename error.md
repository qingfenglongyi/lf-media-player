2026-06-06T02:57:29.3686382Z Current runner version: '2.334.0'
2026-06-06T02:57:29.3711268Z ##[group]Runner Image Provisioner
2026-06-06T02:57:29.3712338Z Hosted Compute Agent
2026-06-06T02:57:29.3712976Z Version: 20260520.533
2026-06-06T02:57:29.3713677Z Commit: 189110e25284a9812c124fd27b339e2fb4f2f9db
2026-06-06T02:57:29.3714510Z Build Date: 2026-05-20T17:44:04Z
2026-06-06T02:57:29.3715250Z Worker ID: {90c72f7d-4fe9-4f17-b205-e6f0484e37bf}
2026-06-06T02:57:29.3716083Z Azure Region: westcentralus
2026-06-06T02:57:29.3716737Z ##[endgroup]
2026-06-06T02:57:29.3718052Z ##[group]Operating System
2026-06-06T02:57:29.3719199Z Ubuntu
2026-06-06T02:57:29.3719788Z 24.04.4
2026-06-06T02:57:29.3720369Z LTS
2026-06-06T02:57:29.3720942Z ##[endgroup]
2026-06-06T02:57:29.3721541Z ##[group]Runner Image
2026-06-06T02:57:29.3722308Z Image: ubuntu-24.04
2026-06-06T02:57:29.3722924Z Version: 20260525.161.1
2026-06-06T02:57:29.3724279Z Included Software: https://github.com/actions/runner-images/blob/ubuntu24/20260525.161/images/ubuntu/Ubuntu2404-Readme.md
2026-06-06T02:57:29.3725784Z Image Release: https://github.com/actions/runner-images/releases/tag/ubuntu24%2F20260525.161
2026-06-06T02:57:29.3726907Z ##[endgroup]
2026-06-06T02:57:29.3728125Z ##[group]GITHUB_TOKEN Permissions
2026-06-06T02:57:29.3730332Z Contents: read
2026-06-06T02:57:29.3730982Z Metadata: read
2026-06-06T02:57:29.3731692Z Packages: read
2026-06-06T02:57:29.3732302Z ##[endgroup]
2026-06-06T02:57:29.3734480Z Secret source: Actions
2026-06-06T02:57:29.3735332Z Prepare workflow directory
2026-06-06T02:57:29.4118819Z Prepare all required actions
2026-06-06T02:57:29.4156627Z Getting action download info
2026-06-06T02:57:29.8832323Z Download action repository 'actions/checkout@v4' (SHA:34e114876b0b11c390a56381ad16ebd13914f8d5)
2026-06-06T02:57:29.9973339Z Download action repository 'actions/setup-java@v4' (SHA:c1e323688fd81a25caa38c78aa6df2d33d3e20d9)
2026-06-06T02:57:30.8287754Z Download action repository 'actions/upload-artifact@v4' (SHA:ea165f8d65b6e75b540449e92b4886f43607fa02)
2026-06-06T02:57:31.0555443Z Complete job name: build
2026-06-06T02:57:31.1262519Z ##[group]Run actions/checkout@v4
2026-06-06T02:57:31.1263355Z with:
2026-06-06T02:57:31.1263836Z   repository: qingfenglongyi/lf-media-player
2026-06-06T02:57:31.1269162Z   token: ***
2026-06-06T02:57:31.1269615Z   ssh-strict: true
2026-06-06T02:57:31.1270066Z   ssh-user: git
2026-06-06T02:57:31.1270514Z   persist-credentials: true
2026-06-06T02:57:31.1270999Z   clean: true
2026-06-06T02:57:31.1271438Z   sparse-checkout-cone-mode: true
2026-06-06T02:57:31.1271964Z   fetch-depth: 1
2026-06-06T02:57:31.1272392Z   fetch-tags: false
2026-06-06T02:57:31.1272828Z   show-progress: true
2026-06-06T02:57:31.1273294Z   lfs: false
2026-06-06T02:57:31.1273716Z   submodules: false
2026-06-06T02:57:31.1274161Z   set-safe-directory: true
2026-06-06T02:57:31.1274921Z ##[endgroup]
2026-06-06T02:57:31.2408692Z Syncing repository: qingfenglongyi/lf-media-player
2026-06-06T02:57:31.2410621Z ##[group]Getting Git version info
2026-06-06T02:57:31.2411642Z Working directory is '/home/runner/work/lf-media-player/lf-media-player'
2026-06-06T02:57:31.2412751Z [command]/usr/bin/git version
2026-06-06T02:57:31.2462955Z git version 2.54.0
2026-06-06T02:57:31.2487768Z ##[endgroup]
2026-06-06T02:57:31.2502708Z Temporarily overriding HOME='/home/runner/work/_temp/641693b1-466b-4a26-a1d6-9e8ab26505f5' before making global git config changes
2026-06-06T02:57:31.2505008Z Adding repository directory to the temporary git global config as a safe directory
2026-06-06T02:57:31.2509532Z [command]/usr/bin/git config --global --add safe.directory /home/runner/work/lf-media-player/lf-media-player
2026-06-06T02:57:31.2558560Z Deleting the contents of '/home/runner/work/lf-media-player/lf-media-player'
2026-06-06T02:57:31.2561220Z ##[group]Initializing the repository
2026-06-06T02:57:31.2566007Z [command]/usr/bin/git init /home/runner/work/lf-media-player/lf-media-player
2026-06-06T02:57:31.2650302Z hint: Using 'master' as the name for the initial branch. This default branch name
2026-06-06T02:57:31.2651724Z hint: will change to "main" in Git 3.0. To configure the initial branch name
2026-06-06T02:57:31.2652683Z hint: to use in all of your new repositories, which will suppress this warning,
2026-06-06T02:57:31.2653412Z hint: call:
2026-06-06T02:57:31.2653809Z hint:
2026-06-06T02:57:31.2654312Z hint: 	git config --global init.defaultBranch <name>
2026-06-06T02:57:31.2654950Z hint:
2026-06-06T02:57:31.2655867Z hint: Names commonly chosen instead of 'master' are 'main', 'trunk' and
2026-06-06T02:57:31.2657050Z hint: 'development'. The just-created branch can be renamed via this command:
2026-06-06T02:57:31.2657786Z hint:
2026-06-06T02:57:31.2658218Z hint: 	git branch -m <name>
2026-06-06T02:57:31.2658928Z hint:
2026-06-06T02:57:31.2659542Z hint: Disable this message with "git config set advice.defaultBranchName false"
2026-06-06T02:57:31.2660578Z Initialized empty Git repository in /home/runner/work/lf-media-player/lf-media-player/.git/
2026-06-06T02:57:31.2667538Z [command]/usr/bin/git remote add origin https://github.com/qingfenglongyi/lf-media-player
2026-06-06T02:57:31.2714182Z ##[endgroup]
2026-06-06T02:57:31.2714971Z ##[group]Disabling automatic garbage collection
2026-06-06T02:57:31.2718165Z [command]/usr/bin/git config --local gc.auto 0
2026-06-06T02:57:31.2746755Z ##[endgroup]
2026-06-06T02:57:31.2747484Z ##[group]Setting up auth
2026-06-06T02:57:31.2753714Z [command]/usr/bin/git config --local --name-only --get-regexp core\.sshCommand
2026-06-06T02:57:31.2785676Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'core\.sshCommand' && git config --local --unset-all 'core.sshCommand' || :"
2026-06-06T02:57:31.3127112Z [command]/usr/bin/git config --local --name-only --get-regexp http\.https\:\/\/github\.com\/\.extraheader
2026-06-06T02:57:31.3156899Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'http\.https\:\/\/github\.com\/\.extraheader' && git config --local --unset-all 'http.https://github.com/.extraheader' || :"
2026-06-06T02:57:31.3385021Z [command]/usr/bin/git config --local --name-only --get-regexp ^includeIf\.gitdir:
2026-06-06T02:57:31.3423020Z [command]/usr/bin/git submodule foreach --recursive git config --local --show-origin --name-only --get-regexp remote.origin.url
2026-06-06T02:57:31.3672475Z [command]/usr/bin/git config --local http.https://github.com/.extraheader AUTHORIZATION: basic ***
2026-06-06T02:57:31.3706870Z ##[endgroup]
2026-06-06T02:57:31.3707728Z ##[group]Fetching the repository
2026-06-06T02:57:31.3715642Z [command]/usr/bin/git -c protocol.version=2 fetch --no-tags --prune --no-recurse-submodules --depth=1 origin +37e4f2dcb7d074096e15c4fe2b2ce43a0f7dc983:refs/remotes/origin/main
2026-06-06T02:57:32.9527345Z From https://github.com/qingfenglongyi/lf-media-player
2026-06-06T02:57:32.9530403Z  * [new ref]         37e4f2dcb7d074096e15c4fe2b2ce43a0f7dc983 -> origin/main
2026-06-06T02:57:32.9564347Z ##[endgroup]
2026-06-06T02:57:32.9565995Z ##[group]Determining the checkout info
2026-06-06T02:57:32.9572892Z ##[endgroup]
2026-06-06T02:57:32.9579474Z [command]/usr/bin/git sparse-checkout disable
2026-06-06T02:57:32.9615875Z [command]/usr/bin/git config --local --unset-all extensions.worktreeConfig
2026-06-06T02:57:32.9643233Z ##[group]Checking out the ref
2026-06-06T02:57:32.9647349Z [command]/usr/bin/git checkout --progress --force -B main refs/remotes/origin/main
2026-06-06T02:57:32.9972232Z Switched to a new branch 'main'
2026-06-06T02:57:32.9973905Z branch 'main' set up to track 'origin/main'.
2026-06-06T02:57:32.9981941Z ##[endgroup]
2026-06-06T02:57:33.0020401Z [command]/usr/bin/git log -1 --format=%H
2026-06-06T02:57:33.0043572Z 37e4f2dcb7d074096e15c4fe2b2ce43a0f7dc983
2026-06-06T02:57:33.0432637Z ##[group]Run actions/setup-java@v4
2026-06-06T02:57:33.0433784Z with:
2026-06-06T02:57:33.0434574Z   distribution: temurin
2026-06-06T02:57:33.0435478Z   java-version: 17
2026-06-06T02:57:33.0436345Z   java-package: jdk
2026-06-06T02:57:33.0437222Z   check-latest: false
2026-06-06T02:57:33.0438489Z   server-id: github
2026-06-06T02:57:33.0439362Z   server-username: GITHUB_ACTOR
2026-06-06T02:57:33.0440404Z   server-password: GITHUB_TOKEN
2026-06-06T02:57:33.0441438Z   overwrite-settings: true
2026-06-06T02:57:33.0515208Z   job-status: success
2026-06-06T02:57:33.0527494Z   token: ***
2026-06-06T02:57:33.0528482Z ##[endgroup]
2026-06-06T02:57:33.2516946Z ##[group]Installed distributions
2026-06-06T02:57:33.2570574Z Resolved Java 17.0.19+10 from tool-cache
2026-06-06T02:57:33.2571952Z Setting Java 17.0.19+10 as the default
2026-06-06T02:57:33.2585091Z Creating toolchains.xml for JDK version 17 from temurin
2026-06-06T02:57:33.2656549Z Writing to /home/runner/.m2/toolchains.xml
2026-06-06T02:57:33.2658007Z 
2026-06-06T02:57:33.2658753Z Java configuration:
2026-06-06T02:57:33.2660086Z   Distribution: temurin
2026-06-06T02:57:33.2661485Z   Version: 17.0.19+10
2026-06-06T02:57:33.2666761Z   Path: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-06T02:57:33.2679561Z 
2026-06-06T02:57:33.2681097Z ##[endgroup]
2026-06-06T02:57:33.2703812Z Creating settings.xml with server-id: github
2026-06-06T02:57:33.2705274Z Writing to /home/runner/.m2/settings.xml
2026-06-06T02:57:33.2845734Z ##[group]Run chmod +x ./gradlew
2026-06-06T02:57:33.2846852Z ␛[36;1mchmod +x ./gradlew␛[0m
2026-06-06T02:57:33.2847996Z ␛[36;1m./gradlew wrapper --gradle-version 8.4␛[0m
2026-06-06T02:57:33.2884637Z shell: /usr/bin/bash -e {0}
2026-06-06T02:57:33.2885643Z env:
2026-06-06T02:57:33.2886835Z   JAVA_HOME: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-06T02:57:33.2889117Z   JAVA_HOME_17_X64: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-06T02:57:33.2890740Z ##[endgroup]
2026-06-06T02:57:33.3027786Z sed: -e expression #1, char 8: unterminated `s' command
2026-06-06T02:57:33.3046003Z echo: write error: Broken pipe
2026-06-06T02:57:33.3056066Z echo: write error: Broken pipe
2026-06-06T02:57:33.3061926Z ./gradlew: 197: s~.*~\&'~ ; : not found
2026-06-06T02:57:35.1505985Z Downloading https://services.gradle.org/distributions/gradle-8.4-bin.zip
2026-06-06T02:57:38.2553692Z ............10%............20%.............30%............40%.............50%............60%.............70%............80%.............90%............100%
2026-06-06T02:57:39.5207589Z 
2026-06-06T02:57:39.5208652Z Welcome to Gradle 8.4!
2026-06-06T02:57:39.5209380Z 
2026-06-06T02:57:39.5212240Z Here are the highlights of this release:
2026-06-06T02:57:39.5218758Z  - Compiling and testing with Java 21
2026-06-06T02:57:39.5219413Z  - Faster Java compilation on Windows
2026-06-06T02:57:39.5220722Z  - Role focused dependency configurations creation
2026-06-06T02:57:39.5221126Z 
2026-06-06T02:57:39.5222653Z For more details see https://docs.gradle.org/8.4/release-notes.html
2026-06-06T02:57:39.5223084Z 
2026-06-06T02:57:39.7198178Z Starting a Gradle Daemon (subsequent builds will be faster)
2026-06-06T02:58:41.1269883Z > Task :wrapper
2026-06-06T02:58:41.1289507Z 
2026-06-06T02:58:41.1335016Z BUILD SUCCESSFUL in 1m 6s
2026-06-06T02:58:41.1342540Z 1 actionable task: 1 executed
2026-06-06T02:58:41.5136103Z ##[group]Run ./gradlew assembleDebug
2026-06-06T02:58:41.5136441Z ␛[36;1m./gradlew assembleDebug␛[0m
2026-06-06T02:58:41.5164376Z shell: /usr/bin/bash -e {0}
2026-06-06T02:58:41.5164629Z env:
2026-06-06T02:58:41.5164948Z   JAVA_HOME: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-06T02:58:41.5165465Z   JAVA_HOME_17_X64: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-06T02:58:41.5165841Z ##[endgroup]
2026-06-06T02:58:43.3799621Z > Task :app:preBuild UP-TO-DATE
2026-06-06T02:58:43.3812707Z > Task :app:preDebugBuild UP-TO-DATE
2026-06-06T02:58:43.3813706Z > Task :app:mergeDebugNativeDebugMetadata NO-SOURCE
2026-06-06T02:58:43.3814545Z > Task :app:checkKotlinGradlePluginConfigurationErrors
2026-06-06T02:58:55.6879029Z 
2026-06-06T02:58:55.6907229Z > Task :app:checkDebugAarMetadata
2026-06-06T02:58:55.6989381Z WARNING: [Processor] Library '/home/runner/.gradle/caches/modules-2/files-2.1/androidx.media3/media3-session/1.2.0/56908a8e279d1309f131b639d8c323a7b7d75034/media3-session-1.2.0.aar' contains references to both AndroidX and old support library. This seems like the library is partially migrated. Jetifier will try to rewrite the library anyway.
2026-06-06T02:58:55.6993386Z  Example of androidX reference: 'androidx/media3/session/MediaBrowserImplLegacy$1'
2026-06-06T02:58:55.7029212Z  Example of support library reference: 'android/support/v4/media/MediaBrowserCompat$MediaItem'
2026-06-06T02:58:55.7070764Z WARNING: [Processor] Library '/home/runner/.gradle/caches/modules-2/files-2.1/androidx.media3/media3-ui/1.2.0/65a0bff9352c8805515d6bcbee6d24e19a6c3d06/media3-ui-1.2.0.aar' contains references to both AndroidX and old support library. This seems like the library is partially migrated. Jetifier will try to rewrite the library anyway.
2026-06-06T02:58:55.7119201Z  Example of androidX reference: 'androidx/media3/ui/PlayerNotificationManager'
2026-06-06T02:58:55.7182864Z  Example of support library reference: 'android/support/v4/media/session/MediaSessionCompat$Token'
2026-06-06T02:59:00.2799091Z 
2026-06-06T02:59:00.2819293Z > Task :app:generateDebugResValues
2026-06-06T02:59:00.4770478Z > Task :app:mapDebugSourceSetPaths
2026-06-06T02:59:00.4773122Z > Task :app:generateDebugResources
2026-06-06T02:59:01.6789458Z > Task :app:packageDebugResources
2026-06-06T02:59:02.3770124Z > Task :app:mergeDebugResources
2026-06-06T02:59:02.8780550Z > Task :app:createDebugCompatibleScreenManifests
2026-06-06T02:59:02.8825518Z > Task :app:extractDeepLinksDebug
2026-06-06T02:59:02.9773421Z > Task :app:parseDebugLocalResources
2026-06-06T02:59:03.2776087Z > Task :app:processDebugMainManifest
2026-06-06T02:59:03.2789421Z > Task :app:processDebugManifest
2026-06-06T02:59:05.4788901Z > Task :app:javaPreCompileDebug
2026-06-06T02:59:05.4790056Z > Task :app:mergeDebugShaders
2026-06-06T02:59:05.4790787Z > Task :app:compileDebugShaders NO-SOURCE
2026-06-06T02:59:05.4824669Z > Task :app:generateDebugAssets UP-TO-DATE
2026-06-06T02:59:05.5802380Z > Task :app:mergeDebugAssets
2026-06-06T02:59:05.5809985Z > Task :app:compressDebugAssets
2026-06-06T02:59:05.5810414Z > Task :app:desugarDebugFileDependencies
2026-06-06T02:59:07.3775627Z > Task :app:processDebugManifestForPackage
2026-06-06T02:59:07.3779812Z > Task :app:checkDebugDuplicateClasses
2026-06-06T02:59:37.3769930Z > Task :app:processDebugResources
2026-06-06T02:59:38.8799347Z > Task :app:mergeExtDexDebug
2026-06-06T02:59:58.2787523Z > Task :app:mergeDebugJniLibFolders
2026-06-06T02:59:58.2813372Z > Task :app:mergeLibDexDebug
2026-06-06T02:59:58.3817089Z > Task :app:mergeDebugNativeLibs NO-SOURCE
2026-06-06T02:59:58.3865265Z > Task :app:stripDebugDebugSymbols NO-SOURCE
2026-06-06T02:59:59.1769324Z > Task :app:validateSigningDebug
2026-06-06T02:59:59.2776908Z > Task :app:writeDebugAppMetadata
2026-06-06T02:59:59.2789171Z > Task :app:writeDebugSigningConfigVersions
2026-06-06T03:00:04.0779596Z > Task :app:kspDebugKotlin
2026-06-06T03:00:14.0792463Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:611:13 Cannot find a parameter with this name: onSetMusicDirectory
2026-06-06T03:00:14.0793719Z 
2026-06-06T03:00:14.0803951Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/ui/PlayerScreen.kt:193:29 Type mismatch: inferred type is Long but Int was expected
2026-06-06T03:00:14.0842846Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/ui/PlaylistPanel.kt:603:39 Type mismatch: inferred type is (Song) -> Unit but (Long) -> Unit was expected
2026-06-06T03:00:14.0844326Z > Task :app:compileDebugKotlin FAILED
2026-06-06T03:00:14.0864260Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/ui/PlaylistPanel.kt:603:41 Expected parameter of type Long
2026-06-06T03:00:14.0865307Z 27 actionable tasks: 27 executed
2026-06-06T03:00:14.0866608Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/ui/PlaylistPanel.kt:615:39 Type mismatch: inferred type is (Song) -> Unit but (Long) -> Unit was expected
2026-06-06T03:00:14.0868948Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/ui/PlaylistPanel.kt:615:41 Expected parameter of type Long
2026-06-06T03:00:14.0883751Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/util/LrcParser.kt:98:40 Unresolved reference: context
2026-06-06T03:00:14.0884651Z 
2026-06-06T03:00:14.0884812Z FAILURE: Build failed with an exception.
2026-06-06T03:00:14.0885103Z 
2026-06-06T03:00:14.0885216Z * What went wrong:
2026-06-06T03:00:14.0885550Z Execution failed for task ':app:compileDebugKotlin'.
2026-06-06T03:00:14.0886563Z > A failure occurred while executing org.jetbrains.kotlin.compilerRunner.GradleCompilerRunnerWithWorkers$GradleKotlinCompilerWorkAction
2026-06-06T03:00:14.0887595Z    > Compilation error. See log for more details
2026-06-06T03:00:14.0887912Z 
2026-06-06T03:00:14.0888028Z * Try:
2026-06-06T03:00:14.0888539Z > Run with --stacktrace option to get the stack trace.
2026-06-06T03:00:14.0889081Z > Run with --info or --debug option to get more log output.
2026-06-06T03:00:14.0889558Z > Run with --scan to get full insights.
2026-06-06T03:00:14.0889993Z > Get more help at https://help.gradle.org.
2026-06-06T03:00:14.0890278Z 
2026-06-06T03:00:14.0890398Z BUILD FAILED in 1m 32s
2026-06-06T03:00:14.2277550Z ##[error]Process completed with exit code 1.
2026-06-06T03:00:14.2392614Z Post job cleanup.
2026-06-06T03:00:14.4374496Z Post job cleanup.
2026-06-06T03:00:14.5340623Z [command]/usr/bin/git version
2026-06-06T03:00:14.5377377Z git version 2.54.0
2026-06-06T03:00:14.5420539Z Temporarily overriding HOME='/home/runner/work/_temp/99460868-1e88-44fc-bae8-872f49e1d0dd' before making global git config changes
2026-06-06T03:00:14.5421832Z Adding repository directory to the temporary git global config as a safe directory
2026-06-06T03:00:14.5427149Z [command]/usr/bin/git config --global --add safe.directory /home/runner/work/lf-media-player/lf-media-player
2026-06-06T03:00:14.5468623Z [command]/usr/bin/git config --local --name-only --get-regexp core\.sshCommand
2026-06-06T03:00:14.5500725Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'core\.sshCommand' && git config --local --unset-all 'core.sshCommand' || :"
2026-06-06T03:00:14.5734839Z [command]/usr/bin/git config --local --name-only --get-regexp http\.https\:\/\/github\.com\/\.extraheader
2026-06-06T03:00:14.5759825Z http.https://github.com/.extraheader
2026-06-06T03:00:14.5772932Z [command]/usr/bin/git config --local --unset-all http.https://github.com/.extraheader
2026-06-06T03:00:14.5804355Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'http\.https\:\/\/github\.com\/\.extraheader' && git config --local --unset-all 'http.https://github.com/.extraheader' || :"
2026-06-06T03:00:14.6035994Z [command]/usr/bin/git config --local --name-only --get-regexp ^includeIf\.gitdir:
2026-06-06T03:00:14.6066622Z [command]/usr/bin/git submodule foreach --recursive git config --local --show-origin --name-only --get-regexp remote.origin.url
2026-06-06T03:00:14.6417729Z Cleaning up orphan processes
2026-06-06T03:00:14.6745157Z Terminate orphan process: pid (2397) (java)
2026-06-06T03:00:14.6801495Z Terminate orphan process: pid (2678) (java)
2026-06-06T03:00:14.6818228Z ##[warning]Node.js 20 actions are deprecated. The following actions are running on Node.js 20 and may not work as expected: actions/checkout@v4, actions/setup-java@v4. Actions will be forced to run with Node.js 24 by default starting June 16th, 2026. Node.js 20 will be removed from the runner on September 16th, 2026. Please check if updated versions of these actions are available that support Node.js 24. To opt into Node.js 24 now, set the FORCE_JAVASCRIPT_ACTIONS_TO_NODE24=true environment variable on the runner or in your workflow file. Once Node.js 24 becomes the default, you can temporarily opt out by setting ACTIONS_ALLOW_USE_UNSECURE_NODE_VERSION=true. For more information see: https://github.blog/changelog/2025-09-19-deprecation-of-node-20-on-github-actions-runners/
