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
import com.example.myapplication.component.CalendarGrid
import com.example.myapplication.component.TaskTimelineItem
import com.example.myapplication.viewmodel.CategoryViewModel
import com.example.myapplication.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(
    taskViewModel: TaskViewModel,
    categoryViewModel: CategoryViewModel,
    onTaskClick: (Long) -> Unit,
    onAddTask: () -> Unit
) {
    var selectedDate by remember {
        mutableLongStateOf(
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        )
    }

    val selectedDateTasks by taskViewModel.getTasksByDate(selectedDate).collectAsState()
    val allTasks by taskViewModel.allTasks.collectAsState()
    val categories by categoryViewModel.allCategories.collectAsState()
    val categoryMap = categories.associateBy { it.id }

    // 计算当月每天的任务数量
    val taskDateMap = remember(allTasks) {
        val map = mutableMapOf<Int, Int>()
        val calendar = Calendar.getInstance()
        allTasks.forEach { task ->
            task.dueDate?.let { date ->
                calendar.timeInMillis = date
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                map[day] = (map[day] ?: 0) + 1
            }
        }
        map
    }

    val dateFormat = SimpleDateFormat("M月d日", Locale.CHINA)

    Column(modifier = Modifier.fillMaxSize()) {
        // 日历网格
        CalendarGrid(
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it },
            taskDateMap = taskDateMap
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // 选中日期的任务列表
        Text(
            text = "${dateFormat.format(Date(selectedDate))} 的任务",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (selectedDateTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "这一天没有任务",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(selectedDateTasks, key = { it.id }) { task ->
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
