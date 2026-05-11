package com.example.notesy

import kotlinx.serialization.Serializable

@Serializable
data class Folder(
    val id: String,
    val name: String,
    val createdAt: String
)

@Serializable
data class CreateFolderRequest(
    val name: String
)

@Serializable
data class GroceryNote(
    val id: String,
    val title: String,
    val items: List<String>,
    val folderId: String?,
    val createdAt: String
)

@Serializable
data class CreateNoteRequest(
    val title: String,
    val items: List<String>,
    val folderId: String? = null
)