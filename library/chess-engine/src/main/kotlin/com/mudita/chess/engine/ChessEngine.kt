package com.mudita.chess.engine

interface ChessEngine {
    suspend fun start()
    suspend fun setOptions(options: Map<String, Any>)
    suspend fun newGame()
    suspend fun calculateBestMove(options: SearchOptions): String
    suspend fun cancelMoveCalculation()
    suspend fun stop()
}
