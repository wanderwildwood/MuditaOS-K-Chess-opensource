import com.mudita.tasks.GenerateChangelogTask
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
        versionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
    }

    // The checked-in certs/debug.keystore is the AOSP test key, whose private half ships with
    // AOSP - anyone can build an APK this app's own signature check would accept as an update.
    // A real keystore in signing/ replaces it for every build type when one is present; the
    // release workflow writes it there from repository secrets.
    val signingPropertiesFile = rootProject.file("signing/signing.properties")
    val realSigningConfig = if (signingPropertiesFile.isFile) {
        val signingProperties = Properties().apply {
            signingPropertiesFile.inputStream().use(::load)
        }
        signingConfigs.create("release") {
            storeFile = rootProject.file("signing/signing.keystore")
            storePassword = signingProperties.getProperty("STORE_PASSWORD")
            keyAlias = signingProperties.getProperty("KEY_ALIAS")
            keyPassword = signingProperties.getProperty("KEY_PASSWORD")
        }
    } else {
        null
    }

    signingConfigs {
        getByName("debug") {
            storeFile = rootProject.file("app-android/certs/debug.keystore")
            storePassword = "android"
            keyAlias = "system-debug"
            keyPassword = "android"
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            signingConfig = realSigningConfig ?: signingConfigs.getByName("debug")
        }
        create("qa") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            matchingFallbacks += listOf("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = realSigningConfig ?: signingConfigs.getByName("debug")
        }

        create("benchmark") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")
            signingConfig = realSigningConfig ?: signingConfigs.getByName("debug")
            proguardFiles("benchmark-rules.pro")
        }
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // TODO use a proper release signing
            signingConfig = realSigningConfig ?: signingConfigs.getByName("debug")
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
    "qaImplementation"(projects.features.gameloader)
    implementation(projects.features.statistics)

    "baselineProfile"(projects.baselineprofile)

    debugImplementation(libs.leakcanary)

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

tasks.register("generateChangelog", GenerateChangelogTask::class) {
    // e.g.: com.mudita.notes -> notes
    appName = project.libs.versions.app.version.appId.get().split(".").last()
    // MAJOR.MINOR.PATCH versioning
    versionName = project.libs.versions.app.version.versionName.get()
}

tasks.register("checkVersion") {
    doFirst {
        val currentVersion = project.libs.versions.app.version.versionName.get()

        // Extracting the tag from the GITHUB_REF environment variable
        val githubRef = System.getenv("GITHUB_REF") ?: throw GradleException("GITHUB_REF not found.")
        // Example of githubRef: refs/tags/release.0.0.1

        val pattern = Regex("(release|qa)\\.(\\d+\\.\\d+\\.\\d+(-rc\\d+)?)")
        val matchResult = pattern.find(githubRef.removePrefix("refs/tags/"))
            ?: throw GradleException("The git tag does not follow the required 'type.x.y.z' pattern.")
        val tagVersion = matchResult.groupValues[2]

        if (currentVersion != tagVersion) {
            throw GradleException("The version in build.gradle.kts ($currentVersion) does not match the tag version ($tagVersion).")
        }
    }
}
