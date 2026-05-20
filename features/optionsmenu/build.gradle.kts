plugins {
    alias(libs.plugins.android.library.convention)
    alias(libs.plugins.android.library.compose.convention)
}

android {
    namespace = "com.mudita.chess.optionsmenu"
}

dependencies {
    implementation(projects.service.gameoptions)

    implementation(projects.library.coroutines)
    implementation(projects.library.frontitude)
    implementation(projects.library.mvvm)
    implementation(projects.library.navigation)
    implementation(projects.library.ui)

    implementation(libs.androidx.compose.material3)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    implementation(libs.kompakt.ui)
    implementation(libs.logcat)

    testImplementation(testFixtures(projects.library.coroutines))
    testImplementation(libs.bundles.test)
}
