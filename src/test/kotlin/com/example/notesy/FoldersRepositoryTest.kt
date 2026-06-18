package com.example.notesy

import kotlin.test.*

class FoldersRepositoryTest {

    @BeforeTest
    fun clearFolders() {
        FoldersRepository.getAllFolders().map { it.id }.forEach { FoldersRepository.deleteFolder(it) }
    }

    @Test
    fun testAddAndGetFolder() {
        val folder = Folder(id = "f1", name = "Folder 1", createdAt = "now")
        FoldersRepository.addFolder(folder)

        val all = FoldersRepository.getAllFolders()
        assertEquals(1, all.size)

        val fetched = FoldersRepository.getFolderById("f1")
        assertNotNull(fetched)
        assertEquals("Folder 1", fetched?.name)
    }

    @Test
    fun testUpdateFolder() {
        val folder = Folder(id = "f2", name = "Old", createdAt = "then")
        FoldersRepository.addFolder(folder)

        val updated = Folder(id = "f2", name = "New", createdAt = "then")
        val ok = FoldersRepository.updateFolder("f2", updated)
        assertTrue(ok)

        val fetched = FoldersRepository.getFolderById("f2")
        assertNotNull(fetched)
        assertEquals("New", fetched?.name)
    }

    @Test
    fun testDeleteFolder() {
        val folder = Folder(id = "f3", name = "ToDelete", createdAt = "now")
        FoldersRepository.addFolder(folder)

        val deleted = FoldersRepository.deleteFolder("f3")
        assertTrue(deleted)

        val fetched = FoldersRepository.getFolderById("f3")
        assertNull(fetched)
    }
}

