plugins {
    alias(libs.plugins.android.library.convention)
}

android {
    namespace = "com.mudita.chess.engine"

    defaultConfig {
        ndk {
            abiFilters.clear()
            //noinspection ChromeOsAbiSupport
            abiFilters += "arm64-v8a"
        }
    }
}

dependencies {
    implementation(projects.library.coroutines)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.core)

    implementation(libs.logcat)

    testImplementation(libs.bundles.test)
}
