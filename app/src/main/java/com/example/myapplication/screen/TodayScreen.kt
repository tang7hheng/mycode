package com.example.myapplication.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.component.DateHeader
import com.example.myapplication.component.TaskTimelineItem
import com.example.myapplication.data.entity.Category
import com.example.myapplication.viewmodel.CategoryViewModel
import com.example.myapplication.viewmodel.TaskViewModel

@Composable
fun TodayScreen(
    taskViewModel: TaskViewModel,
    categoryViewModel: CategoryViewModel,
    onTaskClick: (Long) -> Unit,
    onAddTask: () -> Unit
) {
    val todayTasks by taskViewModel.getTodayTasks().collectAsState()
    val todayTotal by taskViewModel.getTodayTaskCount().collectAsState()
    val todayCompleted by taskViewModel.getTodayCompletedCount().collectAsState()
    val categories by categoryViewModel.allCategories.collectAsState()

    val categoryMap = categories.associateBy { it.id }

    Column(modifier = Modifier.fillMaxSize()) {
        DateHeader(
            totalTasks = todayTotal,
            completedTasks = todayCompleted
        )

        if (todayTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "今天还没有任务",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "点击右下角的 + 按钮添加新任务",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(todayTasks, key = { it.id }) { task ->
                    val category = task.categoryId?.let { categoryMap[it] }
                    TaskTimelineItem(
                        task = task,
                        categoryColor = category?.color,
                        onTaskClick = { onTaskClick(task.id) },
                        onToggleComplete = { taskViewModel.toggleComplete(task) }
                    )
                }
            }
        }
    }
}
