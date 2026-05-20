plugins {
    alias(libs.plugins.jvm.library.convention)
    alias(libs.plugins.java.test.fixtures)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)

    testFixturesImplementation(libs.junit.api)
    testFixturesImplementation(libs.kotlinx.coroutines.test)
}
