package com.example.notesy

object NotesRepository {
    private val notes = mutableListOf<GroceryNote>()

    fun getAllNotes(): List<GroceryNote> = notes

    fun getNoteById(id: String): GroceryNote? = notes.find { it.id == id }

    fun getNotesByFolder(folderId: String): List<GroceryNote> =
        notes.filter { it.folderId == folderId }

    fun addNote(note: GroceryNote) {
        notes.add(note)
    }

    fun updateNote(id: String, updatedNote: GroceryNote): Boolean {
        val index = notes.indexOfFirst { it.id == id }
        return if (index != -1) {
            notes[index] = updatedNote
            true
        } else {
            false
        }
    }

    fun deleteNote(id: String): Boolean {
        return notes.removeIf { it.id == id }
    }
}