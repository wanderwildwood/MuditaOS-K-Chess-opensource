package com.mudita.chess.json

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class KotlinXSerializationJsonTest {

    private val json: Json = KotlinXSerializationJson()

    @Serializable
    data class ClassUnderTest(
        val testString: String,
        val testInt: Int
    )

    @Test
    fun `test simple object serialization`() {
        val clazz = ClassUnderTest(testString = "Test", testInt = 2)
        val expected = "{\"testString\":\"Test\",\"testInt\":2}"
        val actual = json.toJson(clazz)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `test simple object deserialization`() {
        val jsonText = "{\"testString\":\"Test\",\"testInt\":2}"
        val expected = ClassUnderTest(testString = "Test", testInt = 2)
        val actual = json.fromJson<ClassUnderTest>(jsonText)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `test simple object deserialization with missing field`() {
        val jsonText = "{\"testString\":\"Test\"}"

        val actual = json.fromJson<ClassUnderTest>(jsonText)

        assertThat(actual).isNull()
    }

    @Test
    fun `test object to output stream`() {
        val clazz = ClassUnderTest(testString = "Test", testInt = 2)
        val expected = "{\"testString\":\"Test\",\"testInt\":2}"

        val outputStream = ByteArrayOutputStream()
        json.toOutputStream(outputStream, clazz, ClassUnderTest::class.java)
        val actual = outputStream.toString()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `test json from input stream`() {
        val jsonText = "{\"testString\":\"Test\",\"testInt\":2}"
        val expected = ClassUnderTest(testString = "Test", testInt = 2)

        val inputStream = ByteArrayInputStream(jsonText.toByteArray())
        val actual: ClassUnderTest? =
            json.fromInputStream(inputStream, ClassUnderTest::class.java)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `test json from input stream with missing field`() {
        val jsonText = "{\"testString\":\"Test\"}"

        val inputStream = ByteArrayInputStream(jsonText.toByteArray())
        val actual = json.fromInputStream<ClassUnderTest>(inputStream, ClassUnderTest::class.java)

        assertThat(actual).isNull()
    }
}
