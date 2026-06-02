2026-06-02T09:32:37.3687542Z Current runner version: '2.334.0'
2026-06-02T09:32:37.3714548Z ##[group]Runner Image Provisioner
2026-06-02T09:32:37.3715537Z Hosted Compute Agent
2026-06-02T09:32:37.3716128Z Version: 20260520.533
2026-06-02T09:32:37.3716943Z Commit: 189110e25284a9812c124fd27b339e2fb4f2f9db
2026-06-02T09:32:37.3717674Z Build Date: 2026-05-20T17:44:04Z
2026-06-02T09:32:37.3718422Z Worker ID: {15dd65e0-c5d0-4bfe-aed0-60a9446c31b2}
2026-06-02T09:32:37.3719191Z Azure Region: eastus2
2026-06-02T09:32:37.3719779Z ##[endgroup]
2026-06-02T09:32:37.3721272Z ##[group]Operating System
2026-06-02T09:32:37.3721920Z Ubuntu
2026-06-02T09:32:37.3722442Z 24.04.4
2026-06-02T09:32:37.3723111Z LTS
2026-06-02T09:32:37.3723660Z ##[endgroup]
2026-06-02T09:32:37.3724469Z ##[group]Runner Image
2026-06-02T09:32:37.3725095Z Image: ubuntu-24.04
2026-06-02T09:32:37.3725672Z Version: 20260525.161.1
2026-06-02T09:32:37.3727053Z Included Software: https://github.com/actions/runner-images/blob/ubuntu24/20260525.161/images/ubuntu/Ubuntu2404-Readme.md
2026-06-02T09:32:37.3728671Z Image Release: https://github.com/actions/runner-images/releases/tag/ubuntu24%2F20260525.161
2026-06-02T09:32:37.3729657Z ##[endgroup]
2026-06-02T09:32:37.3730889Z ##[group]GITHUB_TOKEN Permissions
2026-06-02T09:32:37.3732919Z Contents: read
2026-06-02T09:32:37.3733532Z Metadata: read
2026-06-02T09:32:37.3734615Z Packages: read
2026-06-02T09:32:37.3735195Z ##[endgroup]
2026-06-02T09:32:37.3737616Z Secret source: Actions
2026-06-02T09:32:37.3738413Z Prepare workflow directory
2026-06-02T09:32:37.4080348Z Prepare all required actions
2026-06-02T09:32:37.4119782Z Getting action download info
2026-06-02T09:32:37.9433800Z Download action repository 'actions/checkout@v4' (SHA:34e114876b0b11c390a56381ad16ebd13914f8d5)
2026-06-02T09:32:38.0339032Z Download action repository 'actions/setup-java@v4' (SHA:c1e323688fd81a25caa38c78aa6df2d33d3e20d9)
2026-06-02T09:32:38.3487332Z Download action repository 'actions/upload-artifact@v4' (SHA:ea165f8d65b6e75b540449e92b4886f43607fa02)
2026-06-02T09:32:38.5681533Z Complete job name: build
2026-06-02T09:32:38.6411347Z ##[group]Run actions/checkout@v4
2026-06-02T09:32:38.6412320Z with:
2026-06-02T09:32:38.6412845Z   repository: qingfenglongyi/lf-media-player
2026-06-02T09:32:38.6417810Z   token: ***
2026-06-02T09:32:38.6418306Z   ssh-strict: true
2026-06-02T09:32:38.6418784Z   ssh-user: git
2026-06-02T09:32:38.6419280Z   persist-credentials: true
2026-06-02T09:32:38.6419826Z   clean: true
2026-06-02T09:32:38.6420343Z   sparse-checkout-cone-mode: true
2026-06-02T09:32:38.6420915Z   fetch-depth: 1
2026-06-02T09:32:38.6421391Z   fetch-tags: false
2026-06-02T09:32:38.6421899Z   show-progress: true
2026-06-02T09:32:38.6422390Z   lfs: false
2026-06-02T09:32:38.6422850Z   submodules: false
2026-06-02T09:32:38.6423345Z   set-safe-directory: true
2026-06-02T09:32:38.6424255Z ##[endgroup]
2026-06-02T09:32:38.7584766Z Syncing repository: qingfenglongyi/lf-media-player
2026-06-02T09:32:38.7586877Z ##[group]Getting Git version info
2026-06-02T09:32:38.7587855Z Working directory is '/home/runner/work/lf-media-player/lf-media-player'
2026-06-02T09:32:38.7589058Z [command]/usr/bin/git version
2026-06-02T09:32:38.7638994Z git version 2.54.0
2026-06-02T09:32:38.7664318Z ##[endgroup]
2026-06-02T09:32:38.7687584Z Temporarily overriding HOME='/home/runner/work/_temp/90a67a07-cb9d-4129-9de2-0e9e8eeb2862' before making global git config changes
2026-06-02T09:32:38.7692465Z Adding repository directory to the temporary git global config as a safe directory
2026-06-02T09:32:38.7693863Z [command]/usr/bin/git config --global --add safe.directory /home/runner/work/lf-media-player/lf-media-player
2026-06-02T09:32:38.7736772Z Deleting the contents of '/home/runner/work/lf-media-player/lf-media-player'
2026-06-02T09:32:38.7740016Z ##[group]Initializing the repository
2026-06-02T09:32:38.7744259Z [command]/usr/bin/git init /home/runner/work/lf-media-player/lf-media-player
2026-06-02T09:32:38.7827309Z hint: Using 'master' as the name for the initial branch. This default branch name
2026-06-02T09:32:38.7829560Z hint: will change to "main" in Git 3.0. To configure the initial branch name
2026-06-02T09:32:38.7831230Z hint: to use in all of your new repositories, which will suppress this warning,
2026-06-02T09:32:38.7832291Z hint: call:
2026-06-02T09:32:38.7832747Z hint:
2026-06-02T09:32:38.7833674Z hint: 	git config --global init.defaultBranch <name>
2026-06-02T09:32:38.7834809Z hint:
2026-06-02T09:32:38.7835833Z hint: Names commonly chosen instead of 'master' are 'main', 'trunk' and
2026-06-02T09:32:38.7836864Z hint: 'development'. The just-created branch can be renamed via this command:
2026-06-02T09:32:38.7837661Z hint:
2026-06-02T09:32:38.7838120Z hint: 	git branch -m <name>
2026-06-02T09:32:38.7838639Z hint:
2026-06-02T09:32:38.7839317Z hint: Disable this message with "git config set advice.defaultBranchName false"
2026-06-02T09:32:38.7840526Z Initialized empty Git repository in /home/runner/work/lf-media-player/lf-media-player/.git/
2026-06-02T09:32:38.7849807Z [command]/usr/bin/git remote add origin https://github.com/qingfenglongyi/lf-media-player
2026-06-02T09:32:38.7886529Z ##[endgroup]
2026-06-02T09:32:38.7887406Z ##[group]Disabling automatic garbage collection
2026-06-02T09:32:38.7890553Z [command]/usr/bin/git config --local gc.auto 0
2026-06-02T09:32:38.7920708Z ##[endgroup]
2026-06-02T09:32:38.7921537Z ##[group]Setting up auth
2026-06-02T09:32:38.7927893Z [command]/usr/bin/git config --local --name-only --get-regexp core\.sshCommand
2026-06-02T09:32:38.7960448Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'core\.sshCommand' && git config --local --unset-all 'core.sshCommand' || :"
2026-06-02T09:32:38.8280406Z [command]/usr/bin/git config --local --name-only --get-regexp http\.https\:\/\/github\.com\/\.extraheader
2026-06-02T09:32:38.8311001Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'http\.https\:\/\/github\.com\/\.extraheader' && git config --local --unset-all 'http.https://github.com/.extraheader' || :"
2026-06-02T09:32:38.8538902Z [command]/usr/bin/git config --local --name-only --get-regexp ^includeIf\.gitdir:
2026-06-02T09:32:38.8570865Z [command]/usr/bin/git submodule foreach --recursive git config --local --show-origin --name-only --get-regexp remote.origin.url
2026-06-02T09:32:38.8801227Z [command]/usr/bin/git config --local http.https://github.com/.extraheader AUTHORIZATION: basic ***
2026-06-02T09:32:38.8836193Z ##[endgroup]
2026-06-02T09:32:38.8837251Z ##[group]Fetching the repository
2026-06-02T09:32:38.8844971Z [command]/usr/bin/git -c protocol.version=2 fetch --no-tags --prune --no-recurse-submodules --depth=1 origin +51f1299d00d78d68239bfaf1aedd6551ac21448b:refs/remotes/origin/main
2026-06-02T09:32:39.2126113Z From https://github.com/qingfenglongyi/lf-media-player
2026-06-02T09:32:39.2128783Z  * [new ref]         51f1299d00d78d68239bfaf1aedd6551ac21448b -> origin/main
2026-06-02T09:32:39.2159311Z ##[endgroup]
2026-06-02T09:32:39.2160971Z ##[group]Determining the checkout info
2026-06-02T09:32:39.2162824Z ##[endgroup]
2026-06-02T09:32:39.2166392Z [command]/usr/bin/git sparse-checkout disable
2026-06-02T09:32:39.2208593Z [command]/usr/bin/git config --local --unset-all extensions.worktreeConfig
2026-06-02T09:32:39.2236866Z ##[group]Checking out the ref
2026-06-02T09:32:39.2240306Z [command]/usr/bin/git checkout --progress --force -B main refs/remotes/origin/main
2026-06-02T09:32:39.2327810Z Switched to a new branch 'main'
2026-06-02T09:32:39.2330488Z branch 'main' set up to track 'origin/main'.
2026-06-02T09:32:39.2371617Z ##[endgroup]
2026-06-02T09:32:39.2374717Z [command]/usr/bin/git log -1 --format=%H
2026-06-02T09:32:39.2397348Z 51f1299d00d78d68239bfaf1aedd6551ac21448b
2026-06-02T09:32:39.2787108Z ##[group]Run actions/setup-java@v4
2026-06-02T09:32:39.2788305Z with:
2026-06-02T09:32:39.2789172Z   distribution: temurin
2026-06-02T09:32:39.2790165Z   java-version: 17
2026-06-02T09:32:39.2791094Z   java-package: jdk
2026-06-02T09:32:39.2792042Z   check-latest: false
2026-06-02T09:32:39.2793216Z   server-id: github
2026-06-02T09:32:39.2794478Z   server-username: GITHUB_ACTOR
2026-06-02T09:32:39.2795636Z   server-password: GITHUB_TOKEN
2026-06-02T09:32:39.2796766Z   overwrite-settings: true
2026-06-02T09:32:39.2797817Z   job-status: success
2026-06-02T09:32:39.2808377Z   token: ***
2026-06-02T09:32:39.2809267Z ##[endgroup]
2026-06-02T09:32:39.4924033Z ##[group]Installed distributions
2026-06-02T09:32:39.4975136Z Resolved Java 17.0.19+10 from tool-cache
2026-06-02T09:32:39.4977541Z Setting Java 17.0.19+10 as the default
2026-06-02T09:32:39.4988989Z Creating toolchains.xml for JDK version 17 from temurin
2026-06-02T09:32:39.5061641Z Writing to /home/runner/.m2/toolchains.xml
2026-06-02T09:32:39.5063291Z 
2026-06-02T09:32:39.5064235Z Java configuration:
2026-06-02T09:32:39.5066033Z   Distribution: temurin
2026-06-02T09:32:39.5067855Z   Version: 17.0.19+10
2026-06-02T09:32:39.5070792Z   Path: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-02T09:32:39.5072503Z 
2026-06-02T09:32:39.5074398Z ##[endgroup]
2026-06-02T09:32:39.5097211Z Creating settings.xml with server-id: github
2026-06-02T09:32:39.5098823Z Writing to /home/runner/.m2/settings.xml
2026-06-02T09:32:39.5251945Z ##[group]Run chmod +x ./gradlew
2026-06-02T09:32:39.5253179Z [36;1mchmod +x ./gradlew[0m
2026-06-02T09:32:39.5254760Z [36;1m./gradlew wrapper --gradle-version 8.4[0m
2026-06-02T09:32:39.5289769Z shell: /usr/bin/bash -e {0}
2026-06-02T09:32:39.5290886Z env:
2026-06-02T09:32:39.5292167Z   JAVA_HOME: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-02T09:32:39.5294469Z   JAVA_HOME_17_X64: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-02T09:32:39.5296130Z ##[endgroup]
2026-06-02T09:32:39.5435332Z sed: -e expression #1, char 8: unterminated `s' command
2026-06-02T09:32:39.5447769Z echo: write error: Broken pipe
2026-06-02T09:32:39.5458035Z echo: write error: Broken pipe
2026-06-02T09:32:39.5463657Z ./gradlew: 197: s~.*~\&'~ ; : not found
2026-06-02T09:32:39.6843069Z Downloading https://services.gradle.org/distributions/gradle-8.4-bin.zip
2026-06-02T09:32:41.5329531Z ............10%............20%.............30%............40%.............50%............60%.............70%............80%.............90%............100%
2026-06-02T09:32:42.6542653Z 
2026-06-02T09:32:42.6565534Z Welcome to Gradle 8.4!
2026-06-02T09:32:42.6577056Z 
2026-06-02T09:32:42.6577782Z Here are the highlights of this release:
2026-06-02T09:32:42.6582871Z  - Compiling and testing with Java 21
2026-06-02T09:32:42.6583809Z  - Faster Java compilation on Windows
2026-06-02T09:32:42.6584938Z  - Role focused dependency configurations creation
2026-06-02T09:32:42.6614551Z 
2026-06-02T09:32:42.6618709Z For more details see https://docs.gradle.org/8.4/release-notes.html
2026-06-02T09:32:42.6619647Z 
2026-06-02T09:32:42.7529925Z Starting a Gradle Daemon (subsequent builds will be faster)
2026-06-02T09:33:38.5550201Z > Task :wrapper
2026-06-02T09:33:38.6209920Z 
2026-06-02T09:33:38.6220312Z BUILD SUCCESSFUL in 58s
2026-06-02T09:33:38.6222427Z 1 actionable task: 1 executed
2026-06-02T09:33:38.9495179Z ##[group]Run ./gradlew assembleDebug
2026-06-02T09:33:38.9495590Z [36;1m./gradlew assembleDebug[0m
2026-06-02T09:33:38.9525523Z shell: /usr/bin/bash -e {0}
2026-06-02T09:33:38.9525808Z env:
2026-06-02T09:33:38.9526151Z   JAVA_HOME: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-02T09:33:38.9526695Z   JAVA_HOME_17_X64: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-02T09:33:38.9527120Z ##[endgroup]
2026-06-02T09:33:40.7138072Z > Task :app:preBuild UP-TO-DATE
2026-06-02T09:33:40.7139064Z > Task :app:preDebugBuild UP-TO-DATE
2026-06-02T09:33:40.7154141Z > Task :app:mergeDebugNativeDebugMetadata NO-SOURCE
2026-06-02T09:33:40.7155061Z > Task :app:checkKotlinGradlePluginConfigurationErrors
2026-06-02T09:33:46.7164646Z 
2026-06-02T09:33:46.7193213Z > Task :app:checkDebugAarMetadata
2026-06-02T09:33:46.7247121Z WARNING: [Processor] Library '/home/runner/.gradle/caches/modules-2/files-2.1/androidx.media3/media3-ui/1.2.0/65a0bff9352c8805515d6bcbee6d24e19a6c3d06/media3-ui-1.2.0.aar' contains references to both AndroidX and old support library. This seems like the library is partially migrated. Jetifier will try to rewrite the library anyway.
2026-06-02T09:33:46.7297011Z  Example of androidX reference: 'androidx/media3/ui/PlayerNotificationManager'
2026-06-02T09:33:46.7316961Z  Example of support library reference: 'android/support/v4/media/session/MediaSessionCompat$Token'
2026-06-02T09:33:46.7386383Z WARNING: [Processor] Library '/home/runner/.gradle/caches/modules-2/files-2.1/androidx.media3/media3-session/1.2.0/56908a8e279d1309f131b639d8c323a7b7d75034/media3-session-1.2.0.aar' contains references to both AndroidX and old support library. This seems like the library is partially migrated. Jetifier will try to rewrite the library anyway.
2026-06-02T09:33:46.7388835Z  Example of androidX reference: 'androidx/media3/session/MediaBrowserImplLegacy$1'
2026-06-02T09:33:46.7390221Z  Example of support library reference: 'android/support/v4/media/MediaBrowserCompat$MediaItem'
2026-06-02T09:33:51.1115964Z 
2026-06-02T09:33:51.1145182Z > Task :app:generateDebugResValues
2026-06-02T09:33:51.2115135Z > Task :app:mapDebugSourceSetPaths
2026-06-02T09:33:51.2134918Z > Task :app:generateDebugResources
2026-06-02T09:33:52.4123554Z > Task :app:packageDebugResources
2026-06-02T09:33:53.0125108Z > Task :app:createDebugCompatibleScreenManifests
2026-06-02T09:33:53.0154722Z > Task :app:extractDeepLinksDebug
2026-06-02T09:33:53.1115271Z > Task :app:parseDebugLocalResources
2026-06-02T09:33:53.2118586Z > Task :app:mergeDebugResources
2026-06-02T09:33:53.4114666Z > Task :app:processDebugMainManifest
2026-06-02T09:33:53.5113412Z > Task :app:processDebugManifest
2026-06-02T09:33:55.1121899Z > Task :app:javaPreCompileDebug
2026-06-02T09:33:55.1144690Z > Task :app:mergeDebugShaders
2026-06-02T09:33:55.1154748Z > Task :app:compileDebugShaders NO-SOURCE
2026-06-02T09:33:55.1156151Z > Task :app:generateDebugAssets UP-TO-DATE
2026-06-02T09:33:55.2113830Z > Task :app:mergeDebugAssets
2026-06-02T09:33:55.2119776Z > Task :app:compressDebugAssets
2026-06-02T09:33:55.2120534Z > Task :app:desugarDebugFileDependencies
2026-06-02T09:33:56.8116765Z > Task :app:mergeDebugJniLibFolders
2026-06-02T09:33:56.8145750Z > Task :app:checkDebugDuplicateClasses
2026-06-02T09:33:56.9144569Z > Task :app:mergeDebugNativeLibs NO-SOURCE
2026-06-02T09:34:23.5125070Z > Task :app:processDebugManifestForPackage
2026-06-02T09:34:26.9125269Z > Task :app:mergeExtDexDebug
2026-06-02T09:34:36.7115121Z > Task :app:stripDebugDebugSymbols NO-SOURCE
2026-06-02T09:34:36.7154849Z > Task :app:mergeLibDexDebug
2026-06-02T09:34:38.6136567Z > Task :app:validateSigningDebug
2026-06-02T09:34:38.6148583Z > Task :app:writeDebugAppMetadata
2026-06-02T09:34:38.7155259Z > Task :app:writeDebugSigningConfigVersions
2026-06-02T09:34:41.2124893Z > Task :app:processDebugResources
2026-06-02T09:34:53.3135224Z > Task :app:kspDebugKotlin
2026-06-02T09:35:00.6126747Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/MainActivity.kt:240:53 Smart cast to 'Song' is impossible, because 'currentSong' is a property that has open or custom getter
2026-06-02T09:35:00.6128073Z 
2026-06-02T09:35:00.6165763Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/ui/PlaylistPanel.kt:139:41 Val cannot be reassigned
2026-06-02T09:35:00.6166953Z > Task :app:compileDebugKotlin FAILED
2026-06-02T09:35:00.6225682Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/ui/PlaylistPanel.kt:369:29 Unresolved reference: songToAdd
2026-06-02T09:35:00.6229545Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/ui/PlaylistPanel.kt:370:29 Unresolved reference: showAddToPlaylistDialog
2026-06-02T09:35:00.7117466Z 
2026-06-02T09:35:00.7121325Z FAILURE: Build failed with an exception.
2026-06-02T09:35:00.7164489Z 
2026-06-02T09:35:00.7165421Z 27 actionable tasks: 27 executed
2026-06-02T09:35:00.7174875Z * What went wrong:
2026-06-02T09:35:00.7175538Z Execution failed for task ':app:compileDebugKotlin'.
2026-06-02T09:35:00.7176950Z > A failure occurred while executing org.jetbrains.kotlin.compilerRunner.GradleCompilerRunnerWithWorkers$GradleKotlinCompilerWorkAction
2026-06-02T09:35:00.7178370Z    > Compilation error. See log for more details
2026-06-02T09:35:00.7178908Z 
2026-06-02T09:35:00.7214701Z * Try:
2026-06-02T09:35:00.7215707Z > Run with --stacktrace option to get the stack trace.
2026-06-02T09:35:00.7216616Z > Run with --info or --debug option to get more log output.
2026-06-02T09:35:00.7217516Z > Run with --scan to get full insights.
2026-06-02T09:35:00.7218276Z > Get more help at https://help.gradle.org.
2026-06-02T09:35:00.7254541Z 
2026-06-02T09:35:00.7254843Z BUILD FAILED in 1m 21s
2026-06-02T09:35:00.7973819Z ##[error]Process completed with exit code 1.
2026-06-02T09:35:00.8100153Z Post job cleanup.
2026-06-02T09:35:01.0182248Z Post job cleanup.
2026-06-02T09:35:01.1221049Z [command]/usr/bin/git version
2026-06-02T09:35:01.1265957Z git version 2.54.0
2026-06-02T09:35:01.1313150Z Temporarily overriding HOME='/home/runner/work/_temp/d167278b-0cc0-4301-839d-b38283a2969e' before making global git config changes
2026-06-02T09:35:01.1314911Z Adding repository directory to the temporary git global config as a safe directory
2026-06-02T09:35:01.1321248Z [command]/usr/bin/git config --global --add safe.directory /home/runner/work/lf-media-player/lf-media-player
2026-06-02T09:35:01.1373024Z [command]/usr/bin/git config --local --name-only --get-regexp core\.sshCommand
2026-06-02T09:35:01.1414650Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'core\.sshCommand' && git config --local --unset-all 'core.sshCommand' || :"
2026-06-02T09:35:01.1677426Z [command]/usr/bin/git config --local --name-only --get-regexp http\.https\:\/\/github\.com\/\.extraheader
2026-06-02T09:35:01.1706902Z http.https://github.com/.extraheader
2026-06-02T09:35:01.1723827Z [command]/usr/bin/git config --local --unset-all http.https://github.com/.extraheader
2026-06-02T09:35:01.1763289Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'http\.https\:\/\/github\.com\/\.extraheader' && git config --local --unset-all 'http.https://github.com/.extraheader' || :"
2026-06-02T09:35:01.2023088Z [command]/usr/bin/git config --local --name-only --get-regexp ^includeIf\.gitdir:
2026-06-02T09:35:01.2083456Z [command]/usr/bin/git submodule foreach --recursive git config --local --show-origin --name-only --get-regexp remote.origin.url
2026-06-02T09:35:01.2725433Z Cleaning up orphan processes
2026-06-02T09:35:01.3114712Z Terminate orphan process: pid (2327) (java)
2026-06-02T09:35:01.3182049Z Terminate orphan process: pid (2610) (java)
2026-06-02T09:35:01.3205791Z ##[warning]Node.js 20 actions are deprecated. The following actions are running on Node.js 20 and may not work as expected: actions/checkout@v4, actions/setup-java@v4. Actions will be forced to run with Node.js 24 by default starting June 16th, 2026. Node.js 20 will be removed from the runner on September 16th, 2026. Please check if updated versions of these actions are available that support Node.js 24. To opt into Node.js 24 now, set the FORCE_JAVASCRIPT_ACTIONS_TO_NODE24=true environment variable on the runner or in your workflow file. Once Node.js 24 becomes the default, you can temporarily opt out by setting ACTIONS_ALLOW_USE_UNSECURE_NODE_VERSION=true. For more information see: https://github.blog/changelog/2025-09-19-deprecation-of-node-20-on-github-actions-runners/