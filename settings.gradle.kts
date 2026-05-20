pluginManagement {
    val properties = java.util.Properties()
    file("local.properties").takeIf { it.isFile }?.inputStream()?.use {
        properties.load(java.io.InputStreamReader(it, Charsets.UTF_8))
    }

    gradle.extra["muditaUsername"] = properties.getProperty("mudita_repo_username")
        ?: System.getenv("MUDITA_ARTIFACTORY_USERNAME")
    gradle.extra["muditaPassword"] = properties.getProperty("mudita_repo_password")
        ?: System.getenv("MUDITA_ARTIFACTORY_PASSWORD")
    gradle.extra["nexusPrivateUrl"] = properties.getProperty("mudita_nexus_repo_url")
        ?: System.getenv("MUDITA_PRIVATE_REPOSITORY_URL")
    gradle.extra["sentryDsn"] = properties.getProperty("sentry_dsn")
        ?: System.getenv("SENTRY_DSN")

    includeBuild("gradle/build-logic")

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven {
            url = uri(gradle.extra["nexusPrivateUrl"].toString())
            credentials {
                username = gradle.extra["muditaUsername"].toString()
                password = gradle.extra["muditaPassword"].toString()
            }
        }
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://jitpack.io") }

        maven {
            url = uri(gradle.extra["nexusPrivateUrl"].toString())
            credentials {
                username = gradle.extra["muditaUsername"].toString()
                password = gradle.extra["muditaPassword"].toString()
            }
        }
    }
}

rootProject.name = "Chess"

include(":app-android")

include(":features:main")
include(":features:gameloader")
include(":features:gamemoves")
include(":features:gameplay")
include(":features:optionsmenu")
include(":features:statistics")

include(":service:gameoptions")
include(":service:games")
include(":service:gamestatistics")

include(":library:appinfo")
include(":library:chess-engine")
include(":library:chess-engine-bin")
include(":library:coroutines")
include(":library:database")
include(":library:frontitude")
include(":library:json")
include(":library:mvvm")
include(":library:navigation")
include(":library:preferences")
include(":library:ui")

include(":benchmark")
include(":baselineprofile")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
