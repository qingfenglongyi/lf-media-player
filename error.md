2026-06-06T01:51:08.3771256Z Current runner version: '2.334.0'
2026-06-06T01:51:08.3790811Z ##[group]Runner Image Provisioner
2026-06-06T01:51:08.3791532Z Hosted Compute Agent
2026-06-06T01:51:08.3792079Z Version: 20260520.533
2026-06-06T01:51:08.3792737Z Commit: 189110e25284a9812c124fd27b339e2fb4f2f9db
2026-06-06T01:51:08.3793330Z Build Date: 2026-05-20T17:44:04Z
2026-06-06T01:51:08.3793940Z Worker ID: {dd1a7927-f2eb-4092-aced-113d0980e2a0}
2026-06-06T01:51:08.3794523Z Azure Region: westcentralus
2026-06-06T01:51:08.3795029Z ##[endgroup]
2026-06-06T01:51:08.3796128Z ##[group]Operating System
2026-06-06T01:51:08.3796655Z Ubuntu
2026-06-06T01:51:08.3797137Z 24.04.4
2026-06-06T01:51:08.3797556Z LTS
2026-06-06T01:51:08.3797957Z ##[endgroup]
2026-06-06T01:51:08.3798525Z ##[group]Runner Image
2026-06-06T01:51:08.3799086Z Image: ubuntu-24.04
2026-06-06T01:51:08.3799610Z Version: 20260525.161.1
2026-06-06T01:51:08.3800573Z Included Software: https://github.com/actions/runner-images/blob/ubuntu24/20260525.161/images/ubuntu/Ubuntu2404-Readme.md
2026-06-06T01:51:08.3801837Z Image Release: https://github.com/actions/runner-images/releases/tag/ubuntu24%2F20260525.161
2026-06-06T01:51:08.3802772Z ##[endgroup]
2026-06-06T01:51:08.3803774Z ##[group]GITHUB_TOKEN Permissions
2026-06-06T01:51:08.3805260Z Contents: read
2026-06-06T01:51:08.3805820Z Metadata: read
2026-06-06T01:51:08.3806307Z Packages: read
2026-06-06T01:51:08.3806725Z ##[endgroup]
2026-06-06T01:51:08.3808829Z Secret source: Actions
2026-06-06T01:51:08.3809434Z Prepare workflow directory
2026-06-06T01:51:08.5405237Z Prepare all required actions
2026-06-06T01:51:08.5448084Z Getting action download info
2026-06-06T01:51:08.9370853Z Download action repository 'actions/checkout@v4' (SHA:34e114876b0b11c390a56381ad16ebd13914f8d5)
2026-06-06T01:51:09.3230034Z Download action repository 'actions/setup-java@v4' (SHA:c1e323688fd81a25caa38c78aa6df2d33d3e20d9)
2026-06-06T01:51:10.2586636Z Download action repository 'actions/upload-artifact@v4' (SHA:ea165f8d65b6e75b540449e92b4886f43607fa02)
2026-06-06T01:51:10.4577891Z Complete job name: build
2026-06-06T01:51:10.5147932Z ##[group]Run actions/checkout@v4
2026-06-06T01:51:10.5148635Z with:
2026-06-06T01:51:10.5149045Z   repository: qingfenglongyi/lf-media-player
2026-06-06T01:51:10.5153059Z   token: ***
2026-06-06T01:51:10.5153437Z   ssh-strict: true
2026-06-06T01:51:10.5153812Z   ssh-user: git
2026-06-06T01:51:10.5154194Z   persist-credentials: true
2026-06-06T01:51:10.5154602Z   clean: true
2026-06-06T01:51:10.5154978Z   sparse-checkout-cone-mode: true
2026-06-06T01:51:10.5155415Z   fetch-depth: 1
2026-06-06T01:51:10.5155778Z   fetch-tags: false
2026-06-06T01:51:10.5156158Z   show-progress: true
2026-06-06T01:51:10.5156544Z   lfs: false
2026-06-06T01:51:10.5156908Z   submodules: false
2026-06-06T01:51:10.5157285Z   set-safe-directory: true
2026-06-06T01:51:10.5157859Z ##[endgroup]
2026-06-06T01:51:10.6049784Z Syncing repository: qingfenglongyi/lf-media-player
2026-06-06T01:51:10.6051581Z ##[group]Getting Git version info
2026-06-06T01:51:10.6052636Z Working directory is '/home/runner/work/lf-media-player/lf-media-player'
2026-06-06T01:51:10.6053606Z [command]/usr/bin/git version
2026-06-06T01:51:10.7719666Z git version 2.54.0
2026-06-06T01:51:10.7739467Z ##[endgroup]
2026-06-06T01:51:10.7759496Z Temporarily overriding HOME='/home/runner/work/_temp/463ec12d-9fda-45a3-b5a4-bc9f80e6d1d5' before making global git config changes
2026-06-06T01:51:10.7761496Z Adding repository directory to the temporary git global config as a safe directory
2026-06-06T01:51:10.7764808Z [command]/usr/bin/git config --global --add safe.directory /home/runner/work/lf-media-player/lf-media-player
2026-06-06T01:51:10.7794964Z Deleting the contents of '/home/runner/work/lf-media-player/lf-media-player'
2026-06-06T01:51:10.7797755Z ##[group]Initializing the repository
2026-06-06T01:51:10.7801582Z [command]/usr/bin/git init /home/runner/work/lf-media-player/lf-media-player
2026-06-06T01:51:10.7884656Z hint: Using 'master' as the name for the initial branch. This default branch name
2026-06-06T01:51:10.7886590Z hint: will change to "main" in Git 3.0. To configure the initial branch name
2026-06-06T01:51:10.7887943Z hint: to use in all of your new repositories, which will suppress this warning,
2026-06-06T01:51:10.7888992Z hint: call:
2026-06-06T01:51:10.7889579Z hint:
2026-06-06T01:51:10.7890339Z hint: 	git config --global init.defaultBranch <name>
2026-06-06T01:51:10.7891246Z hint:
2026-06-06T01:51:10.7892080Z hint: Names commonly chosen instead of 'master' are 'main', 'trunk' and
2026-06-06T01:51:10.7893586Z hint: 'development'. The just-created branch can be renamed via this command:
2026-06-06T01:51:10.7894682Z hint:
2026-06-06T01:51:10.7895291Z hint: 	git branch -m <name>
2026-06-06T01:51:10.7896000Z hint:
2026-06-06T01:51:10.7896923Z hint: Disable this message with "git config set advice.defaultBranchName false"
2026-06-06T01:51:10.7898416Z Initialized empty Git repository in /home/runner/work/lf-media-player/lf-media-player/.git/
2026-06-06T01:51:10.7900990Z [command]/usr/bin/git remote add origin https://github.com/qingfenglongyi/lf-media-player
2026-06-06T01:51:10.7922016Z ##[endgroup]
2026-06-06T01:51:10.7923332Z ##[group]Disabling automatic garbage collection
2026-06-06T01:51:10.7925933Z [command]/usr/bin/git config --local gc.auto 0
2026-06-06T01:51:10.7949527Z ##[endgroup]
2026-06-06T01:51:10.7950565Z ##[group]Setting up auth
2026-06-06T01:51:10.7955589Z [command]/usr/bin/git config --local --name-only --get-regexp core\.sshCommand
2026-06-06T01:51:10.7980565Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'core\.sshCommand' && git config --local --unset-all 'core.sshCommand' || :"
2026-06-06T01:51:11.0020033Z [command]/usr/bin/git config --local --name-only --get-regexp http\.https\:\/\/github\.com\/\.extraheader
2026-06-06T01:51:11.0048392Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'http\.https\:\/\/github\.com\/\.extraheader' && git config --local --unset-all 'http.https://github.com/.extraheader' || :"
2026-06-06T01:51:11.0245407Z [command]/usr/bin/git config --local --name-only --get-regexp ^includeIf\.gitdir:
2026-06-06T01:51:11.0271943Z [command]/usr/bin/git submodule foreach --recursive git config --local --show-origin --name-only --get-regexp remote.origin.url
2026-06-06T01:51:11.0457163Z [command]/usr/bin/git config --local http.https://github.com/.extraheader AUTHORIZATION: basic ***
2026-06-06T01:51:11.0613989Z ##[endgroup]
2026-06-06T01:51:11.0614976Z ##[group]Fetching the repository
2026-06-06T01:51:11.0621440Z [command]/usr/bin/git -c protocol.version=2 fetch --no-tags --prune --no-recurse-submodules --depth=1 origin +6877e36f1791f7002aba4788d4b3983d11e27210:refs/remotes/origin/main
2026-06-06T01:51:11.5431940Z From https://github.com/qingfenglongyi/lf-media-player
2026-06-06T01:51:11.5433749Z  * [new ref]         6877e36f1791f7002aba4788d4b3983d11e27210 -> origin/main
2026-06-06T01:51:11.5453128Z ##[endgroup]
2026-06-06T01:51:11.5454603Z ##[group]Determining the checkout info
2026-06-06T01:51:11.5456435Z ##[endgroup]
2026-06-06T01:51:11.5460393Z [command]/usr/bin/git sparse-checkout disable
2026-06-06T01:51:11.5493041Z [command]/usr/bin/git config --local --unset-all extensions.worktreeConfig
2026-06-06T01:51:11.5515407Z ##[group]Checking out the ref
2026-06-06T01:51:11.5518096Z [command]/usr/bin/git checkout --progress --force -B main refs/remotes/origin/main
2026-06-06T01:51:11.5588106Z Switched to a new branch 'main'
2026-06-06T01:51:11.5589874Z branch 'main' set up to track 'origin/main'.
2026-06-06T01:51:11.5596527Z ##[endgroup]
2026-06-06T01:51:11.5624058Z [command]/usr/bin/git log -1 --format=%H
2026-06-06T01:51:11.5641946Z 6877e36f1791f7002aba4788d4b3983d11e27210
2026-06-06T01:51:11.5945288Z ##[group]Run actions/setup-java@v4
2026-06-06T01:51:11.5946105Z with:
2026-06-06T01:51:11.5946693Z   distribution: temurin
2026-06-06T01:51:11.5947387Z   java-version: 17
2026-06-06T01:51:11.5948033Z   java-package: jdk
2026-06-06T01:51:11.5948687Z   check-latest: false
2026-06-06T01:51:11.5949546Z   server-id: github
2026-06-06T01:51:11.5950218Z   server-username: GITHUB_ACTOR
2026-06-06T01:51:11.5951018Z   server-password: GITHUB_TOKEN
2026-06-06T01:51:11.5951814Z   overwrite-settings: true
2026-06-06T01:51:11.6009015Z   job-status: success
2026-06-06T01:51:11.6017363Z   token: ***
2026-06-06T01:51:11.6017972Z ##[endgroup]
2026-06-06T01:51:11.7585723Z ##[group]Installed distributions
2026-06-06T01:51:11.7630287Z Resolved Java 17.0.19+10 from tool-cache
2026-06-06T01:51:11.7631635Z Setting Java 17.0.19+10 as the default
2026-06-06T01:51:11.7641022Z Creating toolchains.xml for JDK version 17 from temurin
2026-06-06T01:51:11.7696889Z Writing to /home/runner/.m2/toolchains.xml
2026-06-06T01:51:11.7697939Z 
2026-06-06T01:51:11.7698347Z Java configuration:
2026-06-06T01:51:11.7699342Z   Distribution: temurin
2026-06-06T01:51:11.7700337Z   Version: 17.0.19+10
2026-06-06T01:51:11.7701753Z   Path: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-06T01:51:11.7703261Z 
2026-06-06T01:51:11.7704338Z ##[endgroup]
2026-06-06T01:51:11.7719670Z Creating settings.xml with server-id: github
2026-06-06T01:51:11.7720776Z Writing to /home/runner/.m2/settings.xml
2026-06-06T01:51:11.7832999Z ##[group]Run chmod +x ./gradlew
2026-06-06T01:51:11.7833854Z [36;1mchmod +x ./gradlew[0m
2026-06-06T01:51:11.7834714Z [36;1m./gradlew wrapper --gradle-version 8.4[0m
2026-06-06T01:51:11.7864211Z shell: /usr/bin/bash -e {0}
2026-06-06T01:51:11.7864955Z env:
2026-06-06T01:51:11.7865845Z   JAVA_HOME: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-06T01:51:11.7867367Z   JAVA_HOME_17_X64: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-06T01:51:11.7868557Z ##[endgroup]
2026-06-06T01:51:11.7978639Z sed: -e expression #1, char 8: unterminated `s' command
2026-06-06T01:51:11.7991696Z echo: write error: Broken pipe
2026-06-06T01:51:11.7999574Z echo: write error: Broken pipe
2026-06-06T01:51:11.8004027Z ./gradlew: 197: s~.*~\&'~ ; : not found
2026-06-06T01:51:13.5080084Z Downloading https://services.gradle.org/distributions/gradle-8.4-bin.zip
2026-06-06T01:51:15.7518719Z ............10%............20%.............30%............40%.............50%............60%.............70%............80%.............90%............100%
2026-06-06T01:51:16.6720877Z 
2026-06-06T01:51:16.6721788Z Welcome to Gradle 8.4!
2026-06-06T01:51:16.6722069Z 
2026-06-06T01:51:16.6722751Z Here are the highlights of this release:
2026-06-06T01:51:16.6731989Z  - Compiling and testing with Java 21
2026-06-06T01:51:16.6732809Z  - Faster Java compilation on Windows
2026-06-06T01:51:16.6733715Z  - Role focused dependency configurations creation
2026-06-06T01:51:16.6734260Z 
2026-06-06T01:51:16.6734620Z For more details see https://docs.gradle.org/8.4/release-notes.html
2026-06-06T01:51:16.6735105Z 
2026-06-06T01:51:16.7721702Z Starting a Gradle Daemon (subsequent builds will be faster)
2026-06-06T01:52:10.7749954Z > Task :wrapper
2026-06-06T01:52:10.8377878Z 
2026-06-06T01:52:10.8378714Z BUILD SUCCESSFUL in 57s
2026-06-06T01:52:10.8403259Z 1 actionable task: 1 executed
2026-06-06T01:52:11.1633297Z ##[group]Run ./gradlew assembleDebug
2026-06-06T01:52:11.1633593Z [36;1m./gradlew assembleDebug[0m
2026-06-06T01:52:11.1656216Z shell: /usr/bin/bash -e {0}
2026-06-06T01:52:11.1656433Z env:
2026-06-06T01:52:11.1656685Z   JAVA_HOME: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-06T01:52:11.1657083Z   JAVA_HOME_17_X64: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-06T01:52:11.1657397Z ##[endgroup]
2026-06-06T01:52:12.6566261Z > Task :app:preBuild UP-TO-DATE
2026-06-06T01:52:12.6566934Z > Task :app:preDebugBuild UP-TO-DATE
2026-06-06T01:52:12.6573074Z > Task :app:mergeDebugNativeDebugMetadata NO-SOURCE
2026-06-06T01:52:12.6573565Z > Task :app:checkKotlinGradlePluginConfigurationErrors
2026-06-06T01:52:24.0645643Z 
2026-06-06T01:52:24.0705350Z > Task :app:checkDebugAarMetadata
2026-06-06T01:52:24.0730777Z WARNING: [Processor] Library '/home/runner/.gradle/caches/modules-2/files-2.1/androidx.media3/media3-session/1.2.0/56908a8e279d1309f131b639d8c323a7b7d75034/media3-session-1.2.0.aar' contains references to both AndroidX and old support library. This seems like the library is partially migrated. Jetifier will try to rewrite the library anyway.
2026-06-06T01:52:24.0747370Z  Example of androidX reference: 'androidx/media3/session/MediaBrowserImplLegacy$1'
2026-06-06T01:52:24.0813023Z  Example of support library reference: 'android/support/v4/media/MediaBrowserCompat$MediaItem'
2026-06-06T01:52:24.0835513Z WARNING: [Processor] Library '/home/runner/.gradle/caches/modules-2/files-2.1/androidx.media3/media3-ui/1.2.0/65a0bff9352c8805515d6bcbee6d24e19a6c3d06/media3-ui-1.2.0.aar' contains references to both AndroidX and old support library. This seems like the library is partially migrated. Jetifier will try to rewrite the library anyway.
2026-06-06T01:52:24.0872995Z  Example of androidX reference: 'androidx/media3/ui/PlayerNotificationManager'
2026-06-06T01:52:24.0930518Z  Example of support library reference: 'android/support/v4/media/session/MediaSessionCompat$Token'
2026-06-06T01:52:27.4553256Z 
2026-06-06T01:52:27.4579847Z > Task :app:generateDebugResValues
2026-06-06T01:52:27.4630326Z > Task :app:mapDebugSourceSetPaths
2026-06-06T01:52:27.4653228Z > Task :app:generateDebugResources
2026-06-06T01:52:28.9553685Z > Task :app:packageDebugResources
2026-06-06T01:52:29.0550164Z > Task :app:mergeDebugResources
2026-06-06T01:52:29.5577241Z > Task :app:createDebugCompatibleScreenManifests
2026-06-06T01:52:29.5603243Z > Task :app:extractDeepLinksDebug
2026-06-06T01:52:29.6552881Z > Task :app:parseDebugLocalResources
2026-06-06T01:52:29.8563468Z > Task :app:processDebugMainManifest
2026-06-06T01:52:29.9549415Z > Task :app:processDebugManifest
2026-06-06T01:52:32.5568590Z > Task :app:javaPreCompileDebug
2026-06-06T01:52:32.5583806Z > Task :app:mergeDebugShaders
2026-06-06T01:52:32.5584151Z > Task :app:compileDebugShaders NO-SOURCE
2026-06-06T01:52:32.5584513Z > Task :app:generateDebugAssets UP-TO-DATE
2026-06-06T01:52:32.6573361Z > Task :app:mergeDebugAssets
2026-06-06T01:52:32.6574206Z > Task :app:compressDebugAssets
2026-06-06T01:52:32.6600235Z > Task :app:desugarDebugFileDependencies
2026-06-06T01:52:34.2551600Z > Task :app:mergeDebugJniLibFolders
2026-06-06T01:52:34.3553413Z > Task :app:checkDebugDuplicateClasses
2026-06-06T01:52:34.3559334Z > Task :app:mergeDebugNativeLibs NO-SOURCE
2026-06-06T01:52:59.9549958Z > Task :app:processDebugManifestForPackage
2026-06-06T01:53:04.3563220Z > Task :app:mergeExtDexDebug
2026-06-06T01:53:09.9553204Z > Task :app:stripDebugDebugSymbols NO-SOURCE
2026-06-06T01:53:09.9563123Z > Task :app:mergeLibDexDebug
2026-06-06T01:53:10.4563116Z > Task :app:processDebugResources
2026-06-06T01:53:10.9549678Z > Task :app:validateSigningDebug
2026-06-06T01:53:16.9550240Z > Task :app:writeDebugAppMetadata
2026-06-06T01:53:16.9573157Z > Task :app:writeDebugSigningConfigVersions
2026-06-06T01:53:21.4549929Z > Task :app:kspDebugKotlin
2026-06-06T01:53:30.2571880Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:395:53 Operator '==' cannot be applied to 'Long' and 'Int'
2026-06-06T01:53:30.2572940Z 
2026-06-06T01:53:30.2595813Z > Task :app:compileDebugKotlin FAILED
2026-06-06T01:53:30.2603258Z 27 actionable tasks: 27 executed
2026-06-06T01:53:30.2623422Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:601:13 Cannot find a parameter with this name: onSetMusicDirectory
2026-06-06T01:53:30.2643753Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:613:9 Unresolved reference: directoryPickerLauncher
2026-06-06T01:53:30.2645467Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:617:36 Unresolved reference: musicDirectoryUri
2026-06-06T01:53:30.2647211Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:619:28 Unresolved reference: musicDirectoryUri
2026-06-06T01:53:30.2649411Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:620:60 Unresolved reference: musicDirectoryUri
2026-06-06T01:53:30.2651210Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:622:24 Suspend function 'getAllSongs' should be called only from a coroutine or another suspend function
2026-06-06T01:53:30.2653110Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:624:9 Unresolved reference: librarySongs
2026-06-06T01:53:30.2654700Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:625:9 Unresolved reference: playlist
2026-06-06T01:53:30.2656224Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/data/MediaStoreHelper.kt:8:25 Unresolved reference: DocumentFile
2026-06-06T01:53:30.2657853Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/data/MediaStoreHelper.kt:48:32 Unresolved reference: DocumentFile
2026-06-06T01:53:30.2659426Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/data/MediaStoreHelper.kt:63:67 Unresolved reference: DocumentFile
2026-06-06T01:53:30.2660901Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/data/MediaStoreHelper.kt:64:26 Unresolved reference: DocumentFile
2026-06-06T01:53:30.2662786Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/ui/PlayerScreen.kt:193:29 Type mismatch: inferred type is Long but Int was expected
2026-06-06T01:53:30.2664360Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/ui/PlaylistPanel.kt:603:66 Unresolved reference: id
2026-06-06T01:53:30.2665793Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/ui/PlaylistPanel.kt:615:66 Unresolved reference: id
2026-06-06T01:53:30.2667251Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/util/LrcParser.kt:7:25 Unresolved reference: DocumentFile
2026-06-06T01:53:30.2668724Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/util/LrcParser.kt:71:32 Unresolved reference: DocumentFile
2026-06-06T01:53:30.2686294Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/util/LrcParser.kt:86:47 Unresolved reference: DocumentFile
2026-06-06T01:53:30.2689907Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/util/LrcParser.kt:87:26 Unresolved reference: DocumentFile
2026-06-06T01:53:30.2690828Z 
2026-06-06T01:53:30.2691075Z FAILURE: Build failed with an exception.
2026-06-06T01:53:30.2691447Z 
2026-06-06T01:53:30.2691962Z * What went wrong:
2026-06-06T01:53:30.2692540Z Execution failed for task ':app:compileDebugKotlin'.
2026-06-06T01:53:30.2693559Z > A failure occurred while executing org.jetbrains.kotlin.compilerRunner.GradleCompilerRunnerWithWorkers$GradleKotlinCompilerWorkAction
2026-06-06T01:53:30.2694534Z    > Compilation error. See log for more details
2026-06-06T01:53:30.2694911Z 
2026-06-06T01:53:30.2695118Z * Try:
2026-06-06T01:53:30.2695527Z > Run with --stacktrace option to get the stack trace.
2026-06-06T01:53:30.2696093Z > Run with --info or --debug option to get more log output.
2026-06-06T01:53:30.2696624Z > Run with --scan to get full insights.
2026-06-06T01:53:30.2697108Z > Get more help at https://help.gradle.org.
2026-06-06T01:53:30.2697475Z 
2026-06-06T01:53:30.2697694Z BUILD FAILED in 1m 19s
2026-06-06T01:53:30.4654036Z ##[error]Process completed with exit code 1.
2026-06-06T01:53:30.4790556Z Post job cleanup.
2026-06-06T01:53:30.6253236Z Post job cleanup.
2026-06-06T01:53:30.7008975Z [command]/usr/bin/git version
2026-06-06T01:53:30.7038219Z git version 2.54.0
2026-06-06T01:53:30.7071441Z Temporarily overriding HOME='/home/runner/work/_temp/0869166b-5610-45ec-8bd4-1e7da9d30bbd' before making global git config changes
2026-06-06T01:53:30.7072595Z Adding repository directory to the temporary git global config as a safe directory
2026-06-06T01:53:30.7077047Z [command]/usr/bin/git config --global --add safe.directory /home/runner/work/lf-media-player/lf-media-player
2026-06-06T01:53:30.7106051Z [command]/usr/bin/git config --local --name-only --get-regexp core\.sshCommand
2026-06-06T01:53:30.7132362Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'core\.sshCommand' && git config --local --unset-all 'core.sshCommand' || :"
2026-06-06T01:53:30.7317611Z [command]/usr/bin/git config --local --name-only --get-regexp http\.https\:\/\/github\.com\/\.extraheader
2026-06-06T01:53:30.7334756Z http.https://github.com/.extraheader
2026-06-06T01:53:30.7344235Z [command]/usr/bin/git config --local --unset-all http.https://github.com/.extraheader
2026-06-06T01:53:30.7368567Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'http\.https\:\/\/github\.com\/\.extraheader' && git config --local --unset-all 'http.https://github.com/.extraheader' || :"
2026-06-06T01:53:30.7551876Z [command]/usr/bin/git config --local --name-only --get-regexp ^includeIf\.gitdir:
2026-06-06T01:53:30.7576972Z [command]/usr/bin/git submodule foreach --recursive git config --local --show-origin --name-only --get-regexp remote.origin.url
2026-06-06T01:53:30.7883286Z Cleaning up orphan processes
2026-06-06T01:53:30.8124613Z Terminate orphan process: pid (2104) (java)
2026-06-06T01:53:30.8182928Z Terminate orphan process: pid (2384) (java)
2026-06-06T01:53:30.8196967Z ##[warning]Node.js 20 actions are deprecated. The following actions are running on Node.js 20 and may not work as expected: actions/checkout@v4, actions/setup-java@v4. Actions will be forced to run with Node.js 24 by default starting June 16th, 2026. Node.js 20 will be removed from the runner on September 16th, 2026. Please check if updated versions of these actions are available that support Node.js 24. To opt into Node.js 24 now, set the FORCE_JAVASCRIPT_ACTIONS_TO_NODE24=true environment variable on the runner or in your workflow file. Once Node.js 24 becomes the default, you can temporarily opt out by setting ACTIONS_ALLOW_USE_UNSECURE_NODE_VERSION=true. For more information see: https://github.blog/changelog/2025-09-19-deprecation-of-node-20-on-github-actions-runners/