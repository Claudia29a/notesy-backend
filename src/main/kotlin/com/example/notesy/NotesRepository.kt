package com.example.notesy

object NotesRepository {
    private val notes = mutableListOf<Note>()  // Changed from GroceryNote

    fun getAllNotes(): List<Note> = notes

    fun getNoteById(id: String): Note? = notes.find { it.id == id }

    fun getNotesByFolder(folderId: String): List<Note> =
        notes.filter { it.folderId == folderId }

    fun addNote(note: Note) {  // Changed from GroceryNote
        notes.add(note)
    }

    fun updateNote(id: String, updatedNote: Note): Boolean {  // Changed from GroceryNote
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