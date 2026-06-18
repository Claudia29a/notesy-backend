package com.example.notesy

import kotlin.test.*

class OllamaServiceTest {

    @Test
    fun testExtractEmpty() {
        val raw = ""
        val out = extractOllamaResponseText(raw)
        assertEquals("", out)
    }

    @Test
    fun testExtractSingleLine() {
        val raw = "{\"response\":\"[\\\"milk\\\",\\\"eggs\\\"]\"}"
        val out = extractOllamaResponseText(raw)
        assertEquals("[\"milk\",\"eggs\"]", out)
    }

    @Test
    fun testExtractMultipleChunks() {
        val raw = "\n{\"response\":\"[\\\"milk\\\"]\"}\n{\"response\":\"[\\\"eggs\\\"]\"}\n"
        val out = extractOllamaResponseText(raw)
        // concatenates the responses from each decoded chunk
        assertEquals("[\"milk\"][\"eggs\"]", out)
    }

    @Test
    fun testExtractIgnoresInvalidLines() {
        val raw = "not-json\n{\"response\":\"[\\\"banana\\\"]\"}\n"
        val out = extractOllamaResponseText(raw)
        assertEquals("[\"banana\"]", out)
    }
}

