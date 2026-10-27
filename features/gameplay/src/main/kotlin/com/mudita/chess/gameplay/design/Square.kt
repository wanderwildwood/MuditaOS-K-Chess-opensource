package com.mudita.chess.gameplay.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mudita.chess.ui.model.PieceTypeUi
import com.mudita.chess.ui.model.PieceUi
import com.mudita.chess.ui.model.PositionUi
import com.mudita.chess.ui.model.PositionUi.A1
import com.mudita.chess.gameplay.model.SquareUi
import com.mudita.chess.ui.compontent.Piece
import com.mudita.chess.ui.design.AppTheme
import com.mudita.chess.ui.design.appColorBlack
import com.mudita.chess.ui.design.appColorWhite

private val LAST_MOVE_INSET = 2.dp
private val LAST_MOVE_STROKE = 2.dp

@Composable
internal fun Square(
    square: SquareUi,
    onClick: (PositionUi) -> Unit,
    modifier: Modifier = Modifier
) {
    val squareColor = if (square.isWhite) appColorWhite else appColorBlack

    // Ink on the opposite colour to the square, so the mark reads on both and is never grey.
    val inkColor = if (square.isWhite) appColorBlack else appColorWhite

    Box(
        modifier = modifier
            .size(45.dp)
            .background(squareColor)
            .clickable { onClick(square.position) },
        contentAlignment = Alignment.Center
    ) {
        if (square.isLastMove) {
            // A frame set in from the edge with a gap of the square's own colour outside it, so it
            // reads as a mark on the square rather than the square shrinking.
            Box(
                Modifier
                    .matchParentSize()
                    .padding(LAST_MOVE_INSET)
                    .border(LAST_MOVE_STROKE, inkColor)
            )
        }
        square.piece?.let { piece ->
            // A black piece on a dark square has only its thin white outline to show it; a halo of
            // white behind it makes the shape plain.
            Piece(piece, haloed = !square.isWhite && !piece.isWhite)
        }
        if (square.isHighlighted) {
            PieceSelector(isWhite = !square.isWhite)
        }
    }
}

@Preview
@Composable
private fun SquarePreview() = AppTheme {
    Row {
        EmptySquares()
        SquareWithPieces()
        HighlightedSquares()
    }
}

@Composable
private fun EmptySquares() {
    Square(SquareUi(position = A1, isWhite = true), onClick = {})
    Square(SquareUi(position = A1, isWhite = false), onClick = {})
}

@Composable
private fun SquareWithPieces() {
    Square(
        SquareUi(
            position = A1,
            isWhite = true,
            piece = PieceUi(PieceTypeUi.ROOK, isWhite = false)
        ),
        onClick = {}
    )
    Square(
        SquareUi(
            position = A1,
            isWhite = false,
            piece = PieceUi(PieceTypeUi.KNIGHT, isWhite = true)
        ),
        onClick = {}
    )
}

@Composable
private fun HighlightedSquares() {
    Square(
        SquareUi(
            position = A1,
            isHighlighted = true,
            isWhite = true,
            piece = PieceUi(PieceTypeUi.ROOK, isWhite = false)
        ),
        onClick = {}
    )
    Square(
        SquareUi(
            position = A1,
            isHighlighted = true,
            isWhite = false,
            piece = PieceUi(PieceTypeUi.KNIGHT, isWhite = true)
        ),
        onClick = {}
    )
}
