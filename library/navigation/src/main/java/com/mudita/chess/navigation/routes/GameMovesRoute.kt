package com.mudita.chess.navigation.routes

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.mudita.chess.navigation.ParcelableListType
import com.mudita.chess.navigation.Route
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
data class GameMovesRoute(
    val moves: List<MoveArg>
) : Route {
    companion object {
        val typeMap = mapOf(typeOf<List<MoveArg>>() to ParcelableListType<MoveArg>())

        fun from(savedStateHandle: SavedStateHandle) =
            savedStateHandle.toRoute<GameMovesRoute>(typeMap)
    }
}

@Parcelize
@Serializable
data class MoveArg(
    val pieceFenSymbol: String,
    val moveLAN: String
) : Parcelable
