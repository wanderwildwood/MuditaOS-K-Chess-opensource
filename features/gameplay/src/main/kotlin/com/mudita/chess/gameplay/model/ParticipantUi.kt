package com.mudita.chess.gameplay.model

import androidx.annotation.StringRes

internal data class ParticipantUi(
    @StringRes val nameResId: Int,
    val isWhite: Boolean,
    val isSelected: Boolean
)
