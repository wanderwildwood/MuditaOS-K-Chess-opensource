plugins {
    alias(libs.plugins.android.library.convention)
    alias(libs.plugins.android.library.compose.convention)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.mudita.chess.navigation"
}

dependencies {
    implementation(libs.androidx.appcompat)
    api(libs.androidx.navigation.common.ktx)

    implementation(libs.kotlinx.serialization.json)
}
