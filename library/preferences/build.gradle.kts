plugins {
    alias(libs.plugins.android.library.convention)
}

android {
    namespace = "com.mudita.chess.preferences"
}

dependencies {
    implementation(projects.library.json)

    implementation(libs.androidx.datastore.preferences)

    api(libs.kotlinx.coroutines.core)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.core)
}
