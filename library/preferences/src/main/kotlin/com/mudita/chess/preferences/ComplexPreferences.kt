package com.mudita.chess.preferences

import kotlinx.coroutines.flow.Flow

interface ComplexPreferences<T : Any> {
    suspend fun get(): T?
    fun observe(): Flow<T?>
    suspend fun put(transform: suspend (T) -> T)
}
