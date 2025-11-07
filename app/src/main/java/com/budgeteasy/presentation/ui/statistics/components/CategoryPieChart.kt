// presentation/ui/statistics/components/CategoryPieChart.kt
package com.budgeteasy.presentation.ui.statistics.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.budgeteasy.presentation.ui.statistics.CategoryData
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

@Composable
fun CategoryPieChart(categoryData: List<CategoryData>) {
    if (categoryData.isEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No hay datos para mostrar")
            }
        }
        return
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Lista de categorías con colores
            categoryData.take(5).forEachIndexed { index, data ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Surface(
                            modifier = Modifier.size(16.dp),
                            color = getCategoryColor(index),
                            shape = MaterialTheme.shapes.small
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = data.categoria,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = "${String.format("%.1f", data.porcentaje)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

fun getCategoryColor(index: Int): Color {
    val colors = listOf(
        Color(0xFF6200EE),
        Color(0xFF03DAC6),
        Color(0xFFFF6F00),
        Color(0xFFF44336),
        Color(0xFF4CAF50)
    )
    return colors.getOrElse(index) { Color.Gray }
}