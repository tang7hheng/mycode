package com.example.myapplication.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: Long? = null,
    val priority: Int = 0,
    val categoryId: Long? = null,
    val tags: String = "", // 逗号分隔的标签
    val estimatedMinutes: Int = 0, // 预估耗时（分钟）
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
