package com.mudita.chess.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.BundleCompat
import androidx.navigation.NavType
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

@Suppress("FunctionNaming", "ktlint:standard:function-naming")
inline fun <reified T> ParcelableListType(
    isNullableAllowed: Boolean = false,
    json: Json = Json
) where T : Any, T : Parcelable = ParcelableListType(isNullableAllowed, T::class, json)

class ParcelableListType<T>(
    isNullableAllowed: Boolean = false,
    private val clazz: KClass<T>,
    private val json: Json = Json
) : NavType<List<T>>(isNullableAllowed = isNullableAllowed) where T : Any, T : Parcelable {

    override fun put(bundle: Bundle, key: String, value: List<T>) =
        bundle.putParcelableArrayList(key, ArrayList(value))

    override fun get(bundle: Bundle, key: String): List<T>? =
        BundleCompat.getParcelableArrayList(bundle, key, clazz.java)

    override fun serializeAsValue(value: List<T>): String =
        json.encodeToString(ListSerializer(serializer(clazz.java)), value)

    @Suppress("UNCHECKED_CAST")
    override fun parseValue(value: String): List<T> =
        json.decodeFromString(ListSerializer(serializer(clazz.java)), value) as List<T>
}
