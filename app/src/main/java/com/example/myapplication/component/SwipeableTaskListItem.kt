package com.example.myapplication.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.entity.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTaskListItem(
    task: Task,
    categoryColor: Long?,
    categoryName: String?,
    onTaskClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    true
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    onToggleComplete()
                    true
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = {
            val direction = dismissState.dismissDirection

            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primary
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                    else -> Color.Transparent
                },
                label = "swipe_color"
            )

            val icon = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.Filled.Check
                SwipeToDismissBoxValue.EndToStart -> Icons.Filled.Delete
                else -> Icons.Filled.Check
            }

            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }

            val alpha by animateFloatAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.5f else 1f,
                label = "swipe_alpha"
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.alpha(alpha),
                    tint = Color.White
                )
            }
        },
        content = {
            TaskListItem(
                task = task,
                categoryColor = categoryColor,
                categoryName = categoryName,
                onTaskClick = onTaskClick,
                onToggleComplete = onToggleComplete,
                onDelete = onDelete
            )
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true
    )
}
