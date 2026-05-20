plugins {
    alias(libs.plugins.android.library.convention)
    alias(libs.plugins.android.library.compose.convention)
}

android {
    namespace = "com.mudita.chess.mvvm"
}

dependencies {
    api(libs.androidx.lifecycle.viewmodel)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.logcat)

    testImplementation(testFixtures(projects.library.coroutines))
    testImplementation(libs.bundles.test)
}
