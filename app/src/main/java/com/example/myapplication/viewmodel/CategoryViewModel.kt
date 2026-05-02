package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.entity.Category
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val categoryDao = database.categoryDao()

    val allCategories: StateFlow<List<Category>> = categoryDao.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTaskCountForCategory(categoryId: Long): StateFlow<Int> {
        return categoryDao.getTaskCountForCategory(categoryId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    }

    suspend fun getCategoryById(categoryId: Long): Category? {
        return categoryDao.getCategoryById(categoryId)
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            categoryDao.insertCategory(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            categoryDao.updateCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryDao.deleteCategory(category)
        }
    }
}
