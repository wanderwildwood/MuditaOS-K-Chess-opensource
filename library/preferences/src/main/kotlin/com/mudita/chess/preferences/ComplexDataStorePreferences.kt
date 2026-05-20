package com.mudita.chess.preferences

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class ComplexDataStorePreferences<T : Any>(
    private val context: Context,
    name: String,
    serializer: Serializer<T>
) : ComplexPreferences<T> {

    private val Context.store by dataStore(fileName = name, serializer = serializer)

    override suspend fun get(): T? {
        return context.store.data.firstOrNull()
    }

    override fun observe(): Flow<T?> {
        return context.store.data
    }

    override suspend fun put(transform: suspend (T) -> T) {
        context.store.updateData(transform)
    }
}
