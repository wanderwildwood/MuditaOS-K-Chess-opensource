package com.mudita.chess.engine.process

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

internal class ChessEngineProcessImpl(
    private val engineContext: CoroutineContext,
    private val processBuilderProvider: ProcessBuilderProvider
) : ChessEngineProcess {

    private var process: Process? = null
    private var monitorJob: Job? = null

    private var incomeChannel: Channel<String>? = null

    override fun start() {
        if (process != null) return

        val processBuilder = processBuilderProvider.provide("./stockfish")
        processBuilder.directory(File("/system/bin/"))
        process = processBuilder.start()
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
        process?.outputStream?.write("$data\n".toByteArray())
        process?.outputStream?.flush()
    }

    override fun receiveLines(): Flow<String> {
        return incomeChannel?.receiveAsFlow() ?: emptyFlow()
    }
}
