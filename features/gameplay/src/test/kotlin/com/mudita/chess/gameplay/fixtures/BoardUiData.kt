package com.mudita.chess.gameplay.fixtures

import com.mudita.chess.gameplay.game.ChessBoard.Companion.BOARD_SIZE
import com.mudita.chess.ui.model.PieceTypeUi.BISHOP
import com.mudita.chess.ui.model.PieceTypeUi.KING
import com.mudita.chess.ui.model.PieceTypeUi.KNIGHT
import com.mudita.chess.ui.model.PieceTypeUi.PAWN
import com.mudita.chess.ui.model.PieceTypeUi.QUEEN
import com.mudita.chess.ui.model.PieceTypeUi.ROOK
import com.mudita.chess.ui.model.PieceUi
import com.mudita.chess.ui.model.PositionUi
import com.mudita.chess.ui.model.PositionUi.A1
import com.mudita.chess.ui.model.PositionUi.A2
import com.mudita.chess.ui.model.PositionUi.A3
import com.mudita.chess.ui.model.PositionUi.A4
import com.mudita.chess.ui.model.PositionUi.A5
import com.mudita.chess.ui.model.PositionUi.A6
import com.mudita.chess.ui.model.PositionUi.A7
import com.mudita.chess.ui.model.PositionUi.A8
import com.mudita.chess.ui.model.PositionUi.B1
import com.mudita.chess.ui.model.PositionUi.B2
import com.mudita.chess.ui.model.PositionUi.B3
import com.mudita.chess.ui.model.PositionUi.B4
import com.mudita.chess.ui.model.PositionUi.B5
import com.mudita.chess.ui.model.PositionUi.B6
import com.mudita.chess.ui.model.PositionUi.B7
import com.mudita.chess.ui.model.PositionUi.B8
import com.mudita.chess.ui.model.PositionUi.C1
import com.mudita.chess.ui.model.PositionUi.C2
import com.mudita.chess.ui.model.PositionUi.C3
import com.mudita.chess.ui.model.PositionUi.C4
import com.mudita.chess.ui.model.PositionUi.C5
import com.mudita.chess.ui.model.PositionUi.C6
import com.mudita.chess.ui.model.PositionUi.C7
import com.mudita.chess.ui.model.PositionUi.C8
import com.mudita.chess.ui.model.PositionUi.D1
import com.mudita.chess.ui.model.PositionUi.D2
import com.mudita.chess.ui.model.PositionUi.D3
import com.mudita.chess.ui.model.PositionUi.D4
import com.mudita.chess.ui.model.PositionUi.D5
import com.mudita.chess.ui.model.PositionUi.D6
import com.mudita.chess.ui.model.PositionUi.D7
import com.mudita.chess.ui.model.PositionUi.D8
import com.mudita.chess.ui.model.PositionUi.E1
import com.mudita.chess.ui.model.PositionUi.E2
import com.mudita.chess.ui.model.PositionUi.E3
import com.mudita.chess.ui.model.PositionUi.E4
import com.mudita.chess.ui.model.PositionUi.E5
import com.mudita.chess.ui.model.PositionUi.E6
import com.mudita.chess.ui.model.PositionUi.E7
import com.mudita.chess.ui.model.PositionUi.E8
import com.mudita.chess.ui.model.PositionUi.F1
import com.mudita.chess.ui.model.PositionUi.F2
import com.mudita.chess.ui.model.PositionUi.F3
import com.mudita.chess.ui.model.PositionUi.F4
import com.mudita.chess.ui.model.PositionUi.F5
import com.mudita.chess.ui.model.PositionUi.F6
import com.mudita.chess.ui.model.PositionUi.F7
import com.mudita.chess.ui.model.PositionUi.F8
import com.mudita.chess.ui.model.PositionUi.G1
import com.mudita.chess.ui.model.PositionUi.G2
import com.mudita.chess.ui.model.PositionUi.G3
import com.mudita.chess.ui.model.PositionUi.G4
import com.mudita.chess.ui.model.PositionUi.G5
import com.mudita.chess.ui.model.PositionUi.G6
import com.mudita.chess.ui.model.PositionUi.G7
import com.mudita.chess.ui.model.PositionUi.G8
import com.mudita.chess.ui.model.PositionUi.H1
import com.mudita.chess.ui.model.PositionUi.H2
import com.mudita.chess.ui.model.PositionUi.H3
import com.mudita.chess.ui.model.PositionUi.H4
import com.mudita.chess.ui.model.PositionUi.H5
import com.mudita.chess.ui.model.PositionUi.H6
import com.mudita.chess.ui.model.PositionUi.H7
import com.mudita.chess.ui.model.PositionUi.H8
import com.mudita.chess.gameplay.model.SquareUi

