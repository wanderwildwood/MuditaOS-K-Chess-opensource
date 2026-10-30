package com.mudita.chess.main

import com.google.common.truth.Truth.assertThat
import java.io.File
import java.net.URI
import org.junit.jupiter.api.Test

class LicencesTest {

    private fun asset(name: String) = File("../../app-android/src/main/assets/$name").readText()

    @Test
    fun `every library has a name, a licence and an https link`() {
        assertThat(LIBRARIES).isNotEmpty()
        LIBRARIES.forEach {
            assertThat(URI(it.link).scheme).isEqualTo("https")
            assertThat(it.name).isNotEmpty()
            assertThat(it.licence).isNotEmpty()
        }
        assertThat(LIBRARIES.map { it.name }.toSet()).hasSize(LIBRARIES.size)
    }

    @Test
    fun `the source link is the apps repository on github`() {
        val uri = URI(SOURCE_URL)
        assertThat(uri.host).isEqualTo("github.com")
        assertThat(uri.path).isEqualTo("/wanderwildwood/MuditaOS-K-Chess-opensource")
    }

    @Test
    fun `the three bundled licence texts split into paragraphs`() {
        assertThat(asset("LICENSE.md").toParagraphs().size).isGreaterThan(10)
        assertThat(asset("licences/Apache-2.0.txt").toParagraphs().size).isGreaterThan(10)
        assertThat(asset("licences/OFL-1.1.txt").toParagraphs().size).isGreaterThan(5)
        assertThat(asset("licences/Apache-2.0.txt")).contains("Apache License")
    }

    @Test
    fun `paragraphs are joined into one row each`() {
        assertThat("One two\n three\n\n\nFour\r\nfive".toParagraphs()).containsExactly("One two three", "Four five").inOrder()
    }
}
