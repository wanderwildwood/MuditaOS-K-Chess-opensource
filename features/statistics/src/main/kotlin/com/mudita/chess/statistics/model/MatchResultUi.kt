package com.mudita.chess.statistics.model

import androidx.annotation.StringRes

internal data class MatchResultUi(
    @StringRes val titleResId: Int,
    val value: Int,
    /** Shown with a percent sign, so a share of games does not read as a count of them. */
    val isPercentage: Boolean = false
)