internal object BoardUiData {

    private fun createEmptyBlack(position: PositionUi) =
        SquareUi(
            position = position,
            piece = null,
            isWhite = false
        )

    private fun createEmptyWhite(position: PositionUi) =
        SquareUi(
            position = position,
            piece = null,
            isWhite = true
        )

    val WHITE_PLAYER_POSITION_UIS = listOf(
        listOf(A8, B8, C8, D8, E8, F8, G8, H8),
        listOf(A7, B7, C7, D7, E7, F7, G7, H7),
        listOf(A6, B6, C6, D6, E6, F6, G6, H6),
        listOf(A5, B5, C5, D5, E5, F5, G5, H5),
        listOf(A4, B4, C4, D4, E4, F4, G4, H4),
        listOf(A3, B3, C3, D3, E3, F3, G3, H3),
        listOf(A2, B2, C2, D2, E2, F2, G2, H2),
        listOf(A1, B1, C1, D1, E1, F1, G1, H1)
    )

    private val BLACK_PLAYER_POSITION_UIS = listOf(
        listOf(H1, G1, F1, E1, D1, C1, B1, A1),
        listOf(H2, G2, F2, E2, D2, C2, B2, A2),
        listOf(H3, G3, F3, E3, D3, C3, B3, A3),
        listOf(H4, G4, F4, E4, D4, C4, B4, A4),
        listOf(H5, G5, F5, E5, D5, C5, B5, A5),
        listOf(H6, G6, F6, E6, D6, C6, B6, A6),
        listOf(H7, G7, F7, E7, D7, C7, B7, A7),
        listOf(H8, G8, F8, E8, D8, C8, B8, A8)
    )

    private fun createWhiteBlackRow(positions: List<PositionUi>) =
        List(BOARD_SIZE) { if (it % 2 == 0) createEmptyWhite(positions[it]) else createEmptyBlack(positions[it]) }

    private fun createBlackWhiteRow(positions: List<PositionUi>) =
        List(BOARD_SIZE) { if (it % 2 == 0) createEmptyBlack(positions[it]) else createEmptyWhite(positions[it]) }

    val WHITE_PLAYER_EMPTY_BOARD_UI = List(BOARD_SIZE) {
        if (it % 2 == 0) {
            createWhiteBlackRow(WHITE_PLAYER_POSITION_UIS[it])
        } else {
            createBlackWhiteRow(WHITE_PLAYER_POSITION_UIS[it])
        }
    }

    val BLACK_PLAYER_EMPTY_BOARD_UI = List(BOARD_SIZE) {
        if (it % 2 == 0) {
            createWhiteBlackRow(BLACK_PLAYER_POSITION_UIS[it])
        } else {
            createBlackWhiteRow(BLACK_PLAYER_POSITION_UIS[it])
        }
    }

