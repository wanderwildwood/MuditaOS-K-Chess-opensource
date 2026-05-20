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
        return if (tmpFile.parentFile?.mkdirs() == true && tmpFile.createNewFile()) {
            appContext.assets.open(DEFAULT_NET).copyTo(tmpFile.outputStream())
            tmpFile.renameTo(netFile)
            netFile
        } else {
            null
        }
    }

    private companion object {
        const val NET_DIR = "chess-engine/net"
        const val DEFAULT_NET = "nn-3475407dc199.nnue"
    }
}
