package com.example.notesy

import kotlinx.serialization.Serializable

@Serializable
data class GroceryNote(
    val id: String,
    val title: String,
    val items: List<GroceryItem>,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class GroceryItem(
    val name: String,
    val isChecked: Boolean = false
)