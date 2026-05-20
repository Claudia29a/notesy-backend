package com.example.notesy

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private val ollamaJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

private val httpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(ollamaJson)
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                println("HTTP Client: $message")
            }
        }
        level = LogLevel.INFO
    }
}

suspend fun suggestGroceriesWithOllama(noteText: String): List<String> {
    val ollamaBaseUrl = System.getenv("OLLAMA_BASE_URL") ?: "https://ollama.com/api"
    val ollamaModel = System.getenv("OLLAMA_MODEL") ?: "gpt-oss:20b"
    val ollamaApiKey = System.getenv("OLLAMA_API_KEY")

    val isCloudRequest = ollamaBaseUrl.startsWith("https://ollama.com")

    if (isCloudRequest && ollamaApiKey.isNullOrBlank()) {
        throw IllegalStateException("OLLAMA_API_KEY is missing for Ollama Cloud")
    }

    val prompt = """
        You extract grocery items from a user's note.

        Return ONLY a valid JSON array of strings.
        Do not explain anything.
        Do not return markdown.
        Do not return an object.

        Example output:
        ["milk","eggs","bananas"]

        Rules:
        - Include only grocery items the user may want to buy.
        - Remove duplicates.
        - Keep names short and clean.
        - If there are no grocery items, return [].

        User note:
        $noteText
    """.trimIndent()

    val response: OllamaGenerateResponse = httpClient.post("$ollamaBaseUrl/generate") {
        contentType(ContentType.Application.Json)

        if (!ollamaApiKey.isNullOrBlank()) {
            header(HttpHeaders.Authorization, "Bearer $ollamaApiKey")
        }

        setBody(
            OllamaGenerateRequest(
                model = ollamaModel,
                prompt = prompt,
                stream = false
            )
        )
    }.body()

    val rawText = response.response.trim()

    return try {
        ollamaJson.decodeFromString<List<String>>(rawText)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
    } catch (_: Exception) {
        emptyList()
    }
}