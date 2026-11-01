package com.mudita.chess.main

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.ui.LicenceText
import com.mudita.chess.ui.LicencesData
import com.mudita.chess.ui.licenceParagraphs
import com.mudita.chess.ui.licencesProblems
import java.io.File
import java.net.URI
import org.junit.jupiter.api.Test

class LicencesTest {

    private fun raw(name: String) = File("src/main/res/raw/$name.txt").readText()

    private val data = LicencesData(
        "Chess+", SOURCE_URL, "GPL-3.0-only", LICENCE_COMPONENTS,
        LICENCE_RAW.map { LicenceText(it.first, it.first, it.second) },
    )

    @Test
    fun `the licence data has no problems`() {
        assertThat(licencesProblems(data)).isEmpty()
    }

    @Test
    fun `mudita and the engine stay credited`() {
        val names = LICENCE_COMPONENTS.map { it.name }
        assertThat(names.any { it.startsWith("Mudita's Chess") }).isTrue()
        assertThat(names.any { it.startsWith("Fairy-Stockfish") }).isTrue()
    }

    @Test
    fun `the source link is the apps repository on github`() {
        val uri = URI(SOURCE_URL)
        assertThat(uri.host).isEqualTo("github.com")
        assertThat(uri.path).isEqualTo("/wanderwildwood/MuditaOS-K-Chess-opensource")
    }

    @Test
    fun `the three bundled licence texts split into paragraphs`() {
        assertThat(licenceParagraphs(raw("gpl_3_0")).size).isGreaterThan(10)
        assertThat(licenceParagraphs(raw("apache_2_0")).size).isGreaterThan(10)
        assertThat(licenceParagraphs(raw("ofl_1_1")).size).isGreaterThan(5)
        assertThat(raw("apache_2_0")).contains("Apache License")
    }
}
