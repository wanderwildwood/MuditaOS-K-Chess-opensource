package com.mudita.chess.gameplay.fixtures

fun <T> Iterable<T>.replace(index: Int, value: T): List<T> {
    val mutable = toMutableList()
    mutable[index] = value
    return mutable.toList()
}
