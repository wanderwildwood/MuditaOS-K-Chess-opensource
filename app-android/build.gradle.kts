import java.util.Properties

plugins {
    alias(libs.plugins.android.application.convention)
    alias(libs.plugins.android.application.compose.convention)
    alias(libs.plugins.androidx.baselineprofile)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mudita.chess"
    defaultConfig {
        applicationId = project.libs.versions.app.version.appId.get()
        versionName = project.libs.versions.app.version.versionName.get()
        versionCode = project.libs.versions.app.version.versionCode.get().toInt()
    }

    // A real keystore in signing/ signs every build type when it is present. It is gitignored,
    // and there is no fallback: without it a release builds unsigned, which will not install
    // anywhere. A key in a public repository is not a signing key, and a missing one should
    // stop a release rather than produce something installable. Debug builds without it get
    // the ordinary Android debug key from AGP.
    val signingPropertiesFile = rootProject.file("signing/signing.properties")
    val realSigningConfig = if (signingPropertiesFile.isFile) {
        val signingProperties = Properties().apply {
            signingPropertiesFile.inputStream().use(::load)
        }
        signingConfigs.create("real") {
            storeFile = rootProject.file("signing/signing.keystore")
            storePassword = signingProperties.getProperty("STORE_PASSWORD")
            keyAlias = signingProperties.getProperty("KEY_ALIAS")
            keyPassword = signingProperties.getProperty("KEY_PASSWORD")
        }
    } else {
        null
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            realSigningConfig?.let { signingConfig = it }
        }
        create("benchmark") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")
            // Benchmarks are run here, never published, so the debug key will do.
            signingConfig = realSigningConfig ?: signingConfigs.getByName("debug")
            proguardFiles("benchmark-rules.pro")
        }
        getByName("release") {
            // AGP stamps the git revision into META-INF, and the build box works from an rsync
            // with no .git. Off, so a release built anywhere has the same contents.
            vcsInfo {
                include = false
            }

            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = realSigningConfig
        }
    }

    android.applicationVariants.all {
        outputs.all {
            val outputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            val appName = project.libs.versions.app.version.appId.get().split(".").last()
            val appVersion = versionName
            val buildType = buildType.name
            val appVersionCode = versionCode

            val newApkName = "$appName-$appVersion($appVersionCode)-$buildType.apk"
            outputImpl.outputFileName = newApkName
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

baselineProfile {
    mergeIntoMain = true
    baselineProfileOutputDir = "${project.projectDir}/src/main/baselineProfiles"
}

dependencies {
    implementation(projects.service.gameoptions)
    implementation(projects.service.games)
    implementation(projects.service.gamestatistics)

    implementation(projects.library.appinfo)
    implementation(projects.library.chessEngine)
    implementation(projects.library.coroutines)
    implementation(projects.library.database)
    implementation(projects.library.json)
    implementation(projects.library.navigation)
    implementation(projects.library.preferences)
    implementation(projects.library.ui)

    implementation(projects.features.main)
    implementation(projects.features.gamemoves)
    implementation(projects.features.gameplay)
    implementation(projects.features.optionsmenu)
    debugImplementation(projects.features.gameloader)
    implementation(projects.features.statistics)

    "baselineProfile"(projects.baselineprofile)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material3)

    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    implementation(libs.mmd)

    implementation(libs.logcat)
}
