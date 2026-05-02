package com.example.myapplication.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Today : Screen("today", "今日", Icons.Filled.Today)
    object Todo : Screen("todo", "待办", Icons.Filled.List)
    object Calendar : Screen("calendar", "日历", Icons.Filled.CalendarMonth)
    object Stats : Screen("stats", "统计", Icons.Filled.BarChart)
    object More : Screen("more", "更多", Icons.Filled.MoreHoriz)
    object TaskEdit : Screen("task_edit/{taskId}", "编辑任务", Icons.Filled.Edit) {
        fun createRoute(taskId: Long = -1L) = "task_edit/$taskId"
    }
}
