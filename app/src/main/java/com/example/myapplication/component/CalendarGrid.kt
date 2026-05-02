package com.example.myapplication.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarGrid(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    taskDateMap: Map<Int, Int>,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember {
        mutableStateOf(Calendar.getInstance().apply {
            timeInMillis = selectedDate
        })
    }

    val dateFormat = SimpleDateFormat("yyyy年M月", Locale.CHINA)

    Column(modifier = modifier) {
        // 月份导航
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                currentMonth = (currentMonth.clone() as Calendar).apply {
                    add(Calendar.MONTH, -1)
                }
            }) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "上月")
            }

            Text(
                text = dateFormat.format(currentMonth.time),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = {
                currentMonth = (currentMonth.clone() as Calendar).apply {
                    add(Calendar.MONTH, 1)
                }
            }) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "下月")
            }
        }

        // 星期头部
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            val weekDays = listOf("日", "一", "二", "三", "四", "五", "六")
            weekDays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 日历网格
        val calendar = (currentMonth.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val today = Calendar.getInstance()

        val selectedCalendar = Calendar.getInstance().apply {
            timeInMillis = selectedDate
        }

        var dayCounter = 1
        for (week in 0..5) {
            if (dayCounter > daysInMonth) break

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                for (dayOfWeek in 0..6) {
                    if (week == 0 && dayOfWeek < firstDayOfWeek || dayCounter > daysInMonth) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val day = dayCounter
                        val isToday = today.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                                today.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH) &&
                                today.get(Calendar.DAY_OF_MONTH) == day
                        val isSelected = selectedCalendar.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                                selectedCalendar.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH) &&
                                selectedCalendar.get(Calendar.DAY_OF_MONTH) == day
                        val taskCount = taskDateMap[day] ?: 0

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        isToday -> MaterialTheme.colorScheme.primaryContainer
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable {
                                    val newDate = (currentMonth.clone() as Calendar).apply {
                                        set(Calendar.DAY_OF_MONTH, day)
                                        set(Calendar.HOUR_OF_DAY, 0)
                                        set(Calendar.MINUTE, 0)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    onDateSelected(newDate.timeInMillis)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = day.toString(),
                                    fontSize = 14.sp,
                                    color = when {
                                        isSelected -> MaterialTheme.colorScheme.onPrimary
                                        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                if (taskCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .background(
                                                color = if (isSelected)
                                                    MaterialTheme.colorScheme.onPrimary
                                                else MaterialTheme.colorScheme.primary,
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }
                        }
                        dayCounter++
                    }
                }
            }
        }
    }
}
