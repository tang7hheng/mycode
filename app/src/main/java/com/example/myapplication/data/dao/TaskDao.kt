package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END, dueDate ASC, priority DESC")
    fun getPendingTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate >= :startOfDay AND dueDate < :endOfDay ORDER BY dueDate ASC")
    fun getTasksByDate(startOfDay: Long, endOfDay: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    fun getTasksByCategory(categoryId: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE priority >= 3 AND isCompleted = 0 ORDER BY priority DESC")
    fun getHighPriorityTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate IS NULL AND isCompleted = 0 ORDER BY createdAt DESC")
    fun getNoDateTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): Task?

    @Query("SELECT COUNT(*) FROM tasks WHERE dueDate >= :startOfDay AND dueDate < :endOfDay")
    fun getTaskCountForDate(startOfDay: Long, endOfDay: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE dueDate >= :startOfDay AND dueDate < :endOfDay AND isCompleted = 1")
    fun getCompletedCountForDate(startOfDay: Long, endOfDay: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1 AND completedAt >= :startTime AND completedAt < :endTime")
    fun getCompletedCountInRange(startTime: Long, endTime: Long): Flow<Int>

    @Query("SELECT * FROM tasks WHERE dueDate >= :startTime AND dueDate < :endTime ORDER BY dueDate ASC")
    fun getTasksInRange(startTime: Long, endTime: Long): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchTasks(query: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    suspend fun getAllTasksOnce(): List<Task>
}
