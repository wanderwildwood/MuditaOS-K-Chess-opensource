package com.mudita.chess.gameplay.fixtures

import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import com.mudita.chess.gameplay.game.CheckInfo
import com.mudita.chess.gameplay.game.ChessBoardState

internal fun ChessBoardState.withPieces(pieces: List<Piece>): ChessBoardState =
    copy(pieces = pieces)

internal fun ChessBoardState.replace(square: Square, piece: Piece): ChessBoardState =
    copy(pieces = pieces.replace(square indexIn squares, piece))

internal fun ChessBoardState.withSideToMove(side: Side): ChessBoardState =
    copy(sideToMove = side)

internal fun ChessBoardState.withHighlight(square: Square): ChessBoardState =
    withHighlights(setOf(square))

internal fun ChessBoardState.withHighlights(squares: Set<Square>): ChessBoardState =
    copy(highlights = squares)

internal fun ChessBoardState.withMoveManualConfirmationRequired(): ChessBoardState =
    copy(isMoveManualConfirmationRequired = true)


internal fun ChessBoardState.withPromotionManualConfirmationRequired(): ChessBoardState =
    copy(isPromotionManualConfirmationRequired = true)

internal fun ChessBoardState.withMoves(vararg moves: Move): ChessBoardState =
    copy(moves = moves.toList())

internal fun ChessBoardState.withCheckInfo(checkInfo: CheckInfo): ChessBoardState =
    copy(checkInfo = checkInfo)
