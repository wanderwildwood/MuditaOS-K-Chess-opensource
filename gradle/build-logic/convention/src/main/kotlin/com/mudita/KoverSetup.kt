package com.mudita

import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension

fun setupKover(kover: KoverProjectExtension) = with(kover) {
    reports {
        filters {
            excludes {
                packages(
                    "com.mudita.chess.appinfo",
                    "com.mudita.chess.coroutines",
                    "com.mudita.chess.database",
                    "com.mudita.chess.gameloader",
                    "com.mudita.chess.navigation",
                    "com.mudita.chess.preferences",
                    "com.mudita.chess.root",
                    "com.mudita.chess.ui",
                    "com.mudita.chess.*.di",
                    "com.mudita.chess.*.design"
                )
                annotatedBy("*Generated*", "*Composable*")
                classes("com.mudita.chess.App", "*.BuildConfig", "*Initializer", "*Composable*")
            }
        }
    }
}
