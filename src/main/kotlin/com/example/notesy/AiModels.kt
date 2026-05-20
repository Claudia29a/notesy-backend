package com.example.notesy

import kotlinx.serialization.Serializable

@Serializable
data class AiSuggestRequest(
    val noteText: String
)

@Serializable
data class AiSuggestResponse(
    val items: List<String>
)

@Serializable
data class AiErrorResponse(
    val error: String
)

@Serializable
data class OllamaGenerateRequest(
    val model: String,
    val prompt: String,
    val stream: Boolean = false
)

@Serializable
data class OllamaGenerateResponse(
    val response: String
)