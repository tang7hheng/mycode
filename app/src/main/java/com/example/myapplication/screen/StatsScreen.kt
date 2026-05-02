package com.example.myapplication.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.component.CategoryPieChart
import com.example.myapplication.component.CompletionChart
import com.example.myapplication.viewmodel.CategoryViewModel
import com.example.myapplication.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatsScreen(
    taskViewModel: TaskViewModel,
    categoryViewModel: CategoryViewModel
) {
    val allTasks by taskViewModel.allTasks.collectAsState()
    val completedTasks by taskViewModel.completedTasks.collectAsState()
    val categories by categoryViewModel.allCategories.collectAsState()

    val totalTasks = allTasks.size
    val totalCompleted = completedTasks.size
    val completionRate = if (totalTasks > 0) (totalCompleted * 100 / totalTasks) else 0

    // 近7天完成数据
    val weeklyData = remember(completedTasks) {
        val dateFormat = SimpleDateFormat("M/d", Locale.CHINA)
        val calendar = Calendar.getInstance()
        val data = mutableListOf<Pair<String, Int>>()

        for (i in 6 downTo 0) {
            val dayCal = (calendar.clone() as Calendar).apply {
                add(Calendar.DAY_OF_MONTH, -i)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = dayCal.timeInMillis
            dayCal.add(Calendar.DAY_OF_MONTH, 1)
            val endOfDay = dayCal.timeInMillis

            val count = completedTasks.count { task ->
                task.completedAt != null && task.completedAt >= startOfDay && task.completedAt < endOfDay
            }
            data.add(Pair(dateFormat.format(Date(startOfDay)), count))
        }
        data
    }

    // 分类分布数据
    val categoryData = remember(allTasks, categories) {
        categories.mapNotNull { category ->
            val count = allTasks.count { it.categoryId == category.id }
            if (count > 0) Pair(category.name, count) else null
        }.sortedByDescending { it.second }
    }

    val pieColors = listOf(
        Color(0xFF2196F3),
        Color(0xFF4CAF50),
        Color(0xFFFF9800),
        Color(0xFFE91E63),
        Color(0xFF9C27B0),
        Color(0xFF00BCD4),
        Color(0xFFFF5722),
        Color(0xFF795548)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 总体统计卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "总体统计",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("总任务", totalTasks.toString())
                    StatItem("已完成", totalCompleted.toString())
                    StatItem("完成率", "$completionRate%")
                }
            }
        }

        // 近7天趋势图
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                CompletionChart(
                    data = weeklyData,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 分类分布饼图
        if (categoryData.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    CategoryPieChart(
                        data = categoryData,
                        colors = pieColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}
