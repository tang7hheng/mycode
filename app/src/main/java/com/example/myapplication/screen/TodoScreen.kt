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
import com.example.myapplication.component.FilterChips
import com.example.myapplication.component.TaskListItem
import com.example.myapplication.data.entity.Category
import com.example.myapplication.data.entity.Task
import com.example.myapplication.viewmodel.CategoryViewModel
import com.example.myapplication.viewmodel.TaskViewModel
import java.util.*

@Composable
fun TodoScreen(
    taskViewModel: TaskViewModel,
    categoryViewModel: CategoryViewModel,
    onTaskClick: (Long) -> Unit,
    onAddTask: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("all") }
    val allTasks by taskViewModel.allTasks.collectAsState()
    val pendingTasks by taskViewModel.pendingTasks.collectAsState()
    val completedTasks by taskViewModel.completedTasks.collectAsState()
    val highPriorityTasks by taskViewModel.highPriorityTasks.collectAsState()
    val todayTasks by taskViewModel.getTodayTasks().collectAsState()
    val categories by categoryViewModel.allCategories.collectAsState()

    val categoryMap = categories.associateBy { it.id }

    val filteredTasks = when (selectedFilter) {
        "today" -> todayTasks
        "high" -> highPriorityTasks
        "completed" -> completedTasks
        else -> allTasks
    }

    Column(modifier = Modifier.fillMaxSize()) {
        FilterChips(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it }
        )

        if (filteredTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (selectedFilter) {
                        "today" -> "今天没有任务"
                        "high" -> "没有高优先级任务"
                        "completed" -> "还没有完成的任务"
                        else -> "还没有任务\n点击右下角的 + 按钮添加"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 0.dp)
            ) {
                item {
                    Text(
                        text = "共 ${filteredTasks.size} 个任务",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                items(filteredTasks, key = { it.id }) { task ->
                    val category = task.categoryId?.let { categoryMap[it] }
                    TaskListItem(
                        task = task,
                        categoryColor = category?.color,
                        categoryName = category?.name,
                        onTaskClick = { onTaskClick(task.id) },
                        onToggleComplete = { taskViewModel.toggleComplete(task) },
                        onDelete = { taskViewModel.deleteTask(task) }
                    )
                }
            }
        }
    }
}
