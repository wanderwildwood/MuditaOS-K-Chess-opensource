plugins {
    alias(libs.plugins.android.library.convention)
}

android {
    namespace = "com.mudita.chess.gameloader"
}

dependencies {
    implementation(projects.service.games)
    implementation(projects.service.gameoptions)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    implementation(libs.chesslib)

    implementation(libs.logcat)
}
