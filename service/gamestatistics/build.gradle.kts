plugins {
    alias(libs.plugins.android.library.convention)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mudita.chess.gamestatistics"
}

dependencies {
    implementation(projects.library.coroutines)
    implementation(projects.library.preferences)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    implementation(libs.kotlinx.serialization.json)

    testImplementation(testFixtures(projects.library.coroutines))
    testImplementation(libs.bundles.test)
}
