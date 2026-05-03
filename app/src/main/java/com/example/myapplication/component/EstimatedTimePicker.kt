package com.example.myapplication.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EstimatedTimePicker(
    estimatedMinutes: Int,
    onEstimatedMinutesChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }
    var customMinutes by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        Text(
            text = "预估耗时",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 快速选择按钮
            val quickOptions = listOf(15, 30, 60, 120)
            quickOptions.forEach { minutes ->
                FilterChip(
                    selected = estimatedMinutes == minutes,
                    onClick = {
                        onEstimatedMinutesChanged(if (estimatedMinutes == minutes) 0 else minutes)
                    },
                    label = {
                        Text(
                            text = if (minutes >= 60) "${minutes / 60}小时" else "${minutes}分钟"
                        )
                    }
                )
            }

            // 自定义按钮
            FilterChip(
                selected = estimatedMinutes > 0 && !quickOptions.contains(estimatedMinutes),
                onClick = { showPicker = true },
                label = { Text("自定义") }
            )
        }

        if (estimatedMinutes > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "预估 ${if (estimatedMinutes >= 60) "${estimatedMinutes / 60}小时${if (estimatedMinutes % 60 > 0) "${estimatedMinutes % 60}分钟" else ""}" else "${estimatedMinutes}分钟"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    // 自定义时间对话框
    if (showPicker) {
        AlertDialog(
            onDismissRequest = {
                showPicker = false
                customMinutes = ""
            },
            title = { Text("自定义预估时间") },
            text = {
                OutlinedTextField(
                    value = customMinutes,
                    onValueChange = { customMinutes = it.filter { c -> c.isDigit() } },
                    label = { Text("分钟数") },
                    placeholder = { Text("输入预估分钟数") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val minutes = customMinutes.toIntOrNull() ?: 0
                        if (minutes > 0) {
                            onEstimatedMinutesChanged(minutes)
                        }
                        showPicker = false
                        customMinutes = ""
                    },
                    enabled = customMinutes.isNotBlank() && (customMinutes.toIntOrNull() ?: 0) > 0
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPicker = false
                    customMinutes = ""
                }) {
                    Text("取消")
                }
            }
        )
    }
}
