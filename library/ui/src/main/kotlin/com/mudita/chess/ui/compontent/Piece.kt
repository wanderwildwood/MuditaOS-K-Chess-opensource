package com.mudita.chess.ui.compontent

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mudita.chess.ui.R
import com.mudita.chess.ui.model.PieceTypeUi
import com.mudita.chess.ui.model.PieceTypeUi.BISHOP
import com.mudita.chess.ui.model.PieceTypeUi.KING
import com.mudita.chess.ui.model.PieceTypeUi.KNIGHT
import com.mudita.chess.ui.model.PieceTypeUi.PAWN
import com.mudita.chess.ui.model.PieceTypeUi.QUEEN
import com.mudita.chess.ui.model.PieceTypeUi.ROOK
import com.mudita.chess.ui.design.AppTheme
import com.mudita.chess.ui.model.PieceUi

@Composable
fun Piece(
    piece: PieceUi,
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier.size(32.dp),
        painter = painterResource(id = getPieceResId(piece)),
        contentDescription = null
    )
}

@Composable
@DrawableRes
private fun getPieceResId(piece: PieceUi) =
    if (piece.isWhite) {
        getWhitePieceResId(piece)
    } else {
        getBlackPieceResId(piece)
    }

@Composable
@DrawableRes
private fun getWhitePieceResId(piece: PieceUi) =
    when (piece.type) {
        PAWN -> R.drawable.ic_pawn_white_outline_black
        ROOK -> R.drawable.ic_rook_white_outline_black
        KNIGHT -> R.drawable.ic_knight_white_outline_black
        BISHOP -> R.drawable.ic_bishop_white_outline_black
        QUEEN -> R.drawable.ic_queen_white_outline_black
        KING -> R.drawable.ic_king_white_outline_black
    }

@Composable
@DrawableRes
private fun getBlackPieceResId(piece: PieceUi) =
    when (piece.type) {
        PAWN -> R.drawable.ic_pawn_black_outline_white
        ROOK -> R.drawable.ic_rook_black_outline_white
        KNIGHT -> R.drawable.ic_knight_black_outline_white
        BISHOP -> R.drawable.ic_bishop_black_outline_white
        QUEEN -> R.drawable.ic_queen_black_outline_white
        KING -> R.drawable.ic_king_black_outline_white
    }

@Preview
@Composable
private fun PiecePreview() = AppTheme {
    Column {
        AllPieces(isWhite = true)
        AllPieces(isWhite = false)
    }
}

@Composable
private fun AllPieces(isWhite: Boolean) {
    Row {
        PieceTypeUi.entries.forEach { pieceType ->
            Piece(piece = PieceUi(pieceType, isWhite))
        }
    }
}
