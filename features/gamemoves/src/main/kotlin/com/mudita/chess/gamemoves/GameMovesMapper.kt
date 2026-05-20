package com.mudita.chess.gamemoves

import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Side.WHITE
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import com.mudita.chess.gamemoves.model.MoveUi
import com.mudita.chess.navigation.routes.MoveArg
import com.mudita.chess.ui.model.PieceTypeUi
import com.mudita.chess.ui.model.PieceUi
import com.mudita.chess.ui.model.PositionUi

internal class GameMovesMapper {

    fun toMovesUi(moves: List<MoveArg>): List<MoveUi> {
        return moves
            .asSequence()
            .zip(generateSequence(WHITE, Side::flip))
            .map { (moveArg, side) ->
                val movingPiece = Piece.fromFenSymbol(moveArg.pieceFenSymbol)
                val move = Move(moveArg.moveLAN, side)
                val pieceToDisplay = if (move.promotion != Piece.NONE) move.promotion else movingPiece
                MoveUi(pieceToDisplay.toUi(), move.from.toUi(), move.to.toUi())
            }
            .toList()
            .reversed()
    }

    private fun Square.toUi(): PositionUi =
        PositionUi.valueOf(this.name)

    private fun Piece.toUi(): PieceUi {
        check(this != Piece.NONE) {
            "Piece.NONE is not allowed here"
        }
        return PieceUi(type = pieceType.toUi(), isWhite = pieceSide == WHITE)
    }

    private fun PieceType.toUi(): PieceTypeUi =
        PieceTypeUi.valueOf(this.name)
}
