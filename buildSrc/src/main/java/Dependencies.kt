object Versions {
    const val compileSdkVersion = 30
    const val minSdkVersion = 21
    const val targetSdkVersion = 30
    const val kotlinVersion = "1.4.21"
}

object Dependencies {
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"

    const val coreKtx = "androidx.core:core-ktx:1.3.2"
    const val appCompat = "androidx.appcompat:appcompat:1.2.0"
    const val material = "com.google.android.material:material:1.2.1"

    private const val arrowVersion = "0.12.0-SNAPSHOT"
    const val arrowCore = "io.arrow-kt:arrow-core:${arrowVersion}"
    const val arrowSyntax = "io.arrow-kt:arrow-syntax:${arrowVersion}"
    const val arrowMeta = "io.arrow-kt:arrow-meta:${arrowVersion}"
    const val arrowFx = "io.arrow-kt:arrow-fx:${arrowVersion}"
    const val arrowFxCoroutines = "io.arrow-kt:arrow-fx-coroutines:${arrowVersion}"

    private const val coroutineVersion = "1.4.1"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutineVersion}"
}

object Plugins {
    const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
    const val androidGradle = "com.android.tools.build:gradle:4.1.1"
}

object Testing {
    private const val kotestVersion = "4.3.1"

    private const val kotestRunner = "io.kotest:kotest-runner-junit5:${kotestVersion}"
    private const val kotestAssertionsCore = "io.kotest:kotest-assertions-core:${kotestVersion}"
    private const val kotestAssertionsArrow = "io.kotest:kotest-assertions-arrow:${kotestVersion}"
    private const val kotestProperty = "io.kotest:kotest-property:${kotestVersion}"

    private const val junit = "androidx.test.ext:junit:1.1.2"
    private const val espresso = "androidx.test.espresso:espresso-core:3.3.0"
    const val mockk = "io.mockk:mockk:v1.10.2"
}
