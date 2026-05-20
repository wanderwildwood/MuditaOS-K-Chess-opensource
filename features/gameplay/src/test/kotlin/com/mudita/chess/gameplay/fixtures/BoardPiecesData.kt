package com.mudita.chess.gameplay.fixtures

import com.github.bhlangonijr.chesslib.Piece.BLACK_BISHOP
import com.github.bhlangonijr.chesslib.Piece.BLACK_KING
import com.github.bhlangonijr.chesslib.Piece.BLACK_KNIGHT
import com.github.bhlangonijr.chesslib.Piece.BLACK_PAWN
import com.github.bhlangonijr.chesslib.Piece.BLACK_QUEEN
import com.github.bhlangonijr.chesslib.Piece.BLACK_ROOK
import com.github.bhlangonijr.chesslib.Piece.NONE
import com.github.bhlangonijr.chesslib.Piece.WHITE_BISHOP
import com.github.bhlangonijr.chesslib.Piece.WHITE_KING
import com.github.bhlangonijr.chesslib.Piece.WHITE_KNIGHT
import com.github.bhlangonijr.chesslib.Piece.WHITE_PAWN
import com.github.bhlangonijr.chesslib.Piece.WHITE_QUEEN
import com.github.bhlangonijr.chesslib.Piece.WHITE_ROOK

internal object BoardPiecesData {
    val WHITE_PLAYER_PIECES = listOf(
        listOf(BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK),
        listOf(BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN),
        listOf(NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE),
        listOf(NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE),
        listOf(NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE),
        listOf(NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE),
        listOf(WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN),
        listOf(WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK)
    ).flatten()

    val BLACK_PLAYER_PIECES = listOf(
        listOf(WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK),
        listOf(WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN),
        listOf(NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE),
        listOf(NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE),
        listOf(NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE),
        listOf(NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE),
        listOf(BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN),
        listOf(BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK)
    ).flatten()
}
