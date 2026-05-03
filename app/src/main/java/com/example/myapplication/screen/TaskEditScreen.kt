package com.example.myapplication.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.component.CategoryPicker
import com.example.myapplication.component.DateTimePicker
import com.example.myapplication.component.EstimatedTimePicker
import com.example.myapplication.component.PrioritySelector
import com.example.myapplication.component.TagPicker
import com.example.myapplication.data.entity.Task
import com.example.myapplication.viewmodel.CategoryViewModel
import com.example.myapplication.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    taskId: Long,
    taskViewModel: TaskViewModel,
    categoryViewModel: CategoryViewModel,
    onNavigateBack: () -> Unit
) {
    val isNewTask = taskId == -1L
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var priority by remember { mutableIntStateOf(0) }
    var categoryId by remember { mutableStateOf<Long?>(null) }
    var tags by remember { mutableStateOf<List<String>>(emptyList()) }
    var estimatedMinutes by remember { mutableIntStateOf(0) }
    var hasReminder by remember { mutableStateOf(true) }
    var isLoaded by remember { mutableStateOf(isNewTask) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val categories by categoryViewModel.allCategories.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 加载现有任务
    LaunchedEffect(taskId) {
        if (!isNewTask) {
            taskViewModel.getTaskById(taskId)?.let { task ->
                title = task.title
                description = task.description
                dueDate = task.dueDate
                priority = task.priority
                categoryId = task.categoryId
                tags = if (task.tags.isNotBlank()) task.tags.split(",") else emptyList()
                estimatedMinutes = task.estimatedMinutes
            }
            isLoaded = true
        }
    }

    if (!isLoaded) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isNewTask) "新建任务" else "编辑任务") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (!isNewTask) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "删除",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 标题输入
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("任务标题") },
                placeholder = { Text("输入任务标题") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 描述输入
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("详细描述") },
                placeholder = { Text("输入任务描述（可选）") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                maxLines = 5
            )

            // 日期时间选择
            DateTimePicker(
                selectedDate = dueDate,
                onDateSelected = { dueDate = it }
            )

            // 优先级选择
            PrioritySelector(
                selectedPriority = priority,
                onPrioritySelected = { priority = it }
            )

            // 分类选择
            CategoryPicker(
                categories = categories,
                selectedCategoryId = categoryId,
                onCategorySelected = { categoryId = it }
            )

            // 标签选择
            TagPicker(
                selectedTags = tags,
                onTagsChanged = { tags = it }
            )

            // 预估耗时
            EstimatedTimePicker(
                estimatedMinutes = estimatedMinutes,
                onEstimatedMinutesChanged = { estimatedMinutes = it }
            )

            // 提醒设置
            if (dueDate != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "任务提醒",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "截止时间前15分钟提醒",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                    Switch(
                        checked = hasReminder,
                        onCheckedChange = { hasReminder = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 保存按钮
            Button(
                onClick = {
                    if (title.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("请输入任务标题")
                        }
                        return@Button
                    }

                    if (isNewTask) {
                        taskViewModel.addTask(
                            Task(
                                title = title.trim(),
                                description = description.trim(),
                                dueDate = dueDate,
                                priority = priority,
                                categoryId = categoryId,
                                tags = tags.joinToString(","),
                                estimatedMinutes = estimatedMinutes
                            )
                        )
                    } else {
                        scope.launch {
                            taskViewModel.getTaskById(taskId)?.let { existingTask ->
                                taskViewModel.updateTask(
                                    existingTask.copy(
                                        title = title.trim(),
                                        description = description.trim(),
                                        dueDate = dueDate,
                                        priority = priority,
                                        categoryId = categoryId,
                                        tags = tags.joinToString(","),
                                        estimatedMinutes = estimatedMinutes
                                    )
                                )
                            }
                        }
                    }
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isNewTask) "创建任务" else "保存修改")
            }
        }
    }

    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除任务") },
            text = { Text("确定要删除这个任务吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskViewModel.deleteTaskById(taskId)
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
