package com.mudita.chess.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver.Companion.IN_MEMORY
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class SampleQueriesTest {

    private val inMemorySqlDriver = JdbcSqliteDriver(IN_MEMORY).apply {
        Database.Schema.create(this)
    }

    private val queries = Database(inMemorySqlDriver).sampleQueries

    @Test
    fun `test sample selectAll`() {
        val elements = queries.selectAll().executeAsList()
        assertThat(elements).hasSize(1)
        assertThat(elements.first()).isEqualTo(Sample(id = 15, name = "Miquido Mayor"))
    }

    @Test
    fun `test sample selectAll using coroutines`() {
        val elements: List<Sample>
        runBlocking { elements = queries.selectAll().asFlow().mapToList(coroutineContext).first() }
        assertThat(elements).hasSize(1)
        assertThat(elements.first()).isEqualTo(Sample(id = 15, name = "Miquido Mayor"))
    }
}
