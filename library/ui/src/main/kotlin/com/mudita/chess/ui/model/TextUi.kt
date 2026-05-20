package com.mudita.chess.ui.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mudita.chess.ui.model.TextUi.Raw
import com.mudita.chess.ui.model.TextUi.Res

sealed interface TextUi {
    data class Raw(val value: String) : TextUi
    data class Res(
        @StringRes val resource: Int,
        val args: Array<out Any> = emptyArray()
    ) : TextUi {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Res

            if (resource != other.resource) return false
            if (!args.contentEquals(other.args)) return false

            return true
        }

        override fun hashCode(): Int {
            return resource.hashCode() + args.contentHashCode()
        }
    }
}

@Composable
fun TextUi.stringify(): String = when (this) {
    is Raw -> value

    is Res ->
        @Suppress("SpreadOperator")
        stringResource(resource, *args)
}
