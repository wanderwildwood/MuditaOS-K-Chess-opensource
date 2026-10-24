package com.mudita.chess.engine.process

import com.mudita.chess.engine.EngineUnavailableException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import logcat.logcat
import java.io.File
import java.io.IOException
import kotlin.coroutines.CoroutineContext

/**
 * Runs the engine this app ships, `libstockfish.so` from its own native library directory.
 *
 * It used to run `./stockfish` in `/system/bin`, which is a copy the Kompakt's firmware carries
 * for Mudita's own Chess app. That left the engine built from this repository unused, and
 * one-player games depending on whatever the phone's firmware happened to hold.
 */
internal class ChessEngineProcessImpl(
    private val engineContext: CoroutineContext,
    private val engineFile: File,
    private val processBuilderProvider: ProcessBuilderProvider
) : ChessEngineProcess {

    private var process: Process? = null
    private var monitorJob: Job? = null

    private var incomeChannel: Channel<String>? = null

    override fun start() {
        if (process != null) return

        try {
            val processBuilder = processBuilderProvider.provide(engineFile.absolutePath)
            processBuilder.directory(engineFile.parentFile)
            process = processBuilder.start()
        } catch (ex: IOException) {
            // "Cannot run program": the APK holds no engine for this processor, or it cannot be run.
            throw EngineUnavailableException("The engine could not be started: ${engineFile.absolutePath}", ex)
        } catch (ex: SecurityException) {
            throw EngineUnavailableException("The engine may not be started: ${engineFile.absolutePath}", ex)
        }
    }

    override suspend fun monitor() {
        if (process == null) return
        incomeChannel = Channel(Channel.BUFFERED)
        monitorJob = CoroutineScope(engineContext).launch {
            val inStream = process?.inputStream?.bufferedReader() ?: return@launch
            while (isActive && process?.isAlive == true) {
                val line = try {
                    inStream.readLine()
                } catch (ignored: IOException) {
                    null
                } ?: continue
                logcat("ChessEngineProcess") { "Engine -> App: $line" }
                incomeChannel?.send(line)
            }
            // The engine has gone. Ending the flow lets whoever is waiting on a line find out,
            // instead of waiting for ever on a process that will say nothing more.
            incomeChannel?.close()
        }
    }

    override fun stop() {
        monitorJob?.cancel()
        monitorJob = null
        incomeChannel?.close()
        incomeChannel = null
        process?.destroy()
        process = null
    }

    override fun writeLine(data: String) {
        logcat("ChessEngineProcess") { "App - > Engine: $data" }
        try {
            process?.outputStream?.write("$data\n".toByteArray())
            process?.outputStream?.flush()
        } catch (ex: IOException) {
            // A dead engine: the read side ends, and that is where the caller finds out.
            logcat("ChessEngineProcess") { "Engine is not taking input: $ex" }
        }
    }

    override fun receiveLines(): Flow<String> {
        return incomeChannel?.receiveAsFlow() ?: emptyFlow()
    }
}
