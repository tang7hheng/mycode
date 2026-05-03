package com.example.myapplication.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.entity.Category
import com.example.myapplication.data.entity.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class BackupData(
    val tasks: List<Task>,
    val categories: List<Category>,
    val timestamp: Long
)

class BackupViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val taskDao = database.taskDao()
    private val categoryDao = database.categoryDao()
    private val context = application

    private val _backupStatus = MutableStateFlow<String?>(null)
    val backupStatus: StateFlow<String?> = _backupStatus.asStateFlow()

    fun backupData() {
        viewModelScope.launch {
            try {
                val tasks = taskDao.getAllTasksOnce()
                val categories = categoryDao.getAllCategoriesOnce()

                val backupData = BackupData(tasks, categories, System.currentTimeMillis())
                val json = backupDataToJson(backupData)

                val backupDir = File(context.getExternalFilesDir(null), "backups")
                if (!backupDir.exists()) {
                    backupDir.mkdirs()
                }

                val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val fileName = "todo_backup_${dateFormat.format(Date())}.json"
                val file = File(backupDir, fileName)
                file.writeText(json)

                _backupStatus.value = "备份成功: ${file.absolutePath}"
            } catch (e: Exception) {
                _backupStatus.value = "备份失败: ${e.message}"
            }
        }
    }

    fun restoreData(json: String) {
        viewModelScope.launch {
            try {
                val backupData = jsonToBackupData(json)

                // 恢复分类
                backupData.categories.forEach { category ->
                    categoryDao.insertCategory(category)
                }

                // 恢复任务
                backupData.tasks.forEach { task ->
                    taskDao.insertTask(task)
                }

                _backupStatus.value = "恢复成功"
            } catch (e: Exception) {
                _backupStatus.value = "恢复失败: ${e.message}"
            }
        }
    }

    fun getBackupFiles(): List<File> {
        val backupDir = File(context.getExternalFilesDir(null), "backups")
        return if (backupDir.exists()) {
            backupDir.listFiles()?.filter { it.name.endsWith(".json") }?.sortedByDescending { it.lastModified() } ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun readBackupFile(file: File): String? {
        return try {
            file.readText()
        } catch (e: Exception) {
            null
        }
    }

    fun clearStatus() {
        _backupStatus.value = null
    }

    private fun backupDataToJson(data: BackupData): String {
        val jsonObject = JSONObject()
        jsonObject.put("timestamp", data.timestamp)

        val tasksArray = JSONArray()
        data.tasks.forEach { task ->
            val taskObject = JSONObject()
            taskObject.put("id", task.id)
            taskObject.put("title", task.title)
            taskObject.put("description", task.description)
            taskObject.put("dueDate", task.dueDate ?: JSONObject.NULL)
            taskObject.put("priority", task.priority)
            taskObject.put("categoryId", task.categoryId ?: JSONObject.NULL)
            taskObject.put("tags", task.tags)
            taskObject.put("estimatedMinutes", task.estimatedMinutes)
            taskObject.put("isCompleted", task.isCompleted)
            taskObject.put("createdAt", task.createdAt)
            taskObject.put("completedAt", task.completedAt ?: JSONObject.NULL)
            tasksArray.put(taskObject)
        }
        jsonObject.put("tasks", tasksArray)

        val categoriesArray = JSONArray()
        data.categories.forEach { category ->
            val categoryObject = JSONObject()
            categoryObject.put("id", category.id)
            categoryObject.put("name", category.name)
            categoryObject.put("color", category.color)
            categoryObject.put("icon", category.icon)
            categoryObject.put("sortOrder", category.sortOrder)
            categoriesArray.put(categoryObject)
        }
        jsonObject.put("categories", categoriesArray)

        return jsonObject.toString(2)
    }

    private fun jsonToBackupData(json: String): BackupData {
        val jsonObject = JSONObject(json)
        val timestamp = jsonObject.getLong("timestamp")

        val tasksArray = jsonObject.getJSONArray("tasks")
        val tasks = mutableListOf<Task>()
        for (i in 0 until tasksArray.length()) {
            val taskObject = tasksArray.getJSONObject(i)
            tasks.add(
                Task(
                    id = taskObject.getLong("id"),
                    title = taskObject.getString("title"),
                    description = taskObject.getString("description"),
                    dueDate = if (taskObject.isNull("dueDate")) null else taskObject.getLong("dueDate"),
                    priority = taskObject.getInt("priority"),
                    categoryId = if (taskObject.isNull("categoryId")) null else taskObject.getLong("categoryId"),
                    tags = if (taskObject.has("tags")) taskObject.getString("tags") else "",
                    estimatedMinutes = if (taskObject.has("estimatedMinutes")) taskObject.getInt("estimatedMinutes") else 0,
                    isCompleted = taskObject.getBoolean("isCompleted"),
                    createdAt = taskObject.getLong("createdAt"),
                    completedAt = if (taskObject.isNull("completedAt")) null else taskObject.getLong("completedAt")
                )
            )
        }

        val categoriesArray = jsonObject.getJSONArray("categories")
        val categories = mutableListOf<Category>()
        for (i in 0 until categoriesArray.length()) {
            val categoryObject = categoriesArray.getJSONObject(i)
            categories.add(
                Category(
                    id = categoryObject.getLong("id"),
                    name = categoryObject.getString("name"),
                    color = categoryObject.getLong("color"),
                    icon = categoryObject.getString("icon"),
                    sortOrder = categoryObject.getInt("sortOrder")
                )
            )
        }

        return BackupData(tasks, categories, timestamp)
    }
}
