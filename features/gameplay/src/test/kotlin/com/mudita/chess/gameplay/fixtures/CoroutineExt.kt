package com.mudita.chess.gameplay.fixtures

import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

suspend fun <T> neverReturningCoroutine(): T = flow<T> { awaitCancellation() }.first()
