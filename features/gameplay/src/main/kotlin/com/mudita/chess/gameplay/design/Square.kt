package com.mudita.chess.gameplay.design

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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

@Composable
internal fun Square(
    square: SquareUi,
    onClick: (PositionUi) -> Unit,
    modifier: Modifier = Modifier
) {
    val squareColor = if (square.isWhite) appColorWhite else appColorBlack

    Box(
        modifier = modifier
            .size(45.dp)
            .background(squareColor)
            .clickable { onClick(square.position) },
        contentAlignment = Alignment.Center
    ) {
        square.piece?.let { piece ->
            Piece(piece)
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
