package com.mudita.chess.ui.resourceprovider

import android.content.Context

internal class ResourceProviderImpl(
    private val context: Context
) : ResourceProvider {
    override fun getString(stringResId: Int): String =
        context.getString(stringResId)

    override fun getString(stringResId: Int, vararg arguments: Any?): String =
        context.getString(stringResId, *arguments)

    override fun getQuantityString(pluralResId: Int, quantity: Int): String =
        context.resources.getQuantityString(pluralResId, quantity)
}
