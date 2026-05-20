plugins {
    alias(libs.plugins.android.library.convention)
    alias(libs.plugins.android.library.compose.convention)
}

android {
    namespace = "com.mudita.chess.ui"
}

dependencies {
    implementation(projects.library.frontitude)

    api(libs.androidx.compose.tooling.preview)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.core)

    implementation(libs.kompakt.ui)
}