    val WHITE_PLAYER_INITIAL_BOARD_UI = listOf(
        listOf(
            SquareUi(position = A8, piece = PieceUi(type = ROOK, isWhite = false), isWhite = true),
            SquareUi(position = B8, piece = PieceUi(type = KNIGHT, isWhite = false), isWhite = false),
            SquareUi(position = C8, piece = PieceUi(type = BISHOP, isWhite = false), isWhite = true),
            SquareUi(position = D8, piece = PieceUi(type = QUEEN, isWhite = false), isWhite = false),
            SquareUi(position = E8, piece = PieceUi(type = KING, isWhite = false), isWhite = true),
            SquareUi(position = F8, piece = PieceUi(type = BISHOP, isWhite = false), isWhite = false),
            SquareUi(position = G8, piece = PieceUi(type = KNIGHT, isWhite = false), isWhite = true),
            SquareUi(position = H8, piece = PieceUi(type = ROOK, isWhite = false), isWhite = false),
        ),
        listOf(
            SquareUi(position = A7, piece = PieceUi(type = PAWN, isWhite = false), isWhite = false),
            SquareUi(position = B7, piece = PieceUi(type = PAWN, isWhite = false), isWhite = true),
            SquareUi(position = C7, piece = PieceUi(type = PAWN, isWhite = false), isWhite = false),
            SquareUi(position = D7, piece = PieceUi(type = PAWN, isWhite = false), isWhite = true),
            SquareUi(position = E7, piece = PieceUi(type = PAWN, isWhite = false), isWhite = false),
            SquareUi(position = F7, piece = PieceUi(type = PAWN, isWhite = false), isWhite = true),
            SquareUi(position = G7, piece = PieceUi(type = PAWN, isWhite = false), isWhite = false),
            SquareUi(position = H7, piece = PieceUi(type = PAWN, isWhite = false), isWhite = true),
        ),
        createWhiteBlackRow(positions = listOf(A6, B6, C6, D6, E6, F6, G6, H6)),
        createBlackWhiteRow(positions = listOf(A5, B5, C5, D5, E5, F5, G5, H5)),
        createWhiteBlackRow(positions = listOf(A4, B4, C4, D4, E4, F4, G4, H4)),
        createBlackWhiteRow(positions = listOf(A3, B3, C3, D3, E3, F3, G3, H3)),
        listOf(
            SquareUi(position = A2, piece = PieceUi(type = PAWN, isWhite = true), isWhite = true),
            SquareUi(position = B2, piece = PieceUi(type = PAWN, isWhite = true), isWhite = false),
            SquareUi(position = C2, piece = PieceUi(type = PAWN, isWhite = true), isWhite = true),
            SquareUi(position = D2, piece = PieceUi(type = PAWN, isWhite = true), isWhite = false),
            SquareUi(position = E2, piece = PieceUi(type = PAWN, isWhite = true), isWhite = true),
            SquareUi(position = F2, piece = PieceUi(type = PAWN, isWhite = true), isWhite = false),
            SquareUi(position = G2, piece = PieceUi(type = PAWN, isWhite = true), isWhite = true),
            SquareUi(position = H2, piece = PieceUi(type = PAWN, isWhite = true), isWhite = false),
        ),
        listOf(
            SquareUi(position = A1, piece = PieceUi(type = ROOK, isWhite = true), isWhite = false),
            SquareUi(position = B1, piece = PieceUi(type = KNIGHT, isWhite = true), isWhite = true),
            SquareUi(position = C1, piece = PieceUi(type = BISHOP, isWhite = true), isWhite = false),
            SquareUi(position = D1, piece = PieceUi(type = QUEEN, isWhite = true), isWhite = true),
            SquareUi(position = E1, piece = PieceUi(type = KING, isWhite = true), isWhite = false),
            SquareUi(position = F1, piece = PieceUi(type = BISHOP, isWhite = true), isWhite = true),
            SquareUi(position = G1, piece = PieceUi(type = KNIGHT, isWhite = true), isWhite = false),
            SquareUi(position = H1, piece = PieceUi(type = ROOK, isWhite = true), isWhite = true),
        )
    )

