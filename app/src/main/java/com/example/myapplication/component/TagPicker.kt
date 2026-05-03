package com.example.myapplication.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TagPicker(
    selectedTags: List<String>,
    onTagsChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newTag by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "标签",
                style = MaterialTheme.typography.titleSmall
            )
            IconButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "添加标签"
                )
            }
        }

        if (selectedTags.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedTags) { tag ->
                    InputChip(
                        selected = false,
                        onClick = { },
                        label = { Text(tag) },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    onTagsChanged(selectedTags.filter { it != tag })
                                },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "删除标签",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    )
                }
            }
        } else {
            Text(
                text = "点击 + 添加标签",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }

    // 添加标签对话框
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                newTag = ""
            },
            title = { Text("添加标签") },
            text = {
                OutlinedTextField(
                    value = newTag,
                    onValueChange = { newTag = it },
                    label = { Text("标签名称") },
                    placeholder = { Text("输入标签名称") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTag.isNotBlank() && !selectedTags.contains(newTag.trim())) {
                            onTagsChanged(selectedTags + newTag.trim())
                        }
                        showAddDialog = false
                        newTag = ""
                    },
                    enabled = newTag.isNotBlank()
                ) {
                    Text("添加")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    newTag = ""
                }) {
                    Text("取消")
                }
            }
        )
    }
}
