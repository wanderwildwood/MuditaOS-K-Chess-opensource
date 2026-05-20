package com.mudita.chess.database

interface DatabaseProvider {
    fun provideDatabase(name: String): Database

    companion object Files {
        const val DEFAULT = "chess.db"
    }
}
