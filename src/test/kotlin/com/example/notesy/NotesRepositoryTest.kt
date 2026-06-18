package com.example.notesy

import kotlin.test.*

class NotesRepositoryTest {

    @BeforeTest
    fun clearNotes() {
        // remove any existing notes to keep tests isolated
        NotesRepository.getAllNotes().map { it.id }.forEach { NotesRepository.deleteNote(it) }
    }

    @Test
    fun testAddAndGetNote() {
        val note = Note(
            id = "n1",
            title = "Title 1",
            content = "Content 1",
            folderId = null,
            createdAt = "now"
        )

        NotesRepository.addNote(note)

        val all = NotesRepository.getAllNotes()
        assertEquals(1, all.size)

        val fetched = NotesRepository.getNoteById("n1")
        assertNotNull(fetched)
        assertEquals("Title 1", fetched?.title)
    }

    @Test
    fun testUpdateNote() {
        val note = Note(
            id = "n2",
            title = "Old",
            content = "old",
            folderId = null,
            createdAt = "now"
        )
        NotesRepository.addNote(note)

        val updated = Note(
            id = "n2",
            title = "New",
            content = "new",
            folderId = "f1",
            createdAt = "now"
        )

        val ok = NotesRepository.updateNote("n2", updated)
        assertTrue(ok)

        val fetched = NotesRepository.getNoteById("n2")
        assertNotNull(fetched)
        assertEquals("New", fetched?.title)
        assertEquals("f1", fetched?.folderId)
    }

    @Test
    fun testDeleteNote() {
        val note = Note(
            id = "n3",
            title = "ToDelete",
            content = "x",
            folderId = null,
            createdAt = "now"
        )
        NotesRepository.addNote(note)

        val deleted = NotesRepository.deleteNote("n3")
        assertTrue(deleted)

        val fetched = NotesRepository.getNoteById("n3")
        assertNull(fetched)
    }

    @Test
    fun testGetNotesByFolder() {
        // clean up any notes
        NotesRepository.getAllNotes().map { it.id }.forEach { NotesRepository.deleteNote(it) }

        val a = Note("n4", "A", "a", "folder-1", "now")
        val b = Note("n5", "B", "b", "folder-1", "now")
        val c = Note("n6", "C", "c", "folder-2", "now")

        NotesRepository.addNote(a)
        NotesRepository.addNote(b)
        NotesRepository.addNote(c)

        val folder1 = NotesRepository.getNotesByFolder("folder-1")
        assertEquals(2, folder1.size)

        val folder2 = NotesRepository.getNotesByFolder("folder-2")
        assertEquals(1, folder2.size)
    }
}

