package com.mudita.chess.gameplay.model

import androidx.annotation.StringRes

/**
 * A finished game, shown along the bottom of the screen rather than over the board.
 *
 * This used to be a dialog, which meant the last thing a game did was hide the position that
 * decided it. The result is a line of text and the things you can do next; none of it needs to
 * sit on top of the board to be read.
 */
internal data class EndgameUi(
    @StringRes val resultResId: Int
)
