package com.mudita.chess.engine.di

import com.mudita.chess.coroutines.di.DispatcherQualifierName.IO
import com.mudita.chess.engine.ChessEngine
import com.mudita.chess.engine.ChessEngineImpl
import com.mudita.chess.engine.net.ChessEngineNet
import com.mudita.chess.engine.net.ChessEngineNetImpl
import com.mudita.chess.engine.process.ChessEngineProcess
import com.mudita.chess.engine.process.ChessEngineProcessImpl
import com.mudita.chess.engine.process.ProcessBuilderProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import java.io.File

/** Packed as a library so the installer unpacks it where it may be executed. */
private const val ENGINE_BINARY = "libstockfish.so"

val chessEngineModule = module {
    single<ChessEngineNet> { ChessEngineNetImpl(androidContext()) }

    single<ChessEngineProcess> {
        val ioDispatcher = get<CoroutineDispatcher>(named(IO))
        val engineContext = CoroutineName("EngineProcess") + ioDispatcher
        val engineFile = File(androidContext().applicationInfo.nativeLibraryDir, ENGINE_BINARY)
        ChessEngineProcessImpl(engineContext, engineFile, ProcessBuilderProvider.DEFAULT)
    }

    singleOf(::ChessEngineImpl) bind ChessEngine::class
}
