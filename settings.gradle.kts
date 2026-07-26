pluginManagement {
    includeBuild("gradle/build-logic")

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://jitpack.io") }
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
