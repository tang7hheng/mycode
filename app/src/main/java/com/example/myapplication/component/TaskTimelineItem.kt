package com.example.myapplication.component

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.entity.Task
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskTimelineItem(
    task: Task,
    categoryColor: Long?,
    onTaskClick: () -> Unit,
    onToggleComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOverdue = task.dueDate != null && task.dueDate < System.currentTimeMillis() && !task.isCompleted
    val alpha = if (task.isCompleted) 0.5f else 1.0f
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    // 完成动画
    val checkboxScale by animateFloatAsState(
        targetValue = if (task.isCompleted) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkbox_scale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTaskClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 时间显示
        Column(
            modifier = Modifier.width(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (task.dueDate != null) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (isOverdue) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = timeFormat.format(Date(task.dueDate)),
                    fontSize = 12.sp,
                    color = if (isOverdue) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        // 时间线指示器
        Column(
            modifier = Modifier.width(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = when {
                            task.isCompleted -> MaterialTheme.colorScheme.primary
                            isOverdue -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.outline
                        },
                        shape = CircleShape
                    )
            )
            if (!task.isCompleted) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 任务卡片
        Card(
            modifier = Modifier
                .weight(1f)
                .alpha(alpha),
            colors = CardDefaults.cardColors(
                containerColor = if (task.isCompleted)
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 完成复选框（带动画）
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggleComplete() },
                    modifier = Modifier.scale(checkboxScale)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (task.isCompleted)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.onSurface
                    )
                    if (task.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.description,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                // 优先级指示器
                if (task.priority >= 3) {
                    Spacer(modifier = Modifier.width(8.dp))
                    PriorityIndicator(priority = task.priority)
                }

                // 分类颜色指示
                if (categoryColor != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = androidx.compose.ui.graphics.Color(categoryColor),
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun PriorityIndicator(priority: Int) {
    val color = when (priority) {
        4 -> MaterialTheme.colorScheme.error
        3 -> MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
        else -> MaterialTheme.colorScheme.outline
    }

    Row {
        repeat(priority + 1) {
            Text(
                text = "★",
                fontSize = 10.sp,
                color = color
            )
        }
    }
}
