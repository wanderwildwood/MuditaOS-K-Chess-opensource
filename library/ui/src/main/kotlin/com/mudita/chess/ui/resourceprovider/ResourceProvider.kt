package com.mudita.chess.ui.resourceprovider

interface ResourceProvider {
    fun getString(stringResId: Int): String
    fun getString(stringResId: Int, vararg arguments: Any? = arrayOf(Any())): String
    fun getQuantityString(pluralResId: Int, quantity: Int): String
}
