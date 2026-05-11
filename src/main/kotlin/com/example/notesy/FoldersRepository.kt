package com.example.notesy

object FoldersRepository {
    private val folders = mutableListOf<Folder>()

    fun getAllFolders(): List<Folder> = folders

    fun getFolderById(id: String): Folder? = folders.find { it.id == id }

    fun addFolder(folder: Folder) {
        folders.add(folder)
    }

    fun updateFolder(id: String, updatedFolder: Folder): Boolean {
        val index = folders.indexOfFirst { it.id == id }
        return if (index != -1) {
            folders[index] = updatedFolder
            true
        } else {
            false
        }
    }

    fun deleteFolder(id: String): Boolean {
        return folders.removeIf { it.id == id }
    }
}