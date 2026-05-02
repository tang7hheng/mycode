package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.entity.Task
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val taskDao = database.taskDao()

    val allTasks: StateFlow<List<Task>> = taskDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingTasks: StateFlow<List<Task>> = taskDao.getPendingTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedTasks: StateFlow<List<Task>> = taskDao.getCompletedTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val highPriorityTasks: StateFlow<List<Task>> = taskDao.getHighPriorityTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTodayTasks(): StateFlow<List<Task>> {
        val (startOfDay, endOfDay) = getTodayRange()
        return taskDao.getTasksByDate(startOfDay, endOfDay)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun getTodayTaskCount(): StateFlow<Int> {
        val (startOfDay, endOfDay) = getTodayRange()
        return taskDao.getTaskCountForDate(startOfDay, endOfDay)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    }

    fun getTodayCompletedCount(): StateFlow<Int> {
        val (startOfDay, endOfDay) = getTodayRange()
        return taskDao.getCompletedCountForDate(startOfDay, endOfDay)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    }

    fun getTasksByDate(date: Long): StateFlow<List<Task>> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis

        return taskDao.getTasksByDate(startOfDay, endOfDay)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun getTasksByCategory(categoryId: Long): StateFlow<List<Task>> {
        return taskDao.getTasksByCategory(categoryId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun getCompletedCountInRange(startTime: Long, endTime: Long): StateFlow<Int> {
        return taskDao.getCompletedCountInRange(startTime, endTime)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    }

    fun getTasksInRange(startTime: Long, endTime: Long): StateFlow<List<Task>> {
        return taskDao.getTasksInRange(startTime, endTime)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskDao.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskDao.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
        }
    }

    fun deleteTaskById(taskId: Long) {
        viewModelScope.launch {
            taskDao.deleteTaskById(taskId)
        }
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch {
            val updatedTask = if (task.isCompleted) {
                task.copy(isCompleted = false, completedAt = null)
            } else {
                task.copy(isCompleted = true, completedAt = System.currentTimeMillis())
            }
            taskDao.updateTask(updatedTask)
        }
    }

    private fun getTodayRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis
        return Pair(startOfDay, endOfDay)
    }
}
