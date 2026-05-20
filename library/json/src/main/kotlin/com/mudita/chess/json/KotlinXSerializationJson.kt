package com.mudita.chess.json

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.okio.decodeFromBufferedSource
import kotlinx.serialization.json.okio.encodeToBufferedSink
import kotlinx.serialization.serializer
import okio.buffer
import okio.sink
import okio.source
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Type
import kotlinx.serialization.json.Json as KotlinXJson

internal class KotlinXSerializationJson : Json {

    private val xJson by lazy {
        KotlinXJson {
            ignoreUnknownKeys = true
            isLenient = true
            allowStructuredMapKeys = true
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> toJson(value: T, type: Type): String {
        val serializer = xJson.serializersModule.serializer(type) as KSerializer<T>
        return xJson.encodeToString(serializer, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> fromJson(json: String, type: Type): T? {
        return runCatching {
            val serializer = xJson.serializersModule.serializer(type) as KSerializer<T>
            xJson.decodeFromString(serializer, json)
        }.getOrNull()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    override fun <T> toOutputStream(output: OutputStream, value: T, type: Type) {
        val bufferedSink = output.sink().buffer()
        val serializer = xJson.serializersModule.serializer(type) as KSerializer<T>
        xJson.encodeToBufferedSink(serializer, value, bufferedSink)
        bufferedSink.flush()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    override fun <T> fromInputStream(input: InputStream, type: Type): T? {
        return runCatching {
            val serializer = xJson.serializersModule.serializer(type) as KSerializer<T>
            xJson.decodeFromBufferedSource(serializer, input.source().buffer())
        }.getOrNull()
    }
}
