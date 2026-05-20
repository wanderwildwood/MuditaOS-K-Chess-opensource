package com.mudita.chess.engine.process

import kotlinx.coroutines.flow.Flow

internal interface ChessEngineProcess {
    fun start()
    suspend fun monitor()
    fun stop()
    fun writeLine(data: String)
    fun receiveLines(): Flow<String>
}
