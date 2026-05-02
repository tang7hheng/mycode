package com.example.myapplication.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.entity.Category

@Composable
fun CategoryPicker(
    categories: List<Category>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "分类",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            item {
                CategoryChip(
                    name = "无分类",
                    color = 0xFF607D8B,
                    isSelected = selectedCategoryId == null,
                    onClick = { onCategorySelected(null) }
                )
            }
            items(categories) { category ->
                CategoryChip(
                    name = category.name,
                    color = category.color,
                    isSelected = selectedCategoryId == category.id,
                    onClick = { onCategorySelected(category.id) }
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    name: String,
    color: Long,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.small,
        color = if (isSelected)
            Color(color).copy(alpha = 0.2f)
        else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected)
            ButtonDefaults.outlinedButtonBorder
        else null
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(color))
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
    }
}
