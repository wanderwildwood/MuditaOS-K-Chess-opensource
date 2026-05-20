import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.mudita.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kotlin.compose.gradlePlugin)
    implementation(libs.kotlin.kover.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "android-application.convention"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "android-application.compose.convention"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "android-library.convention"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "android-library.compose.convention"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidTest") {
            id = "android-test.convention"
            implementationClass = "AndroidTestConventionPlugin"
        }
        register("jvmLibrary") {
            id = "jvm-library.convention"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("ktlint") {
            id = "ktlint.convention"
            implementationClass = "KtLintConventionPlugin"
        }
        register("detekt") {
            id = "detekt.convention"
            implementationClass = "DetektConventionPlugin"
        }
        register("lint") {
            id = "lint.convention"
            implementationClass = "LintConventionPlugin"
        }
        register("koverAggregation") {
            id = "kover.aggregation"
            implementationClass = "KoverAggregationPlugin"
        }
    }
}
