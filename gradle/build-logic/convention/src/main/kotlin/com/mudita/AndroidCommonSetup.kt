package com.mudita

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) = with(commonExtension) {
    compileSdk = projectCompileSdk

    defaultConfig {
        minSdk = projectMinSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        animationsDisabled = true
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.useJUnitPlatform()
                it.jvmArgs(it.jvmArgs + "-Xshare:off")
                it.maxParallelForks = Runtime.getRuntime().availableProcessors() - 1
            }
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    dependencies {
        add("coreLibraryDesugaring", libs.findLibrary("desugarJdkLibs").get())
    }
}
