package com.mudita.chess.gameplay.design

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mudita.chess.gameplay.model.ParticipantUi
import com.mudita.chess.ui.R
import com.mudita.kompakt.commonUi.KompaktTheme
import com.mudita.kompakt.commonUi.KompaktTypography900
import com.mudita.kompakt.commonUi.colorWhite
import com.mudita.chess.frontitude.R as RFrontitude

@Composable
internal fun Participant(
    participant: ParticipantUi,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        if (participant.isSelected) {
            Image(
                painter = painterResource(id = R.drawable.frame_dashed),
                contentScale = ContentScale.FillBounds,
                contentDescription = null
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val participantIconResId = getParticipantIconResId(participant)
            Image(
                modifier = Modifier.size(34.dp),
                painter = painterResource(id = participantIconResId),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = participant.nameResId).uppercase(),
                style = KompaktTypography900.displaySmall
            )
        }
    }
}

@Composable
@DrawableRes
private fun getParticipantIconResId(participant: ParticipantUi) =
    if (participant.isWhite) {
        R.drawable.ic_knight_white_outline_black
    } else {
        R.drawable.ic_knight_black_no_outline
    }

@Preview
@Composable
private fun ParticipantPreview() = KompaktTheme {
    Column {
        Participant(
            modifier = Modifier
                .background(colorWhite)
                .padding(8.dp),
            participant = ParticipantUi(
                nameResId = RFrontitude.string.common_label_computer,
                isWhite = true,
                isSelected = true
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Participant(
            modifier = Modifier
                .background(colorWhite)
                .padding(8.dp),
            participant = ParticipantUi(
                nameResId = RFrontitude.string.common_label_you,
                isWhite = false,
                isSelected = false
            )
        )
    }
}
