object Versions {

    const val packageName = "be.tapped.vlaamsetv"
    const val compileSdkVersion = 30
    const val minSdkVersion = 26
    const val targetSdkVersion = 30
    const val kotlinVersion = "1.4.30"
}

private const val navVersion = "2.3.2"
private const val workVersion = "2.4.0"

object Dependencies {

    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"

    const val androidXDataStore = "androidx.datastore:datastore:1.0.0-alpha05"

    private const val exoPlayerVersion = "2.12.2"

    // ExoPlayer
    // Required
    const val exoplayerCore = "com.google.android.exoplayer:exoplayer-core:${exoPlayerVersion}"

    // Optional
    const val exoplayerDash = "com.google.android.exoplayer:exoplayer-dash:${exoPlayerVersion}"
    const val exoplayerHLS = "com.google.android.exoplayer:exoplayer-hls:${exoPlayerVersion}"
    const val exoplayerSmoothStreaming = "com.google.android.exoplayer:exoplayer-smoothstreaming:${exoPlayerVersion}"
    const val exoplayerUi = "com.google.android.exoplayer:exoplayer-ui:${exoPlayerVersion}"

    // Extensions
    const val exoplayerExtCast = "com.google.android.exoplayer:extension-cast:${exoPlayerVersion}"
    const val exoplayerExtLeanback = "com.google.android.exoplayer:extension-leanback:${exoPlayerVersion}"
    const val exoplayerExtMedia2 = "com.google.android.exoplayer:extension-media2:${exoPlayerVersion}"
    const val exoplayerExtMediaSession = "com.google.android.exoplayer:extension-mediasession:${exoPlayerVersion}"
    const val exoplayerExtWorkManager = "com.google.android.exoplayer:extension-workmanager:${exoPlayerVersion}"

    const val okHttp3 = "com.squareup.okhttp3:okhttp:4.9.0"

    const val coreKtx = "androidx.core:core-ktx:1.3.2"
    const val appCompat = "androidx.appcompat:appcompat:1.2.0"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:1.2.5"
    const val material = "com.google.android.material:material:1.2.1"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
    const val lifecycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0"
    const val wireRuntime = "com.squareup.wire:wire-runtime:3.5.0"

    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:$navVersion"
    const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:$navVersion"

    private const val arrowVersion = "0.12.0-SNAPSHOT"
    const val arrowCore = "io.arrow-kt:arrow-core:${arrowVersion}"
    const val arrowSyntax = "io.arrow-kt:arrow-syntax:${arrowVersion}"
    const val arrowMeta = "io.arrow-kt:arrow-meta:${arrowVersion}"
    const val arrowFx = "io.arrow-kt:arrow-fx:${arrowVersion}"
    const val arrowFxCoroutines = "io.arrow-kt:arrow-fx-coroutines:${arrowVersion}"

    const val workRuntime = "androidx.work:work-runtime-ktx:$workVersion"

    private const val coroutineVersion = "1.4.1"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutineVersion}"

    const val coil = "io.coil-kt:coil:1.1.1"
}

object Plugins {

    const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
    const val androidGradle = "com.android.tools.build:gradle:4.1.1"
    const val wire = "com.squareup.wire:wire-gradle-plugin:3.3.0"
    const val navigationSafeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion"
}

object Testing {

    private const val kotestVersion = "4.3.1"

    const val kotestRunner = "io.kotest:kotest-runner-junit5:${kotestVersion}"
    const val kotestAssertionsCore = "io.kotest:kotest-assertions-core:${kotestVersion}"
    const val kotestAssertionsArrow = "io.kotest:kotest-assertions-arrow:${kotestVersion}"
    const val kotestProperty = "io.kotest:kotest-property:${kotestVersion}"

    private const val junit = "androidx.test.ext:junit:1.1.2"
    const val mockk = "io.mockk:mockk:v1.10.2"

    const val fragmentTest = "androidx.fragment:fragment-testing:1.2.5"

    const val espressoCore = "androidx.test.espresso:espresso-core:3.2.0"
    const val espressoContribution = "androidx.test.espresso:espresso-contrib:3.2.0"
    const val uiTestRules = "androidx.test:rules:1.2.0"
    const val testRunner = "androidx.test:runner:1.3.0"
    const val testRules = "androidx.test:rules:1.3.0"
    const val testOrchestrator = "androidx.test:orchestrator:1.3.0"
    const val kakao = "com.agoda.kakao:kakao:2.4.0"
    const val workManagerTest = "androidx.work:work-testing:$workVersion"

    const val testNavigation = "androidx.navigation:navigation-testing:$navVersion"
    const val leakCanary = "com.squareup.leakcanary:leakcanary-android:2.6"
}
