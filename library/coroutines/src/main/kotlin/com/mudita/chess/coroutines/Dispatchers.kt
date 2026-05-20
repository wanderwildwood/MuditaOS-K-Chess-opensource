package com.mudita.chess.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Dispatchers.Unconfined

class Dispatchers {
    fun default(): CoroutineDispatcher = Default
    fun io(): CoroutineDispatcher = IO
    fun main(): CoroutineDispatcher = Main
    fun mainImmediate(): CoroutineDispatcher = Main.immediate
    fun unconfined(): CoroutineDispatcher = Unconfined
}
