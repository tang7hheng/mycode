package com.example.myapplication.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.component.FilterChips
import com.example.myapplication.component.SwipeableTaskListItem
import com.example.myapplication.data.entity.Category
import com.example.myapplication.data.entity.Task
import com.example.myapplication.viewmodel.CategoryViewModel
import com.example.myapplication.viewmodel.TaskViewModel
import java.util.*

enum class SortType(val displayName: String) {
    CREATED_DESC("创建时间↓"),
    CREATED_ASC("创建时间↑"),
    DUE_DATE_ASC("截止时间↑"),
    DUE_DATE_DESC("截止时间↓"),
    PRIORITY_DESC("优先级↓"),
    PRIORITY_ASC("优先级↑")
}

@Composable
fun TodoScreen(
    taskViewModel: TaskViewModel,
    categoryViewModel: CategoryViewModel,
    onTaskClick: (Long) -> Unit,
    onAddTask: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("all") }
    var sortType by remember { mutableStateOf(SortType.CREATED_DESC) }
    var showSortMenu by remember { mutableStateOf(false) }
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
    }.let { tasks ->
        when (sortType) {
            SortType.CREATED_DESC -> tasks.sortedByDescending { it.createdAt }
            SortType.CREATED_ASC -> tasks.sortedBy { it.createdAt }
            SortType.DUE_DATE_ASC -> tasks.sortedBy { it.dueDate ?: Long.MAX_VALUE }
            SortType.DUE_DATE_DESC -> tasks.sortedByDescending { it.dueDate ?: 0L }
            SortType.PRIORITY_DESC -> tasks.sortedByDescending { it.priority }
            SortType.PRIORITY_ASC -> tasks.sortedBy { it.priority }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChips(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                modifier = Modifier.weight(1f)
            )

            Box {
                IconButton(onClick = { showSortMenu = true }) {
                    Icon(
                        imageVector = Icons.Filled.Sort,
                        contentDescription = "排序"
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    SortType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = {
                                sortType = type
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (sortType == type) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

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
                    SwipeableTaskListItem(
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
