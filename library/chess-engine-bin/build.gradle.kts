plugins {
    alias(libs.plugins.android.library.convention)
}

android {
    namespace = "com.mudita.chess.engine.bin"

    defaultConfig {
        @Suppress("UnstableApiUsage")
        externalNativeBuild {
            ndkVersion = "24.0.8215888"
            ndkBuild {
                abiFilters.clear()
                //noinspection ChromeOsAbiSupport
                abiFilters += "arm64-v8a"
                arguments += "-j8"
            }
        }
    }

    externalNativeBuild {
        ndkBuild {
            path = file("src/main/jni/Android.mk")
        }
    }
}

val unzipSources = project.tasks.register<Copy>("unzipSources") {
    from(zipTree("src/main/jni/Fairy-Stockfish-fairy_sf_14.zip")) {
        include("**/src/**")
        exclude("**/ffishjs.cpp", "**/pyffish.cpp")
    }
    into("src/main/jni/fairy-stockfish")

    includeEmptyDirs = false
    eachFile {
        relativePath = RelativePath(true, *relativePath.segments.drop(2).toTypedArray())
    }
}

tasks.named("preBuild").configure {
    dependsOn(unzipSources)
}

project.tasks.register<Copy>("copyBinaries") {
    dependsOn("externalNativeBuildRelease")

    from("build/intermediates/ndkBuild/release/obj/local") {
        include("*/stockfish")
    }
    into("../chess-engine/src/main/jniLibs")
    include("stockfish")
    rename { filename ->
        filename.replace("stockfish", "libstockfish.so")
    }
}
