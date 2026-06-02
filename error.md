2026-06-02T09:49:20.0589323Z Current runner version: '2.334.0'
2026-06-02T09:49:20.0622949Z ##[group]Runner Image Provisioner
2026-06-02T09:49:20.0624333Z Hosted Compute Agent
2026-06-02T09:49:20.0625264Z Version: 20260520.533
2026-06-02T09:49:20.0626337Z Commit: 189110e25284a9812c124fd27b339e2fb4f2f9db
2026-06-02T09:49:20.0627502Z Build Date: 2026-05-20T17:44:04Z
2026-06-02T09:49:20.0628826Z Worker ID: {ec0a9360-1051-47f9-86ac-f7ad5c53df71}
2026-06-02T09:49:20.0630094Z Azure Region: eastus
2026-06-02T09:49:20.0630951Z ##[endgroup]
2026-06-02T09:49:20.0633138Z ##[group]Operating System
2026-06-02T09:49:20.0634115Z Ubuntu
2026-06-02T09:49:20.0634940Z 24.04.4
2026-06-02T09:49:20.0635823Z LTS
2026-06-02T09:49:20.0636657Z ##[endgroup]
2026-06-02T09:49:20.0637558Z ##[group]Runner Image
2026-06-02T09:49:20.0638720Z Image: ubuntu-24.04
2026-06-02T09:49:20.0639655Z Version: 20260525.161.1
2026-06-02T09:49:20.0641773Z Included Software: https://github.com/actions/runner-images/blob/ubuntu24/20260525.161/images/ubuntu/Ubuntu2404-Readme.md
2026-06-02T09:49:20.0644648Z Image Release: https://github.com/actions/runner-images/releases/tag/ubuntu24%2F20260525.161
2026-06-02T09:49:20.0646198Z ##[endgroup]
2026-06-02T09:49:20.0648272Z ##[group]GITHUB_TOKEN Permissions
2026-06-02T09:49:20.0651239Z Contents: read
2026-06-02T09:49:20.0652580Z Metadata: read
2026-06-02T09:49:20.0653456Z Packages: read
2026-06-02T09:49:20.0654373Z ##[endgroup]
2026-06-02T09:49:20.0657730Z Secret source: Actions
2026-06-02T09:49:20.0659318Z Prepare workflow directory
2026-06-02T09:49:20.1105657Z Prepare all required actions
2026-06-02T09:49:20.1157277Z Getting action download info
2026-06-02T09:49:20.5660421Z Download action repository 'actions/checkout@v4' (SHA:34e114876b0b11c390a56381ad16ebd13914f8d5)
2026-06-02T09:49:20.7037183Z Download action repository 'actions/setup-java@v4' (SHA:c1e323688fd81a25caa38c78aa6df2d33d3e20d9)
2026-06-02T09:49:20.9858990Z Download action repository 'actions/upload-artifact@v4' (SHA:ea165f8d65b6e75b540449e92b4886f43607fa02)
2026-06-02T09:49:21.2039348Z Complete job name: build
2026-06-02T09:49:21.2774917Z ##[group]Run actions/checkout@v4
2026-06-02T09:49:21.2776014Z with:
2026-06-02T09:49:21.2776537Z   repository: qingfenglongyi/lf-media-player
2026-06-02T09:49:21.2781575Z   token: ***
2026-06-02T09:49:21.2782050Z   ssh-strict: true
2026-06-02T09:49:21.2782538Z   ssh-user: git
2026-06-02T09:49:21.2783029Z   persist-credentials: true
2026-06-02T09:49:21.2783563Z   clean: true
2026-06-02T09:49:21.2784053Z   sparse-checkout-cone-mode: true
2026-06-02T09:49:21.2784616Z   fetch-depth: 1
2026-06-02T09:49:21.2785091Z   fetch-tags: false
2026-06-02T09:49:21.2785582Z   show-progress: true
2026-06-02T09:49:21.2786063Z   lfs: false
2026-06-02T09:49:21.2786518Z   submodules: false
2026-06-02T09:49:21.2787010Z   set-safe-directory: true
2026-06-02T09:49:21.2787910Z ##[endgroup]
2026-06-02T09:49:21.3901396Z Syncing repository: qingfenglongyi/lf-media-player
2026-06-02T09:49:21.3903578Z ##[group]Getting Git version info
2026-06-02T09:49:21.3904543Z Working directory is '/home/runner/work/lf-media-player/lf-media-player'
2026-06-02T09:49:21.3905717Z [command]/usr/bin/git version
2026-06-02T09:49:21.3959807Z git version 2.54.0
2026-06-02T09:49:21.3984981Z ##[endgroup]
2026-06-02T09:49:21.4008807Z Temporarily overriding HOME='/home/runner/work/_temp/62c75eb7-725b-4abf-8694-ae4048ae05f8' before making global git config changes
2026-06-02T09:49:21.4010317Z Adding repository directory to the temporary git global config as a safe directory
2026-06-02T09:49:21.4013929Z [command]/usr/bin/git config --global --add safe.directory /home/runner/work/lf-media-player/lf-media-player
2026-06-02T09:49:21.4057638Z Deleting the contents of '/home/runner/work/lf-media-player/lf-media-player'
2026-06-02T09:49:21.4061502Z ##[group]Initializing the repository
2026-06-02T09:49:21.4065523Z [command]/usr/bin/git init /home/runner/work/lf-media-player/lf-media-player
2026-06-02T09:49:21.4137523Z hint: Using 'master' as the name for the initial branch. This default branch name
2026-06-02T09:49:21.4139892Z hint: will change to "main" in Git 3.0. To configure the initial branch name
2026-06-02T09:49:21.4141571Z hint: to use in all of your new repositories, which will suppress this warning,
2026-06-02T09:49:21.4142933Z hint: call:
2026-06-02T09:49:21.4143618Z hint:
2026-06-02T09:49:21.4144441Z hint: 	git config --global init.defaultBranch <name>
2026-06-02T09:49:21.4145120Z hint:
2026-06-02T09:49:21.4145765Z hint: Names commonly chosen instead of 'master' are 'main', 'trunk' and
2026-06-02T09:49:21.4146782Z hint: 'development'. The just-created branch can be renamed via this command:
2026-06-02T09:49:21.4148019Z hint:
2026-06-02T09:49:21.4148718Z hint: 	git branch -m <name>
2026-06-02T09:49:21.4149261Z hint:
2026-06-02T09:49:21.4149958Z hint: Disable this message with "git config set advice.defaultBranchName false"
2026-06-02T09:49:21.4151336Z Initialized empty Git repository in /home/runner/work/lf-media-player/lf-media-player/.git/
2026-06-02T09:49:21.4153784Z [command]/usr/bin/git remote add origin https://github.com/qingfenglongyi/lf-media-player
2026-06-02T09:49:21.4191803Z ##[endgroup]
2026-06-02T09:49:21.4192665Z ##[group]Disabling automatic garbage collection
2026-06-02T09:49:21.4195455Z [command]/usr/bin/git config --local gc.auto 0
2026-06-02T09:49:21.4224941Z ##[endgroup]
2026-06-02T09:49:21.4225741Z ##[group]Setting up auth
2026-06-02T09:49:21.4231774Z [command]/usr/bin/git config --local --name-only --get-regexp core\.sshCommand
2026-06-02T09:49:21.4265281Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'core\.sshCommand' && git config --local --unset-all 'core.sshCommand' || :"
2026-06-02T09:49:21.4570673Z [command]/usr/bin/git config --local --name-only --get-regexp http\.https\:\/\/github\.com\/\.extraheader
2026-06-02T09:49:21.4602066Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'http\.https\:\/\/github\.com\/\.extraheader' && git config --local --unset-all 'http.https://github.com/.extraheader' || :"
2026-06-02T09:49:21.4831088Z [command]/usr/bin/git config --local --name-only --get-regexp ^includeIf\.gitdir:
2026-06-02T09:49:21.4864475Z [command]/usr/bin/git submodule foreach --recursive git config --local --show-origin --name-only --get-regexp remote.origin.url
2026-06-02T09:49:21.5096057Z [command]/usr/bin/git config --local http.https://github.com/.extraheader AUTHORIZATION: basic ***
2026-06-02T09:49:21.5131085Z ##[endgroup]
2026-06-02T09:49:21.5132543Z ##[group]Fetching the repository
2026-06-02T09:49:21.5140872Z [command]/usr/bin/git -c protocol.version=2 fetch --no-tags --prune --no-recurse-submodules --depth=1 origin +74749c43114f765f88406f6c59bef6a20eae2f19:refs/remotes/origin/main
2026-06-02T09:49:21.7227731Z From https://github.com/qingfenglongyi/lf-media-player
2026-06-02T09:49:21.7229063Z  * [new ref]         74749c43114f765f88406f6c59bef6a20eae2f19 -> origin/main
2026-06-02T09:49:21.7259362Z ##[endgroup]
2026-06-02T09:49:21.7260660Z ##[group]Determining the checkout info
2026-06-02T09:49:21.7262367Z ##[endgroup]
2026-06-02T09:49:21.7268223Z [command]/usr/bin/git sparse-checkout disable
2026-06-02T09:49:21.7311344Z [command]/usr/bin/git config --local --unset-all extensions.worktreeConfig
2026-06-02T09:49:21.7339049Z ##[group]Checking out the ref
2026-06-02T09:49:21.7343493Z [command]/usr/bin/git checkout --progress --force -B main refs/remotes/origin/main
2026-06-02T09:49:21.7431558Z Switched to a new branch 'main'
2026-06-02T09:49:21.7434089Z branch 'main' set up to track 'origin/main'.
2026-06-02T09:49:21.7441130Z ##[endgroup]
2026-06-02T09:49:21.7479093Z [command]/usr/bin/git log -1 --format=%H
2026-06-02T09:49:21.7501967Z 74749c43114f765f88406f6c59bef6a20eae2f19
2026-06-02T09:49:21.7790697Z ##[group]Run actions/setup-java@v4
2026-06-02T09:49:21.7791291Z with:
2026-06-02T09:49:21.7791747Z   distribution: temurin
2026-06-02T09:49:21.7792245Z   java-version: 17
2026-06-02T09:49:21.7792711Z   java-package: jdk
2026-06-02T09:49:21.7793187Z   check-latest: false
2026-06-02T09:49:21.7793889Z   server-id: github
2026-06-02T09:49:21.7794372Z   server-username: GITHUB_ACTOR
2026-06-02T09:49:21.7794933Z   server-password: GITHUB_TOKEN
2026-06-02T09:49:21.7795487Z   overwrite-settings: true
2026-06-02T09:49:21.7796001Z   job-status: success
2026-06-02T09:49:21.7800797Z   token: ***
2026-06-02T09:49:21.7801257Z ##[endgroup]
2026-06-02T09:49:21.9861408Z ##[group]Installed distributions
2026-06-02T09:49:21.9909305Z Resolved Java 17.0.19+10 from tool-cache
2026-06-02T09:49:21.9910573Z Setting Java 17.0.19+10 as the default
2026-06-02T09:49:21.9922384Z Creating toolchains.xml for JDK version 17 from temurin
2026-06-02T09:49:21.9994070Z Writing to /home/runner/.m2/toolchains.xml
2026-06-02T09:49:21.9995036Z 
2026-06-02T09:49:21.9995454Z Java configuration:
2026-06-02T09:49:21.9996442Z   Distribution: temurin
2026-06-02T09:49:21.9997505Z   Version: 17.0.19+10
2026-06-02T09:49:21.9999076Z   Path: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-02T09:49:22.0000293Z 
2026-06-02T09:49:22.0001552Z ##[endgroup]
2026-06-02T09:49:22.0016544Z Creating settings.xml with server-id: github
2026-06-02T09:49:22.0022596Z Writing to /home/runner/.m2/settings.xml
2026-06-02T09:49:22.0179536Z ##[group]Run chmod +x ./gradlew
2026-06-02T09:49:22.0180848Z [36;1mchmod +x ./gradlew[0m
2026-06-02T09:49:22.0182156Z [36;1m./gradlew wrapper --gradle-version 8.4[0m
2026-06-02T09:49:22.0217705Z shell: /usr/bin/bash -e {0}
2026-06-02T09:49:22.0219544Z env:
2026-06-02T09:49:22.0221617Z   JAVA_HOME: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-02T09:49:22.0225040Z   JAVA_HOME_17_X64: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-02T09:49:22.0228433Z ##[endgroup]
2026-06-02T09:49:22.0398277Z sed: -e expression #1, char 8: unterminated `s' command
2026-06-02T09:49:22.0410489Z echo: write error: Broken pipe
2026-06-02T09:49:22.0420447Z echo: write error: Broken pipe
2026-06-02T09:49:22.0426327Z ./gradlew: 197: s~.*~\&'~ ; : not found
2026-06-02T09:49:23.3837131Z Downloading https://services.gradle.org/distributions/gradle-8.4-bin.zip
2026-06-02T09:49:25.8361398Z ............10%............20%.............30%............40%.............50%............60%.............70%............80%.............90%............100%
2026-06-02T09:49:27.0554029Z 
2026-06-02T09:49:27.0554855Z Welcome to Gradle 8.4!
2026-06-02T09:49:27.0555238Z 
2026-06-02T09:49:27.0558655Z Here are the highlights of this release:
2026-06-02T09:49:27.0567682Z  - Compiling and testing with Java 21
2026-06-02T09:49:27.0569877Z  - Faster Java compilation on Windows
2026-06-02T09:49:27.0573560Z  - Role focused dependency configurations creation
2026-06-02T09:49:27.0574031Z 
2026-06-02T09:49:27.0577416Z For more details see https://docs.gradle.org/8.4/release-notes.html
2026-06-02T09:49:27.0578409Z 
2026-06-02T09:49:27.2550613Z Starting a Gradle Daemon (subsequent builds will be faster)
2026-06-02T09:50:22.7553013Z > Task :wrapper
2026-06-02T09:50:22.7553621Z 
2026-06-02T09:50:22.7555609Z BUILD SUCCESSFUL in 59s
2026-06-02T09:50:22.7556473Z 1 actionable task: 1 executed
2026-06-02T09:50:23.1087490Z ##[group]Run ./gradlew assembleDebug
2026-06-02T09:50:23.1088120Z [36;1m./gradlew assembleDebug[0m
2026-06-02T09:50:23.1117144Z shell: /usr/bin/bash -e {0}
2026-06-02T09:50:23.1117426Z env:
2026-06-02T09:50:23.1117981Z   JAVA_HOME: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-02T09:50:23.1118542Z   JAVA_HOME_17_X64: /opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.19-10/x64
2026-06-02T09:50:23.1118943Z ##[endgroup]
2026-06-02T09:50:24.9662924Z > Task :app:preBuild UP-TO-DATE
2026-06-02T09:50:24.9671588Z > Task :app:preDebugBuild UP-TO-DATE
2026-06-02T09:50:24.9675861Z > Task :app:mergeDebugNativeDebugMetadata NO-SOURCE
2026-06-02T09:50:24.9679155Z > Task :app:checkKotlinGradlePluginConfigurationErrors
2026-06-02T09:50:30.2698141Z 
2026-06-02T09:50:30.2711663Z > Task :app:checkDebugAarMetadata
2026-06-02T09:50:30.2713960Z WARNING: [Processor] Library '/home/runner/.gradle/caches/modules-2/files-2.1/androidx.media3/media3-session/1.2.0/56908a8e279d1309f131b639d8c323a7b7d75034/media3-session-1.2.0.aar' contains references to both AndroidX and old support library. This seems like the library is partially migrated. Jetifier will try to rewrite the library anyway.
2026-06-02T09:50:30.2716715Z  Example of androidX reference: 'androidx/media3/session/MediaBrowserImplLegacy$1'
2026-06-02T09:50:30.2717728Z  Example of support library reference: 'android/support/v4/media/MediaBrowserCompat$MediaItem'
2026-06-02T09:50:30.3770976Z WARNING: [Processor] Library '/home/runner/.gradle/caches/modules-2/files-2.1/androidx.media3/media3-ui/1.2.0/65a0bff9352c8805515d6bcbee6d24e19a6c3d06/media3-ui-1.2.0.aar' contains references to both AndroidX and old support library. This seems like the library is partially migrated. Jetifier will try to rewrite the library anyway.
2026-06-02T09:50:30.3773597Z  Example of androidX reference: 'androidx/media3/ui/PlayerNotificationManager'
2026-06-02T09:50:30.3829172Z  Example of support library reference: 'android/support/v4/media/session/MediaSessionCompat$Token'
2026-06-02T09:50:34.1688692Z 
2026-06-02T09:50:34.1738908Z > Task :app:generateDebugResValues
2026-06-02T09:50:34.3642072Z > Task :app:mapDebugSourceSetPaths
2026-06-02T09:50:34.3689826Z > Task :app:generateDebugResources
2026-06-02T09:50:35.6660587Z > Task :app:packageDebugResources
2026-06-02T09:50:36.3631962Z > Task :app:mergeDebugResources
2026-06-02T09:50:36.8673863Z > Task :app:createDebugCompatibleScreenManifests
2026-06-02T09:50:36.8718039Z > Task :app:extractDeepLinksDebug
2026-06-02T09:50:37.0642035Z > Task :app:parseDebugLocalResources
2026-06-02T09:50:37.2637543Z > Task :app:processDebugMainManifest
2026-06-02T09:50:37.3632234Z > Task :app:processDebugManifest
2026-06-02T09:50:38.6656452Z > Task :app:javaPreCompileDebug
2026-06-02T09:50:38.6666385Z > Task :app:mergeDebugShaders
2026-06-02T09:50:38.6667197Z > Task :app:compileDebugShaders NO-SOURCE
2026-06-02T09:50:38.6668140Z > Task :app:generateDebugAssets UP-TO-DATE
2026-06-02T09:50:38.7655791Z > Task :app:mergeDebugAssets
2026-06-02T09:50:38.7679313Z > Task :app:compressDebugAssets
2026-06-02T09:50:38.8692298Z > Task :app:desugarDebugFileDependencies
2026-06-02T09:50:40.4630787Z > Task :app:mergeDebugJniLibFolders
2026-06-02T09:50:40.4631702Z > Task :app:processDebugManifestForPackage
2026-06-02T09:50:40.4632300Z > Task :app:checkDebugDuplicateClasses
2026-06-02T09:51:10.4659612Z > Task :app:processDebugResources
2026-06-02T09:51:11.6635527Z > Task :app:mergeExtDexDebug
2026-06-02T09:51:27.0630668Z > Task :app:mergeLibDexDebug
2026-06-02T09:51:27.1631786Z > Task :app:mergeDebugNativeLibs NO-SOURCE
2026-06-02T09:51:27.1659004Z > Task :app:stripDebugDebugSymbols NO-SOURCE
2026-06-02T09:51:27.8638755Z > Task :app:validateSigningDebug
2026-06-02T09:51:27.8639378Z > Task :app:writeDebugAppMetadata
2026-06-02T09:51:27.8668319Z > Task :app:writeDebugSigningConfigVersions
2026-06-02T09:51:32.5641055Z > Task :app:kspDebugKotlin
2026-06-02T09:51:39.8630950Z 
2026-06-02T09:51:39.8650844Z e: file:///home/runner/work/lf-media-player/lf-media-player/app/src/main/java/com/byd/mediaplayer/ui/PlaylistPanel.kt:164:55 Val cannot be reassigned
2026-06-02T09:51:39.8652379Z 
2026-06-02T09:51:39.8652581Z > Task :app:compileDebugKotlin FAILED
2026-06-02T09:51:39.8653378Z 27 actionable tasks: 27 executed
2026-06-02T09:51:39.8656524Z FAILURE: Build failed with an exception.
2026-06-02T09:51:39.8657220Z 
2026-06-02T09:51:39.8657587Z * What went wrong:
2026-06-02T09:51:39.8658580Z Execution failed for task ':app:compileDebugKotlin'.
2026-06-02T09:51:39.8660035Z > A failure occurred while executing org.jetbrains.kotlin.compilerRunner.GradleCompilerRunnerWithWorkers$GradleKotlinCompilerWorkAction
2026-06-02T09:51:39.8661422Z    > Compilation error. See log for more details
2026-06-02T09:51:39.8661966Z 
2026-06-02T09:51:39.8662297Z * Try:
2026-06-02T09:51:39.8662914Z > Run with --stacktrace option to get the stack trace.
2026-06-02T09:51:39.8663762Z > Run with --info or --debug option to get more log output.
2026-06-02T09:51:39.8664869Z > Run with --scan to get full insights.
2026-06-02T09:51:39.8665589Z > Get more help at https://help.gradle.org.
2026-06-02T09:51:39.8666322Z 
2026-06-02T09:51:39.8666680Z BUILD FAILED in 1m 16s
2026-06-02T09:51:39.9485157Z ##[error]Process completed with exit code 1.
2026-06-02T09:51:39.9643159Z Post job cleanup.
2026-06-02T09:51:40.2245966Z Post job cleanup.
2026-06-02T09:51:40.3538715Z [command]/usr/bin/git version
2026-06-02T09:51:40.3567359Z git version 2.54.0
2026-06-02T09:51:40.3602990Z Temporarily overriding HOME='/home/runner/work/_temp/8c11566f-8dcc-4e52-b82c-b893e3959ffb' before making global git config changes
2026-06-02T09:51:40.3604161Z Adding repository directory to the temporary git global config as a safe directory
2026-06-02T09:51:40.3605221Z [command]/usr/bin/git config --global --add safe.directory /home/runner/work/lf-media-player/lf-media-player
2026-06-02T09:51:40.3650665Z [command]/usr/bin/git config --local --name-only --get-regexp core\.sshCommand
2026-06-02T09:51:40.3686607Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'core\.sshCommand' && git config --local --unset-all 'core.sshCommand' || :"
2026-06-02T09:51:40.3932768Z [command]/usr/bin/git config --local --name-only --get-regexp http\.https\:\/\/github\.com\/\.extraheader
2026-06-02T09:51:40.3959374Z http.https://github.com/.extraheader
2026-06-02T09:51:40.3972025Z [command]/usr/bin/git config --local --unset-all http.https://github.com/.extraheader
2026-06-02T09:51:40.4004575Z [command]/usr/bin/git submodule foreach --recursive sh -c "git config --local --name-only --get-regexp 'http\.https\:\/\/github\.com\/\.extraheader' && git config --local --unset-all 'http.https://github.com/.extraheader' || :"
2026-06-02T09:51:40.4236413Z [command]/usr/bin/git config --local --name-only --get-regexp ^includeIf\.gitdir:
2026-06-02T09:51:40.4269118Z [command]/usr/bin/git submodule foreach --recursive git config --local --show-origin --name-only --get-regexp remote.origin.url
2026-06-02T09:51:40.4676733Z Cleaning up orphan processes
2026-06-02T09:51:40.5009497Z Terminate orphan process: pid (2337) (java)
2026-06-02T09:51:40.5042613Z Terminate orphan process: pid (2627) (java)
2026-06-02T09:51:40.5071153Z ##[warning]Node.js 20 actions are deprecated. The following actions are running on Node.js 20 and may not work as expected: actions/checkout@v4, actions/setup-java@v4. Actions will be forced to run with Node.js 24 by default starting June 16th, 2026. Node.js 20 will be removed from the runner on September 16th, 2026. Please check if updated versions of these actions are available that support Node.js 24. To opt into Node.js 24 now, set the FORCE_JAVASCRIPT_ACTIONS_TO_NODE24=true environment variable on the runner or in your workflow file. Once Node.js 24 becomes the default, you can temporarily opt out by setting ACTIONS_ALLOW_USE_UNSECURE_NODE_VERSION=true. For more information see: https://github.blog/changelog/2025-09-19-deprecation-of-node-20-on-github-actions-runners/