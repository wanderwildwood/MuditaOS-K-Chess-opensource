package com.mudita.chess.gameplay.fixtures

import com.github.bhlangonijr.chesslib.Square

infix fun Square.indexIn(squares: List<Square>) = squares.indexOf(this)
