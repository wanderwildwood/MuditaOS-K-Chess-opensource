plugins {
    alias(libs.plugins.android.library.convention)
}

android {
    namespace = "com.mudita.chess.games"
}

dependencies {
    implementation(projects.library.coroutines)
    implementation(projects.library.database)

    implementation(libs.kotlinx.coroutines.core)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    implementation(libs.chesslib)

    testImplementation(libs.bundles.test)
    testImplementation(libs.sqldelight.coroutines)
    testImplementation(libs.sqldelight.sqlite.driver)
}
