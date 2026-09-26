package com.mudita.chess.gameplay.game

import com.github.bhlangonijr.chesslib.Side
import com.mudita.chess.gameoptions.model.GameOptions

interface Participant {
    val side: Side
    suspend fun setup(options: GameOptions)
    suspend fun doMove()
    suspend fun cleanup()

    /** Take back the last move(s) on this participant's turn. Only a human participant can. */
    fun undoMove() = Unit
}
