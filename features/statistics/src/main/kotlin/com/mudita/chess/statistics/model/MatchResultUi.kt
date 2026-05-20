package com.mudita.chess.statistics.model

import androidx.annotation.StringRes

internal data class MatchResultUi(
    @StringRes val titleResId: Int,
    val value: Int
)
