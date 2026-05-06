package com.example.notesy

import kotlinx.serialization.Serializable

@Serializable
data class GroceryNote(
    val id: String,
    val title: String,
    val items: List<String>,  // ✅ Changed from List<GroceryItem>
    val createdAt: String      // ✅ Changed from Long to String
)

@Serializable
data class CreateNoteRequest(
    val title: String,
    val items: List<String>
)

@Serializable
data class PreferredItem(
    val id: String,
    val itemName: String,
    val addedAt: Long
)