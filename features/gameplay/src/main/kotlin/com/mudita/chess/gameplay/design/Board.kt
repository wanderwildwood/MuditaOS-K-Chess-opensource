package com.mudita.chess.gameplay.design

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Side.BLACK
import com.mudita.chess.gameplay.BoardUi
import com.mudita.chess.gameplay.GameplayMapper
import com.mudita.chess.gameplay.game.ChessBoard
import com.mudita.chess.ui.model.PositionUi
import com.mudita.kompakt.commonUi.KompaktTheme

@Composable
internal fun Board(
    board: BoardUi,
    onSquareClick: (PositionUi) -> Unit,
    modifier: Modifier = Modifier
) = Column(modifier = modifier) {
    HorizontalFrame()
    Content(board, onSquareClick)
    HorizontalFrame()
}

@Composable
private fun HorizontalFrame() {
    HorizontalDivider(
        thickness = 2.dp,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun Content(board: BoardUi, onSquareClick: (PositionUi) -> Unit) {
    board.forEach { row ->
        Row {
            row.forEach { square ->
                Square(square, onSquareClick)
            }
        }
    }
}

@Preview
@Composable
private fun BoardPreview() = KompaktTheme {
    val mapper = GameplayMapper()
    Board(
        board = mapper.toBoardUi(ChessBoard(topParticipantSide = BLACK).state),
        onSquareClick = {}
    )
}
