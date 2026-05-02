package com.example.myapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.data.dao.CategoryDao
import com.example.myapplication.data.dao.TaskDao
import com.example.myapplication.data.entity.Category
import com.example.myapplication.data.entity.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Task::class, Category::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDefaultCategories(database.categoryDao())
                }
            }
        }

        suspend fun populateDefaultCategories(categoryDao: CategoryDao) {
            val defaultCategories = listOf(
                Category(name = "工作", color = 0xFF2196F3, icon = "Work", sortOrder = 0),
                Category(name = "个人", color = 0xFF4CAF50, icon = "Person", sortOrder = 1),
                Category(name = "学习", color = 0xFFFF9800, icon = "School", sortOrder = 2),
                Category(name = "健康", color = 0xFFE91E63, icon = "Favorite", sortOrder = 3),
                Category(name = "购物", color = 0xFF9C27B0, icon = "ShoppingCart", sortOrder = 4),
                Category(name = "其他", color = 0xFF607D8B, icon = "MoreHoriz", sortOrder = 5)
            )
            defaultCategories.forEach { categoryDao.insertCategory(it) }
        }
    }
}
