package com.mudita.chess.preferences

import android.content.Context
import com.mudita.chess.json.Json
import java.lang.reflect.Type

class PreferencesFactory(
    private val context: Context,
    private val json: Json
) {

    fun create(name: String): Preferences {
        return DataStorePreferences(context, name)
    }

    fun <T : Any> create(name: String, type: Type, initialValue: T): ComplexPreferences<T> {
        return ComplexDataStorePreferences(
            context,
            name,
            GenericSerializer(initialValue = initialValue, type = type, json = json)
        )
    }
}
