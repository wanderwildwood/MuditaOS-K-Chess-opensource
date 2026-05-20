package com.mudita.chess.gameplay.design

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mudita.chess.ui.KompaktPreview
import com.mudita.kompakt.commonUi.KompaktTypography900
import com.mudita.kompakt.commonUi.compactColorScheme
import com.mudita.chess.frontitude.R as RFrontitude
import com.mudita.chess.ui.R as RCommonUi

@Composable
fun LoadingDialog(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(3.dp, compactColorScheme.primary, shape = RoundedCornerShape(16.dp))
            .background(color = compactColorScheme.secondary)
            .padding(vertical = 16.dp, horizontal = 48.dp)
    ) {
        Text(
            text = stringResource(RFrontitude.string.common_status_loading),
            style = KompaktTypography900.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(painter = painterResource(RCommonUi.drawable.spinner), contentDescription = null)
    }
}

@KompaktPreview
@Composable
private fun LoadingDialogPreview() {
    LoadingDialog()
}
