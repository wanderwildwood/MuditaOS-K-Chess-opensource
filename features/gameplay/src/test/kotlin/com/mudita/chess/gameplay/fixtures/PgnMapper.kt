package com.mudita.chess.gameplay.fixtures

import com.github.bhlangonijr.chesslib.pgn.GameLoader

internal fun toMovesLAN(pgn : String): List<String> {
    val lines = pgn.split("\n")
    return GameLoader.loadNextGame(lines.iterator())
        .currentMoveList.map { it.toString() }
}
