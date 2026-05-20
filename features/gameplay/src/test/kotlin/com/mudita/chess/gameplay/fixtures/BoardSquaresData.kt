package com.mudita.chess.gameplay.fixtures

import com.github.bhlangonijr.chesslib.Square.A1
import com.github.bhlangonijr.chesslib.Square.A2
import com.github.bhlangonijr.chesslib.Square.A3
import com.github.bhlangonijr.chesslib.Square.A4
import com.github.bhlangonijr.chesslib.Square.A5
import com.github.bhlangonijr.chesslib.Square.A6
import com.github.bhlangonijr.chesslib.Square.A7
import com.github.bhlangonijr.chesslib.Square.A8
import com.github.bhlangonijr.chesslib.Square.B1
import com.github.bhlangonijr.chesslib.Square.B2
import com.github.bhlangonijr.chesslib.Square.B3
import com.github.bhlangonijr.chesslib.Square.B4
import com.github.bhlangonijr.chesslib.Square.B5
import com.github.bhlangonijr.chesslib.Square.B6
import com.github.bhlangonijr.chesslib.Square.B7
import com.github.bhlangonijr.chesslib.Square.B8
import com.github.bhlangonijr.chesslib.Square.C1
import com.github.bhlangonijr.chesslib.Square.C2
import com.github.bhlangonijr.chesslib.Square.C3
import com.github.bhlangonijr.chesslib.Square.C4
import com.github.bhlangonijr.chesslib.Square.C5
import com.github.bhlangonijr.chesslib.Square.C6
import com.github.bhlangonijr.chesslib.Square.C7
import com.github.bhlangonijr.chesslib.Square.C8
import com.github.bhlangonijr.chesslib.Square.D1
import com.github.bhlangonijr.chesslib.Square.D2
import com.github.bhlangonijr.chesslib.Square.D3
import com.github.bhlangonijr.chesslib.Square.D4
import com.github.bhlangonijr.chesslib.Square.D5
import com.github.bhlangonijr.chesslib.Square.D6
import com.github.bhlangonijr.chesslib.Square.D7
import com.github.bhlangonijr.chesslib.Square.D8
import com.github.bhlangonijr.chesslib.Square.E1
import com.github.bhlangonijr.chesslib.Square.E2
import com.github.bhlangonijr.chesslib.Square.E3
import com.github.bhlangonijr.chesslib.Square.E4
import com.github.bhlangonijr.chesslib.Square.E5
import com.github.bhlangonijr.chesslib.Square.E6
import com.github.bhlangonijr.chesslib.Square.E7
import com.github.bhlangonijr.chesslib.Square.E8
import com.github.bhlangonijr.chesslib.Square.F1
import com.github.bhlangonijr.chesslib.Square.F2
import com.github.bhlangonijr.chesslib.Square.F3
import com.github.bhlangonijr.chesslib.Square.F4
import com.github.bhlangonijr.chesslib.Square.F5
import com.github.bhlangonijr.chesslib.Square.F6
import com.github.bhlangonijr.chesslib.Square.F7
import com.github.bhlangonijr.chesslib.Square.F8
import com.github.bhlangonijr.chesslib.Square.G1
import com.github.bhlangonijr.chesslib.Square.G2
import com.github.bhlangonijr.chesslib.Square.G3
import com.github.bhlangonijr.chesslib.Square.G4
import com.github.bhlangonijr.chesslib.Square.G5
import com.github.bhlangonijr.chesslib.Square.G6
import com.github.bhlangonijr.chesslib.Square.G7
import com.github.bhlangonijr.chesslib.Square.G8
import com.github.bhlangonijr.chesslib.Square.H1
import com.github.bhlangonijr.chesslib.Square.H2
import com.github.bhlangonijr.chesslib.Square.H3
import com.github.bhlangonijr.chesslib.Square.H4
import com.github.bhlangonijr.chesslib.Square.H5
import com.github.bhlangonijr.chesslib.Square.H6
import com.github.bhlangonijr.chesslib.Square.H7
import com.github.bhlangonijr.chesslib.Square.H8

internal object BoardSquaresData {
    val WHITE_PLAYER_SQUARES = listOf(
        listOf(A8, B8, C8, D8, E8, F8, G8, H8),
        listOf(A7, B7, C7, D7, E7, F7, G7, H7),
        listOf(A6, B6, C6, D6, E6, F6, G6, H6),
        listOf(A5, B5, C5, D5, E5, F5, G5, H5),
        listOf(A4, B4, C4, D4, E4, F4, G4, H4),
        listOf(A3, B3, C3, D3, E3, F3, G3, H3),
        listOf(A2, B2, C2, D2, E2, F2, G2, H2),
        listOf(A1, B1, C1, D1, E1, F1, G1, H1)
    ).flatten()

    val BLACK_PLAYER_SQUARES = listOf(
        listOf(H1, G1, F1, E1, D1, C1, B1, A1),
        listOf(H2, G2, F2, E2, D2, C2, B2, A2),
        listOf(H3, G3, F3, E3, D3, C3, B3, A3),
        listOf(H4, G4, F4, E4, D4, C4, B4, A4),
        listOf(H5, G5, F5, E5, D5, C5, B5, A5),
        listOf(H6, G6, F6, E6, D6, C6, B6, A6),
        listOf(H7, G7, F7, E7, D7, C7, B7, A7),
        listOf(H8, G8, F8, E8, D8, C8, B8, A8)
    ).flatten()
}
