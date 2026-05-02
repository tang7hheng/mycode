package com.example.myapplication.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PrioritySelector(
    selectedPriority: Int,
    onPrioritySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "优先级",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 0..4) {
                IconButton(
                    onClick = { onPrioritySelected(i) }
                ) {
                    Icon(
                        imageVector = if (i <= selectedPriority) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "优先级 ${i + 1}",
                        tint = if (i <= selectedPriority)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
