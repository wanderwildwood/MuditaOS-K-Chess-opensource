plugins {
    alias(libs.plugins.android.library.convention)
    alias(libs.plugins.sqldelight)
}

android {
    namespace = "com.mudita.chess.database"
    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(libs.androidx.sqlite)

    implementation(libs.sqldelight.android.driver)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.core)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.sqldelight.coroutines)
    testImplementation(libs.sqldelight.sqlite.driver)
}
