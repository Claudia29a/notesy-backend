package com.example.notesy

object PreferredItemsRepository {
    private val preferredItems = mutableListOf<PreferredItem>()
    private const val MAX_ITEMS = 10

    fun getAllItems(): List<PreferredItem> = preferredItems

    fun addItem(item: PreferredItem): Boolean {
        return if (preferredItems.size < MAX_ITEMS) {
            preferredItems.add(item)
            true
        } else {
            false // Cannot add more than 10 items
        }
    }

    fun removeItem(id: String): Boolean {
        return preferredItems.removeIf { it.id == id }
    }
}