package com.mudita.chess.gameplay.fixtures

import com.mudita.chess.gameplay.fixtures.BoardPiecesData.BLACK_PLAYER_PIECES
import com.mudita.chess.gameplay.fixtures.BoardPiecesData.WHITE_PLAYER_PIECES
import com.mudita.chess.gameplay.fixtures.BoardSquaresData.BLACK_PLAYER_SQUARES
import com.mudita.chess.gameplay.fixtures.BoardSquaresData.WHITE_PLAYER_SQUARES
import com.mudita.chess.gameplay.game.ChessBoardState

internal object ChessBoardStateData {

    val WHITE_PLAYER_BOARD = ChessBoardState(
        squares = WHITE_PLAYER_SQUARES,
        pieces = WHITE_PLAYER_PIECES,
        highlights = emptySet(),
        isMoveManualConfirmationRequired = false
    )
    val BLACK_PLAYER_BOARD = ChessBoardState(
        squares = BLACK_PLAYER_SQUARES,
        pieces = BLACK_PLAYER_PIECES,
        highlights = emptySet(),
        isMoveManualConfirmationRequired = false
    )
}
