package com.mudita.chess.engine.process

internal fun interface ProcessBuilderProvider {
    fun provide(command: String): ProcessBuilder

    companion object {
        val DEFAULT = ProcessBuilderProvider { command -> ProcessBuilder(command) }
    }
}
