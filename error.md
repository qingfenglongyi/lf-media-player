2026-06-06T05:20:16.2266450Z Current runner version: '2.334.0'
2026-06-06T05:20:16.2291987Z ##[group]Runner Image Provisioner
2026-06-06T05:20:16.2293306Z Hosted Compute Agent
2026-06-06T05:20:16.2293974Z Version: 20260520.533
2026-06-06T05:20:16.2294685Z Commit: 189110e25284a9812c124fd27b339e2fb4f2f9db
2026-06-06T05:20:16.2295472Z Build Date: 2026-05-20T17:44:04Z
2026-06-06T05:20:16.2296190Z Worker ID: {dd11fdef-e79b-49a1-a1b7-fca04d8e4997}
2026-06-06T05:20:16.2296984Z Azure Region: westus
2026-06-06T05:20:16.2297600Z ##[endgroup]
2026-06-06T05:20:16.2299149Z ##[group]Operating System
2026-06-06T05:20:16.2300041Z Ubuntu
2026-06-06T05:20:16.2300600Z 24.04.4
2026-06-06T05:20:16.2301182Z LTS
2026-06-06T05:20:16.2301740Z ##[endgroup]
2026-06-06T05:20:16.2302825Z ##[group]Runner Image
2026-06-06T05:20:16.2303658Z Image: ubuntu-24.04
2026-06-06T05:20:16.2304273Z Version: 20260525.161.1
2026-06-06T05:20:16.2305593Z Included Software: https://github.com/actions/runner-images/blob/ubuntu24/20260525.161/images/ubuntu/Ubuntu2404-Readme.md
2026-06-06T05:20:16.2307239Z Image Release: https://github.com/actions/runner-images/releases/tag/ubuntu24%2F20260525.161
2026-06-06T05:20:16.2308270Z ##[endgroup]
2026-06-06T05:20:16.2309548Z ##[group]GITHUB_TOKEN Permissions
2026-06-06T05:20:16.2311543Z Contents: read
2026-06-06T05:20:16.2312627Z Metadata: read
2026-06-06T05:20:16.2313317Z Packages: read
2026-06-06T05:20:16.2313926Z ##[endgroup]
2026-06-06T05:20:16.2316416Z Secret source: Actions
2026-06-06T05:20:16.2317319Z Prepare workflow directory
2026-06-06T05:20:16.2721391Z Prepare all required actions
2026-06-06T05:20:16.2765796Z Getting action download info
2026-06-06T05:20:16.7849623Z Download action repository 'actions/checkout@v4' (SHA:34e114876b0b11c390a56381ad16ebd13914f8d5)
2026-06-06T05:20:16.9265854Z Download action repository 'actions/setup-java@v4' (SHA:c1e323688fd81a25caa38c78aa6df2d33d3e20d9)
2026-06-06T05:20:17.4877467Z Download action repository 'actions/upload-artifact@v4' (SHA:ea165f8d65b6e75b540449e92b4886f43607fa02)
2026-06-06T05:20:17.7104937Z Complete job name: build
2026-06-06T05:20:17.7871582Z ##[group]Run actions/checkout@v4
2026-06-06T05:20:17.7872889Z with:
2026-06-06T05:20:17.7873414Z   repository: qingfenglongyi/lf-media-player
2026-06-06T05:20:17.7877892Z   token: ***
2026-06-06T05:20:17.7878350Z   ssh-strict: true
2026-06-06T05:20:17.7878810Z   ssh-user: git
2026-06-06T05:20:17.7879272Z   persist-credentials: true
2026-06-06T05:20:17.7879881Z   clean: true
2026-06-06T05:20:17.7880361Z   sparse-checkout-cone-mode: true
2026-06-06T05:20:17.7880918Z   fetch-depth: 1
2026-06-06T05:20:17.7881367Z   fetch-tags: false
2026-06-06T05:20:17.7881837Z   show-progress: true
2026-06-06T05:20:17.7882527Z   lfs: false
2026-06-06T05:20:17.7882968Z   submodules: false
2026-06-06T05:20:17.7883435Z   set-safe-directory: true
2026-06-06T05:20:17.7884609Z ##[endgroup]
2026-06-06T05:20:17.9099534Z Syncing repository: qingfenglongyi/lf-media-player
2026-06-06T05:20:17.9103003Z ##[group]Getting Git version info
2026-06-06T05:20:17.9104544Z Working directory is '/home/runner/work/lf-media-player/lf-media-player'
2026-06-06T05:20:17.9106673Z [command]/usr/bin/git version
2026-06-06T05:20:17.9175795Z git version 2.54.0
2026-06-06T05:20:17.9205164Z ##[endgroup]
2026-06-06T05:20:17.9223156Z Temporarily overriding HOME='/home/runner/work/_temp/df4a2d59-b39e-435d-94cb-5fb1faa0e3b7' before making global git config changes
2026-06-06T05:20:17.9226276Z Adding repository directory to the temporary git global config as a safe directory
2026-06-06T05:20:17.9230684Z [command]/usr/bin/git config --global --add safe.directory /home/runner/work/lf-media-player/lf-media-player
2026-06-06T05:20:17.9279905Z Deleting the contents of '/home/runner/work/lf-media-player/lf-media-player'
2026-06-06T05:20:17.9284594Z ##[group]Initializing the repository
2026-06-06T05:20:17.9289032Z [command]/usr/bin/git init /home/runner/work/lf-media-player/lf-media-player
2026-06-06T05:20:17.9393772Z hint: Using 'master' as the name for the initial branch. This default branch name
2026-06-06T05:20:17.9395313Z hint: will change to "main" in Git 3.0. To configure the initial branch name
2026-06-06T05:20:17.9396431Z hint: to use in all of your new repositories, which will suppress this warning,
2026-06-06T05:20:17.9397240Z hint: call:
2026-06-06T05:20:17.9397671Z hint:
2026-06-06T05:20:17.9398243Z hint: 	git config --global init.defaultBranch <name>
2026-06-06T05:20:17.9398916Z hint:
2026-06-06T05:20:17.9399542Z hint: Names commonly chosen instead of 'master' are 'main', 'trunk' and
2026-06-06T05:20:17.9400555Z hint: 'development'. The just-created branch can be renamed via this command:
2026-06-06T05:20:17.9401360Z hint:
2026-06-06T05:20:17.9401816Z hint: 	git branch -m <name>
2026-06-06T05:20:17.9402535Z hint:
2026-06-06T05:20:17.9403195Z hint: Disable this message with "git config set advice.defaultBranchName false"
2026-06-06T05:20:17.9410961Z Initialized empty Git repository in /home/runner/work/lf-media-player/lf-media-player/.git/
2026-06-06T05:20:17.9423207Z [command]/usr/bin/git remote add origin https://github.com/qingfenglongyi/lf-media-player
2026-06-06T05:20:17.9463991Z ##[endgroup]
2026-06-06T05:20:17.9464839Z ##[group]Disabling automatic garbage collection
2026-06-06T05:20:17.9468612Z [command]/usr/bin/git config --local gc.auto 0
2026-06-06T05:20:17.9504276Z ##[endgroup]
2026-06-06T05:20:17.9505061Z ##[group]Setting up auth
2026-06-06T05:20:17.9511994Z [command]/usr/bin/git config --local --name-only --get-regexp core\.sshCommand
2026-06-06T05:20:17.9548530Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'core\.sshCommand' && git config --local --unset-all 'core.sshCommand' || :"
2026-06-06T05:20:17.9886910Z [command]/usr/bin/git config --local --name-only --get-regexp http\.https\:\/\/github\.com\/\.extraheader
2026-06-06T05:20:17.9918269Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'http\.https\:\/\/github\.com\/\.extraheader' && git config --local --unset-all 'http.https://github.com/.extraheader' || :"
2026-06-06T05:20:18.0145565Z [command]/usr/bin/git config --local --name-only --get-regexp ^includeIf\.gitdir:
2026-06-06T05:20:18.0185412Z [command]/usr/bin/git submodule foreach --recursive git config --local --show-origin --name-only --get-regexp remote.origin.url
2026-06-06T05:20:18.0417564Z [command]/usr/bin/git config --local http.https://github.com/.extraheader AUTHORIZATION: basic ***
2026-06-06T05:20:18.0453619Z ##[endgroup]
2026-06-06T05:20:18.0454616Z ##[group]Fetching the repository
2026-06-06T05:20:18.0463159Z [command]/usr/bin/git -c protocol.version=2 fetch --no-tags --prune --no-recurse-submodules --depth=1 origin +4f52eed3e05d784d5c2b1da91ae4f92d7fa7d6d6:refs/remotes/origin/main
2026-06-06T05:20:19.1025504Z From https://github.com/qingfenglongyi/lf-media-player
2026-06-06T05:20:19.1029062Z  * [new ref]         4f52eed3e05d784d5c2b1da91ae4f92d7fa7d6d6 -> origin/main
2026-06-06T05:20:19.1064579Z ##[endgroup]
2026-06-06T05:20:19.1066694Z ##[group]Determining the checkout info
2026-06-06T05:20:19.1069012Z ##[endgroup]
2026-06-06T05:20:19.1073145Z [command]/usr/bin/git sparse-checkout disable
2026-06-06T05:20:19.1118377Z [command]/usr/bin/git config --local --unset-all extensions.worktreeConfig
2026-06-06T05:20:19.1147358Z ##[group]Checking out the ref
2026-06-06T05:20:19.1150681Z [command]/usr/bin/git checkout --progress --force -B main refs/remotes/origin/main
2026-06-06T05:20:19.1463759Z Switched to a new branch 'main'
2026-06-06T05:20:19.1465850Z branch 'main' set up to track 'origin/main'.
2026-06-06T05:20:19.1473940Z ##[endgroup]
2026-06-06T05:20:19.1514940Z [command]/usr/bin/git log -1 --format=%H
2026-06-06T05:20:19.1538241Z 4f52eed3e05d784d5c2b1da91ae4f92d7fa7d6d6
2026-06-06T05:20:19.1959161Z ##[group]Run actions/setup-java@v4
2026-06-06T05:20:19.1960380Z with:
2026-06-06T05:20:19.1961254Z   distribution: temurin
2026-06-06T05:20:19.1962436Z   java-version: 17
2026-06-06T05:20:19.1963405Z   java-package: jdk
2026-06-06T05:20:19.1964363Z   check-latest: false
2026-06-06T05:20:19.1965567Z   server-id: github
2026-06-06T05:20:19.1966564Z   server-username: GITHUB_ACTOR
2026-06-06T05:20:19.2035394Z   server-password: GITHUB_TOKEN
2026-06-06T05:20:19.2036602Z   overwrite-settings: true
2026-06-06T05:20:19.2037693Z   job-status: success
2026-06-06T05:20:19.2049905Z   token: ***
2026-06-06T05:20:19.2050799Z ##[endgroup]
2026-06-06T05:20:19.4173457Z ##[group]Installed distributions
2026-06-06T05:20:19.4328494Z Resolved Java 17.0.19+10 from tool-cache
2026-06-06T05:20:19.4330732Z Setting Java 17.0.19+10 as the default
2026-06-06T05:20:19.4345920Z Creating toolchains.xml for JDK version 17 from temurin
2026-06-06T05:20:19.4428240Z Writing to /home/runner/.m2/toolchains.xml
2026-06-06T05:20:19.4430060Z 
2026-06-06T05:20:19.4430996Z Java configuration:
2026-06-06T05:20:19.4432914Z   Distribution: temurin
2026-06-06T05:20:19.4434560Z   Version: 17.0.19+10
2026-06-06T05:20:19.4436814Z   Path: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-06T05:20:19.4439094Z 
2026-06-06T05:20:19.4441147Z ##[endgroup]
2026-06-06T05:20:19.4459904Z Creating settings.xml with server-id: github
2026-06-06T05:20:19.4469633Z Writing to /home/runner/.m2/settings.xml
2026-06-06T05:20:19.4641821Z ##[group]Run chmod +x ./gradlew
2026-06-06T05:20:19.4643396Z ␛[36;1mchmod +x ./gradlew␛[0m
2026-06-06T05:20:19.4644611Z ␛[36;1m./gradlew wrapper --gradle-version 8.4␛[0m
2026-06-06T05:20:19.4683699Z shell: /usr/bin/bash -e {0}
2026-06-06T05:20:19.4684755Z env:
2026-06-06T05:20:19.4686013Z   JAVA_HOME: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-06T05:20:19.4688177Z   JAVA_HOME_17_X64: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-06T05:20:19.4689832Z ##[endgroup]
2026-06-06T05:20:19.4831580Z sed: -e expression #1, char 8: unterminated `s' command
2026-06-06T05:20:19.4879564Z echo: write error: Broken pipe
2026-06-06T05:20:19.4891037Z echo: write error: Broken pipe
2026-06-06T05:20:19.4896744Z ./gradlew: 197: s~.*~\&'~ ; : not found
2026-06-06T05:20:20.6572103Z Downloading https://services.gradle.org/distributions/gradle-8.4-bin.zip
2026-06-06T05:20:23.1809022Z ............10%............20%.............30%............40%.............50%............60%.............70%............80%.............90%............100%
2026-06-06T05:20:24.3585722Z 
2026-06-06T05:20:24.3586551Z Welcome to Gradle 8.4!
2026-06-06T05:20:24.3587367Z 
2026-06-06T05:20:24.3589930Z Here are the highlights of this release:
2026-06-06T05:20:24.3596367Z  - Compiling and testing with Java 21
2026-06-06T05:20:24.3597638Z  - Faster Java compilation on Windows
2026-06-06T05:20:24.3598809Z  - Role focused dependency configurations creation
2026-06-06T05:20:24.3599254Z 
2026-06-06T05:20:24.3600773Z For more details see https://docs.gradle.org/8.4/release-notes.html
2026-06-06T05:20:24.3601321Z 
2026-06-06T05:20:24.5599553Z Starting a Gradle Daemon (subsequent builds will be faster)
2026-06-06T05:21:21.8597404Z > Task :wrapper
2026-06-06T05:21:21.9490353Z 
2026-06-06T05:21:21.9495450Z BUILD SUCCESSFUL in 1m 1s
2026-06-06T05:21:21.9498792Z 1 actionable task: 1 executed
2026-06-06T05:21:22.2816550Z ##[group]Run ./gradlew assembleDebug
2026-06-06T05:21:22.2816917Z ␛[36;1m./gradlew assembleDebug␛[0m
2026-06-06T05:21:22.2843091Z shell: /usr/bin/bash -e {0}
2026-06-06T05:21:22.2843355Z env:
2026-06-06T05:21:22.2843677Z   JAVA_HOME: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-06T05:21:22.2844201Z   JAVA_HOME_17_X64: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-06T05:21:22.2844593Z ##[endgroup]
2026-06-06T05:21:24.2880105Z > Task :app:preBuild UP-TO-DATE
2026-06-06T05:21:24.2880831Z > Task :app:preDebugBuild UP-TO-DATE
2026-06-06T05:21:24.2881464Z > Task :app:mergeDebugNativeDebugMetadata NO-SOURCE
2026-06-06T05:21:24.2882787Z > Task :app:checkKotlinGradlePluginConfigurationErrors
2026-06-06T05:21:31.6013052Z 
2026-06-06T05:21:31.6071963Z > Task :app:checkDebugAarMetadata
2026-06-06T05:21:31.6075201Z WARNING: [Processor] Library '/home/runner/.gradle/caches/modules-2/files-2.1/androidx.media3/media3-session/1.2.0/56908a8e279d1309f131b639d8c323a7b7d75034/media3-session-1.2.0.aar' contains references to both AndroidX and old support library. This seems like the library is partially migrated. Jetifier will try to rewrite the library anyway.
2026-06-06T05:21:31.6133313Z  Example of androidX reference: 'androidx/media3/session/MediaBrowserImplLegacy$1'
2026-06-06T05:21:31.6163248Z  Example of support library reference: 'android/support/v4/media/MediaBrowserCompat$MediaItem'
2026-06-06T05:21:31.6180462Z WARNING: [Processor] Library '/home/runner/.gradle/caches/modules-2/files-2.1/androidx.media3/media3-ui/1.2.0/65a0bff9352c8805515d6bcbee6d24e19a6c3d06/media3-ui-1.2.0.aar' contains references to both AndroidX and old support library. This seems like the library is partially migrated. Jetifier will try to rewrite the library anyway.
2026-06-06T05:21:31.6202513Z  Example of androidX reference: 'androidx/media3/ui/PlayerNotificationManager'
2026-06-06T05:21:31.6228369Z  Example of support library reference: 'android/support/v4/media/session/MediaSessionCompat$Token'
2026-06-06T05:21:36.1861232Z 
2026-06-06T05:21:36.1878677Z > Task :app:generateDebugResValues
2026-06-06T05:21:36.2856253Z > Task :app:mapDebugSourceSetPaths
2026-06-06T05:21:36.2859144Z > Task :app:generateDebugResources
2026-06-06T05:21:37.3850806Z > Task :app:packageDebugResources
2026-06-06T05:21:38.2858210Z > Task :app:mergeDebugResources
2026-06-06T05:21:38.2893337Z > Task :app:createDebugCompatibleScreenManifests
2026-06-06T05:21:38.2921687Z > Task :app:extractDeepLinksDebug
2026-06-06T05:21:38.4876732Z > Task :app:parseDebugLocalResources
2026-06-06T05:21:38.6853421Z > Task :app:processDebugMainManifest
2026-06-06T05:21:38.7850043Z > Task :app:processDebugManifest
2026-06-06T05:21:40.1853309Z > Task :app:javaPreCompileDebug
2026-06-06T05:21:40.2850321Z > Task :app:mergeDebugShaders
2026-06-06T05:21:40.2868612Z > Task :app:compileDebugShaders NO-SOURCE
2026-06-06T05:21:40.2869399Z > Task :app:generateDebugAssets UP-TO-DATE
2026-06-06T05:21:40.3853276Z > Task :app:mergeDebugAssets
2026-06-06T05:21:40.3902913Z > Task :app:compressDebugAssets
2026-06-06T05:21:40.3933071Z > Task :app:desugarDebugFileDependencies
2026-06-06T05:21:42.2865818Z > Task :app:processDebugManifestForPackage
2026-06-06T05:21:42.3849975Z > Task :app:checkDebugDuplicateClasses
2026-06-06T05:22:12.2861484Z > Task :app:processDebugResources
2026-06-06T05:22:13.4853283Z > Task :app:mergeExtDexDebug
2026-06-06T05:22:36.6882918Z > Task :app:mergeDebugJniLibFolders
2026-06-06T05:22:36.6904530Z > Task :app:mergeLibDexDebug
2026-06-06T05:22:36.7849768Z > Task :app:mergeDebugNativeLibs NO-SOURCE
2026-06-06T05:22:36.7864234Z > Task :app:stripDebugDebugSymbols NO-SOURCE
2026-06-06T05:22:37.8853457Z > Task :app:validateSigningDebug
2026-06-06T05:22:37.8862711Z > Task :app:writeDebugAppMetadata
2026-06-06T05:22:37.8891576Z > Task :app:writeDebugSigningConfigVersions
2026-06-06T05:22:42.8873356Z > Task :app:kspDebugKotlin
2026-06-06T05:22:53.0854999Z 
2026-06-06T05:22:53.0870070Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:96:64 Unresolved reference: asSharedFlow
2026-06-06T05:22:53.0871432Z > Task :app:compileDebugKotlin FAILED
2026-06-06T05:22:53.0873587Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:277:45 Cannot infer a type for this parameter. Please specify it explicitly.
2026-06-06T05:22:53.0875770Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:279:47 Unresolved reference: it
2026-06-06T05:22:53.0877642Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:280:60 Unresolved reference: it
2026-06-06T05:22:53.0879486Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:281:41 Unresolved reference: it
2026-06-06T05:22:53.0881310Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:284:96 Unresolved reference: it
2026-06-06T05:22:53.1872859Z 
2026-06-06T05:22:53.1873356Z 27 actionable tasks: 27 executed
2026-06-06T05:22:53.1882863Z FAILURE: Build failed with an exception.
2026-06-06T05:22:53.1885547Z 
2026-06-06T05:22:53.1949751Z * What went wrong:
2026-06-06T05:22:53.1982760Z Execution failed for task ':app:compileDebugKotlin'.
2026-06-06T05:22:53.2019187Z > A failure occurred while executing org.jetbrains.kotlin.compilerRunner.GradleCompilerRunnerWithWorkers$GradleKotlinCompilerWorkAction
2026-06-06T05:22:53.2073193Z    > Compilation error. See log for more details
2026-06-06T05:22:53.2074538Z 
2026-06-06T05:22:53.2074909Z * Try:
2026-06-06T05:22:53.2075561Z > Run with --stacktrace option to get the stack trace.
2026-06-06T05:22:53.2076485Z > Run with --info or --debug option to get more log output.
2026-06-06T05:22:53.2077366Z > Run with --scan to get full insights.
2026-06-06T05:22:53.2078145Z > Get more help at https://help.gradle.org.
2026-06-06T05:22:53.2078721Z 
2026-06-06T05:22:53.2079054Z BUILD FAILED in 1m 30s
2026-06-06T05:22:53.2806386Z ##[error]Process completed with exit code 1.
2026-06-06T05:22:53.2988874Z Post job cleanup.
2026-06-06T05:22:53.5280709Z Post job cleanup.
2026-06-06T05:22:53.6284107Z [command]/usr/bin/git version
2026-06-06T05:22:53.6322862Z git version 2.54.0
2026-06-06T05:22:53.6368990Z Temporarily overriding HOME='/home/runner/work/_temp/ecd40fae-0304-4443-9d53-bf4c30114720' before making global git config changes
2026-06-06T05:22:53.6370200Z Adding repository directory to the temporary git global config as a safe directory
2026-06-06T05:22:53.6375363Z [command]/usr/bin/git config --global --add safe.directory /home/runner/work/lf-media-player/lf-media-player
2026-06-06T05:22:53.6418591Z [command]/usr/bin/git config --local --name-only --get-regexp core\.sshCommand
2026-06-06T05:22:53.6458830Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'core\.sshCommand' && git config --local --unset-all 'core.sshCommand' || :"
2026-06-06T05:22:53.6687935Z [command]/usr/bin/git config --local --name-only --get-regexp http\.https\:\/\/github\.com\/\.extraheader
2026-06-06T05:22:53.6712553Z http.https://github.com/.extraheader
2026-06-06T05:22:53.6725207Z [command]/usr/bin/git config --local --unset-all http.https://github.com/.extraheader
2026-06-06T05:22:53.6756194Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'http\.https\:\/\/github\.com\/\.extraheader' && git config --local --unset-all 'http.https://github.com/.extraheader' || :"
2026-06-06T05:22:53.6983963Z [command]/usr/bin/git config --local --name-only --get-regexp ^includeIf\.gitdir:
2026-06-06T05:22:53.7015779Z [command]/usr/bin/git submodule foreach --recursive git config --local --show-origin --name-only --get-regexp remote.origin.url
2026-06-06T05:22:53.7370542Z Cleaning up orphan processes
2026-06-06T05:22:53.7693759Z Terminate orphan process: pid (2390) (java)
2026-06-06T05:22:53.7755889Z Terminate orphan process: pid (2672) (java)
2026-06-06T05:22:53.7798584Z ##[warning]Node.js 20 actions are deprecated. The following actions are running on Node.js 20 and may not work as expected: actions/checkout@v4, actions/setup-java@v4. Actions will be forced to run with Node.js 24 by default starting June 16th, 2026. Node.js 20 will be removed from the runner on September 16th, 2026. Please check if updated versions of these actions are available that support Node.js 24. To opt into Node.js 24 now, set the FORCE_JAVASCRIPT_ACTIONS_TO_NODE24=true environment variable on the runner or in your workflow file. Once Node.js 24 becomes the default, you can temporarily opt out by setting ACTIONS_ALLOW_USE_UNSECURE_NODE_VERSION=true. For more information see: https://github.blog/changelog/2025-09-19-deprecation-of-node-20-on-github-actions-runners/
