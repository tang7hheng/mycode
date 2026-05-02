package com.example.myapplication.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CompletionChart(
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val maxValue = data.maxOf { it.second }.coerceAtLeast(1)
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Column(modifier = modifier) {
        Text(
            text = "近7天完成趋势",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(start = 32.dp, end = 16.dp, top = 8.dp, bottom = 24.dp)
        ) {
            val width = size.width
            val height = size.height
            val stepX = width / (data.size - 1).coerceAtLeast(1)

            // 绘制网格线
            for (i in 0..4) {
                val y = height * i / 4
                drawLine(
                    color = Color.Gray.copy(alpha = 0.2f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
            }

            // 绘制折线
            if (data.size > 1) {
                val path = Path()
                data.forEachIndexed { index, (_, value) ->
                    val x = index * stepX
                    val y = height - (value.toFloat() / maxValue * height)
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                drawPath(
                    path = path,
                    color = primaryColor,
                    style = Stroke(width = 3f)
                )
            }

            // 绘制数据点
            data.forEachIndexed { index, (_, value) ->
                val x = index * stepX
                val y = height - (value.toFloat() / maxValue * height)
                drawCircle(
                    color = primaryColor,
                    radius = 6f,
                    center = Offset(x, y)
                )
                drawCircle(
                    color = Color.White,
                    radius = 3f,
                    center = Offset(x, y)
                )
            }

            // 绘制X轴标签
            data.forEachIndexed { index, (label, _) ->
                val x = index * stepX
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        label,
                        x,
                        height + 40f,
                        android.graphics.Paint().apply {
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.CENTER
                            color = onSurfaceColor.hashCode()
                        }
                    )
                }
            }

            // 绘制Y轴标签
            for (i in 0..4) {
                val value = maxValue * i / 4
                val y = height - (value.toFloat() / maxValue * height)
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        value.toString(),
                        -16f,
                        y + 10f,
                        android.graphics.Paint().apply {
                            textSize = 24f
                            textAlign = android.graphics.Paint.Align.RIGHT
                            color = Color.Gray.hashCode()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryPieChart(
    data: List<Pair<String, Int>>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val total = data.sumOf { it.second }.toFloat()

    Column(modifier = modifier) {
        Text(
            text = "分类分布",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 饼图
            Canvas(
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp)
            ) {
                var startAngle = -90f
                data.forEachIndexed { index, (_, value) ->
                    val sweepAngle = (value / total) * 360f
                    drawArc(
                        color = colors[index % colors.size],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        size = Size(size.width, size.height)
                    )
                    startAngle += sweepAngle
                }
            }

            // 图例
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                data.forEachIndexed { index, (label, value) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Canvas(modifier = Modifier.size(12.dp)) {
                            drawCircle(color = colors[index % colors.size])
                        }
                        Text(
                            text = "$label ($value)",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
