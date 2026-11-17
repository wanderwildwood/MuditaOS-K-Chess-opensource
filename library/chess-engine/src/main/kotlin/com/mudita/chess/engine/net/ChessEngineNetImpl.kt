package com.mudita.chess.engine.net

import android.content.Context
import java.io.File

internal class ChessEngineNetImpl(
    private val appContext: Context
) : ChessEngineNet {

    override fun load(): File? {
        val netDir = File("${appContext.filesDir}/$NET_DIR")
        val netFile = File(netDir, DEFAULT_NET)
        if (netFile.exists()) return netFile
        val tmpFile = File(netDir, "$DEFAULT_NET.tmp")
        // A copy cut short last time (the app stopped, or the disk full) leaves its .tmp behind;
        // it is started again from the beginning, not taken as a reason to go without the net.
        return runCatching {
            if (!netDir.isDirectory && !netDir.mkdirs()) return null
            tmpFile.delete()
            appContext.assets.open(DEFAULT_NET).use { input ->
                java.io.FileOutputStream(tmpFile).use { output ->
                    input.copyTo(output)
                    output.fd.sync()
                }
            }
            if (tmpFile.renameTo(netFile)) netFile else null
        }.getOrElse {
            tmpFile.delete()
            null
        }
    }

    private companion object {
        const val NET_DIR = "chess-engine/net"
        const val DEFAULT_NET = "nn-3475407dc199.nnue"
    }
}
