package com.mudita.chess.gameplay.di

import com.mudita.chess.coroutines.di.DispatcherQualifierName.IO
import com.mudita.chess.gameplay.GameplayMapper
import com.mudita.chess.gameplay.GameplayRouteProvider
import com.mudita.chess.gameplay.GameplayUiEvents
import com.mudita.chess.gameplay.GameplayViewModel
import com.mudita.chess.gameplay.game.MoveResultNotifier
import com.mudita.chess.gameplay.game.ChessBoard
import com.mudita.chess.gameplay.game.ComputerParticipant
import com.mudita.chess.gameplay.game.Game
import com.mudita.chess.gameplay.game.GameFactory
import com.mudita.chess.gameplay.game.PlayerParticipant
import com.mudita.chess.gameplay.usecase.GetComputerMoveUseCase
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val gameplayModule = module {
    factoryOf(::GameplayRouteProvider)
    factoryOf(::GameplayMapper)
    factoryOf(::GameplayUiEvents)

    factoryOf(::ChessBoard)
    factory { params ->
        MoveResultNotifier(
            board = params.get(),
            uiEvents = params.get()
        )
    }
    factory { params ->
        PlayerParticipant(
            side = params.get(),
            board = params.get(),
            moveResultNotifier = params.get(),
            uiEvents = params.get(),
            mapper = get()
        )
    }
    factory { params ->
        ComputerParticipant(
            side = params.get(),
            board = params.get(),
            moveResultNotifier = params.get(),
            engine = get(),
            getComputerMoveUseCase = get()
        )
    }

    factory { params ->
        Game(
            board = params.get(),
            whiteParticipant = params.get(),
            blackParticipant = params.get(),
            ioDispatcher = get(named(IO))
        )
    }
    factoryOf(::GameFactory)

    factory { GetComputerMoveUseCase(get(), get(named(IO))) }
    viewModelOf(::GameplayViewModel)
}
