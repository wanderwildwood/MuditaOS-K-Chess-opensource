package com.mudita.chess.preferences

import androidx.datastore.core.Serializer
import com.mudita.chess.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Type

class GenericSerializer<T>(
    private val initialValue: T,
    private val type: Type,
    private val json: Json
) : Serializer<T> {

    override val defaultValue: T
        get() = initialValue

    override suspend fun readFrom(input: InputStream): T {
        return json.fromInputStream(input = input, type = type) ?: defaultValue
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        json.toOutputStream(output = output, value = t, type = type)
    }
}