    val BLACK_PLAYER_INITIAL_BOARD_UI = listOf(
        listOf(
            SquareUi(position = H1, piece = PieceUi(type = ROOK, isWhite = true), isWhite = true),
            SquareUi(position = G1, piece = PieceUi(type = KNIGHT, isWhite = true), isWhite = false),
            SquareUi(position = F1, piece = PieceUi(type = BISHOP, isWhite = true), isWhite = true),
            SquareUi(position = E1, piece = PieceUi(type = KING, isWhite = true), isWhite = false),
            SquareUi(position = D1, piece = PieceUi(type = QUEEN, isWhite = true), isWhite = true),
            SquareUi(position = C1, piece = PieceUi(type = BISHOP, isWhite = true), isWhite = false),
            SquareUi(position = B1, piece = PieceUi(type = KNIGHT, isWhite = true), isWhite = true),
            SquareUi(position = A1, piece = PieceUi(type = ROOK, isWhite = true), isWhite = false),
        ),
        listOf(
            SquareUi(position = H2, piece = PieceUi(type = PAWN, isWhite = true), isWhite = false),
            SquareUi(position = G2, piece = PieceUi(type = PAWN, isWhite = true), isWhite = true),
            SquareUi(position = F2, piece = PieceUi(type = PAWN, isWhite = true), isWhite = false),
            SquareUi(position = E2, piece = PieceUi(type = PAWN, isWhite = true), isWhite = true),
            SquareUi(position = D2, piece = PieceUi(type = PAWN, isWhite = true), isWhite = false),
            SquareUi(position = C2, piece = PieceUi(type = PAWN, isWhite = true), isWhite = true),
            SquareUi(position = B2, piece = PieceUi(type = PAWN, isWhite = true), isWhite = false),
            SquareUi(position = A2, piece = PieceUi(type = PAWN, isWhite = true), isWhite = true),
        ),
        createWhiteBlackRow(positions = listOf(H3, G3, F3, E3, D3, C3, B3, A3)),
        createBlackWhiteRow(positions = listOf(H4, G4, F4, E4, D4, C4, B4, A4)),
        createWhiteBlackRow(positions = listOf(H5, G5, F5, E5, D5, C5, B5, A5)),
        createBlackWhiteRow(positions = listOf(H6, G6, F6, E6, D6, C6, B6, A6)),
        listOf(
            SquareUi(position = H7, piece = PieceUi(type = PAWN, isWhite = false), isWhite = true),
            SquareUi(position = G7, piece = PieceUi(type = PAWN, isWhite = false), isWhite = false),
            SquareUi(position = F7, piece = PieceUi(type = PAWN, isWhite = false), isWhite = true),
            SquareUi(position = E7, piece = PieceUi(type = PAWN, isWhite = false), isWhite = false),
            SquareUi(position = D7, piece = PieceUi(type = PAWN, isWhite = false), isWhite = true),
            SquareUi(position = C7, piece = PieceUi(type = PAWN, isWhite = false), isWhite = false),
            SquareUi(position = B7, piece = PieceUi(type = PAWN, isWhite = false), isWhite = true),
            SquareUi(position = A7, piece = PieceUi(type = PAWN, isWhite = false), isWhite = false),
        ),
        listOf(
            SquareUi(position = H8, piece = PieceUi(type = ROOK, isWhite = false), isWhite = false),
            SquareUi(position = G8, piece = PieceUi(type = KNIGHT, isWhite = false), isWhite = true),
            SquareUi(position = F8, piece = PieceUi(type = BISHOP, isWhite = false), isWhite = false),
            SquareUi(position = E8, piece = PieceUi(type = KING, isWhite = false), isWhite = true),
            SquareUi(position = D8, piece = PieceUi(type = QUEEN, isWhite = false), isWhite = false),
            SquareUi(position = C8, piece = PieceUi(type = BISHOP, isWhite = false), isWhite = true),
            SquareUi(position = B8, piece = PieceUi(type = KNIGHT, isWhite = false), isWhite = false),
            SquareUi(position = A8, piece = PieceUi(type = ROOK, isWhite = false), isWhite = true),
        )
    )
}
