package com.mudita.chess.database

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

internal class SqlDelightDatabaseProvider(
    private val context: Context
) : DatabaseProvider {

    override fun provideDatabase(name: String): Database {
        val driver = AndroidSqliteDriver(Database.Schema, context, name)
        return Database(driver)
    }
}
