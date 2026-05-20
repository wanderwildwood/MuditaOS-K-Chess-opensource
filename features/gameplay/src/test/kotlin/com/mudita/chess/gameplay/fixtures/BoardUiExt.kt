package com.mudita.chess.gameplay.fixtures

import com.mudita.chess.gameplay.BoardUi
import com.mudita.chess.ui.model.PositionUi
import com.mudita.chess.gameplay.model.SquareUi

internal operator fun BoardUi.get(position: PositionUi): SquareUi =
    flatten().first { it.position == position }

internal fun BoardUi.replace(value: SquareUi): List<List<SquareUi>> {
    val mutable = toMutableList()
    return mutable.map { row ->
        row.map { square ->
            if (square.position == value.position) value else square
        }
    }.toList()
}
