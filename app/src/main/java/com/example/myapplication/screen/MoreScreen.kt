package com.example.myapplication.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.viewmodel.BackupViewModel
import com.example.myapplication.viewmodel.ThemeMode
import com.example.myapplication.viewmodel.ThemeViewModel

@Composable
fun MoreScreen(
    themeViewModel: ThemeViewModel,
    backupViewModel: BackupViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToCategoryManage: () -> Unit = {}
) {
    var showAboutDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    val themeMode by themeViewModel.themeMode.collectAsState()
    val backupStatus by backupViewModel.backupStatus.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 设置分组
        Text(
            text = "设置",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column {
                SettingItem(
                    icon = Icons.Filled.Palette,
                    title = "主题",
                    subtitle = when (themeMode) {
                        ThemeMode.SYSTEM -> "跟随系统"
                        ThemeMode.LIGHT -> "浅色"
                        ThemeMode.DARK -> "深色"
                    },
                    onClick = { showThemeDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingItem(
                    icon = Icons.Filled.Notifications,
                    title = "通知",
                    subtitle = "已开启",
                    onClick = { }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingItem(
                    icon = Icons.Filled.Language,
                    title = "语言",
                    subtitle = "中文",
                    onClick = { }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 数据管理分组
        Text(
            text = "数据",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column {
                SettingItem(
                    icon = Icons.Filled.Folder,
                    title = "清单管理",
                    subtitle = "管理任务分类",
                    onClick = onNavigateToCategoryManage
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingItem(
                    icon = Icons.Filled.Backup,
                    title = "备份数据",
                    subtitle = backupStatus ?: "导出任务数据",
                    onClick = { backupViewModel.backupData() }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingItem(
                    icon = Icons.Filled.Restore,
                    title = "恢复数据",
                    subtitle = "从备份导入",
                    onClick = { showRestoreDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 关于分组
        Text(
            text = "关于",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column {
                SettingItem(
                    icon = Icons.Filled.Info,
                    title = "关于应用",
                    subtitle = "版本 1.0.0",
                    onClick = { showAboutDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingItem(
                    icon = Icons.Filled.Code,
                    title = "开源许可",
                    subtitle = "MIT License",
                    onClick = { }
                )
            }
        }
    }

    // 关于对话框
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("关于待办App") },
            text = {
                Column {
                    Text("每日待办 v1.0.0")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("一个简洁高效的待办事项管理应用，帮助您规划每一天。")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("功能特点：")
                    Text("• 任务管理与分类")
                    Text("• 日历视图")
                    Text("• 统计分析")
                    Text("• 优先级设置")
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("确定")
                }
            }
        )
    }

    // 主题选择对话框
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("选择主题") },
            text = {
                Column {
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    themeViewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = themeMode == mode,
                                onClick = {
                                    themeViewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (mode) {
                                    ThemeMode.SYSTEM -> "跟随系统"
                                    ThemeMode.LIGHT -> "浅色"
                                    ThemeMode.DARK -> "深色"
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 恢复数据对话框
    if (showRestoreDialog) {
        val backupFiles = remember { backupViewModel.getBackupFiles() }
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("恢复数据") },
            text = {
                Column {
                    if (backupFiles.isEmpty()) {
                        Text(
                            text = "没有找到备份文件",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    } else {
                        Text(
                            text = "选择要恢复的备份文件：",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        backupFiles.forEach { file ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        val json = backupViewModel.readBackupFile(file)
                                        if (json != null) {
                                            backupViewModel.restoreData(json)
                                        }
                                        showRestoreDialog = false
                                    }
                            ) {
                                Text(
                                    text = file.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}
