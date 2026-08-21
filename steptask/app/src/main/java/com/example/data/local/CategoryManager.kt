package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("task_category_prefs", Context.MODE_PRIVATE)

    private val defaultCategories = listOf("Work", "Tech", "Life & Home", "Study", "Writing", "Health")

    private val _categories = MutableStateFlow(loadCategories())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private fun loadCategories(): List<String> {
        val saved = prefs.getString(KEY_CATEGORIES, null)
        return if (saved.isNullOrBlank()) {
            defaultCategories
        } else {
            val list = saved.split(DELIMITER).map { it.trim() }.filter { it.isNotEmpty() }
            if (list.isEmpty()) defaultCategories else list
        }
    }

    private fun saveCategories(list: List<String>) {
        val distinctList = list.distinct()
        _categories.value = distinctList
        prefs.edit().putString(KEY_CATEGORIES, distinctList.joinToString(DELIMITER)).apply()
    }

    fun addCategory(name: String): Boolean {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || trimmed.equals("All", ignoreCase = true)) {
            return false
        }
        val current = _categories.value.toMutableList()
        if (current.any { it.equals(trimmed, ignoreCase = true) }) {
            return false
        }
        current.add(trimmed)
        saveCategories(current)
        return true
    }

    fun deleteCategory(name: String): Boolean {
        val trimmed = name.trim()
        val current = _categories.value.toMutableList()
        val removed = current.removeAll { it.equals(trimmed, ignoreCase = true) }
        if (removed) {
            saveCategories(current)
            return true
        }
        return false
    }

    fun renameCategory(oldName: String, newName: String): Boolean {
        val oldTrimmed = oldName.trim()
        val newTrimmed = newName.trim()
        if (newTrimmed.isEmpty() || newTrimmed.equals("All", ignoreCase = true)) {
            return false
        }
        val current = _categories.value.toMutableList()
        val index = current.indexOfFirst { it.equals(oldTrimmed, ignoreCase = true) }
        if (index == -1) return false
        
        // If changing to another existing category name (other than self)
        if (current.any { it.equals(newTrimmed, ignoreCase = true) && !it.equals(oldTrimmed, ignoreCase = true) }) {
            return false
        }

        current[index] = newTrimmed
        saveCategories(current)
        return true
    }

    companion object {
        private const val KEY_CATEGORIES = "key_user_categories"
        private const val DELIMITER = "||"

        @Volatile
        private var INSTANCE: CategoryManager? = null

        fun getInstance(context: Context): CategoryManager {
            return INSTANCE ?: synchronized(this) {
                val instance = CategoryManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
