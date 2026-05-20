import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import com.mikepenz.aboutlibraries.plugin.DuplicateRule
import com.mudita.sentry.plugins.tasks.SentryReleaseTask
import com.mudita.sentry.plugins.tasks.model.AppMetadata
import com.mudita.sentry.plugins.util.generateSentryUuid
import com.mudita.tasks.DeployTask
import com.mudita.tasks.GenerateChangelogTask

plugins {
    alias(libs.plugins.android.application.convention)
    alias(libs.plugins.android.application.compose.convention)
    alias(libs.plugins.androidx.baselineprofile)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.mudita.sentry)
}

android {
    namespace = "com.mudita.chess"
    defaultConfig {
        applicationId = project.libs.versions.app.version.appId.get()
        versionName = project.libs.versions.app.version.versionName.get()
        versionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
        buildConfigField("String", "PROGUARD_UUID", "\"${generateSentryUuid()}\"")
        buildConfigField("String", "SENTRY_DSN", "\"${gradle.extra["sentryDsn"]}\"")
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
            signingConfig = signingConfigs.getByName("debug")
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
            signingConfig = signingConfigs.getByName("debug")
        }

        create("benchmark") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")
            signingConfig = signingConfigs.getByName("debug")
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
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    android.applicationVariants.all {
        preBuildProvider.configure {
            dependsOn("exportLibraryDefinitions")
        }
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

aboutLibraries {
    val ghToken = System.getenv("GITHUB_TOKEN").orEmpty()
    if (ghToken.isNotBlank()) {
        fetchRemoteLicense = true
        gitHubApiToken = ghToken
    }

    configPath = "config"

    allowedLicenses = arrayOf("Apache-2.0")

    allowedLicensesMap = mapOf(
        "com.github.bhlangonijr:chesslib" to listOf("Apache-2.0")
    )

    exclusionPatterns = listOf(
        Regex("com\\.mudita.*").toPattern()
    )

    duplicationMode = DuplicateMode.MERGE
    duplicationRule = DuplicateRule.SIMPLE
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

    implementation(libs.kompakt.ui)

    implementation(libs.logcat)

    implementation(libs.sentry.sdk)
    implementation(libs.about.libraries)
}

tasks.register("uploadApkToNexus", DeployTask::class) {
    versionName = project.libs.versions.app.version.versionName.get()
    tagPrefix = project.property("tagPrefix") as String? ?: "development"

    nexusUrl = project.property("nexusUrl") as String? ?: ""
    nexusUsername = gradle.extra["muditaUsername"].toString()
    nexusPassword = gradle.extra["muditaPassword"].toString()
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

tasks.named("createReleaseAndUploadMapping", SentryReleaseTask::class) {
    val variantOutput = project.android.applicationVariants
        .firstOrNull {
            gradle.startParameter.taskNames.any { taskName ->
                taskName.contains(
                    it.name,
                    ignoreCase = true
                )
            }
        } ?: throw IllegalStateException("No matching variant found for the task name.")

    val buildVariant = variantOutput.buildType.name.takeIf {
        it.equals("release", ignoreCase = true) || it.equals("qa", ignoreCase = true)
    } ?: throw IllegalStateException(
        "Invalid build variant: ${variantOutput.buildType.name}." +
                " Supported variants are: \"release\", \"qa\""
    )

    appMetadata = AppMetadata(
        buildVariant = buildVariant,
        packageName = variantOutput.applicationId,
        versionName = variantOutput.versionName,
        versionCode = variantOutput.versionCode.toString()
    )
}
