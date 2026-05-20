plugins {
    alias(libs.plugins.jvm.library.convention)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.json.okio)

    implementation(libs.okio)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.truth)
}
