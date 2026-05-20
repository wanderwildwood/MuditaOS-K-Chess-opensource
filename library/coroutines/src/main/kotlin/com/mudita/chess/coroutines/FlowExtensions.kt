package com.mudita.chess.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

fun <T> Flow<T>.toCatchingResult(): Flow<Result<T>> = this
    .map { resultOf { it } }
    .catch { emit(Result.failure(it)) }
