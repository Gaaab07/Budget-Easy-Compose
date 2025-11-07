// presentation/ui/statistics/StatisticsScreen.kt
package com.budgeteasy.presentation.ui.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.budgeteasy.presentation.ui.navigation.BottomNavigationBar
import com.budgeteasy.presentation.ui.statistics.components.CategoryPieChart
import com.budgeteasy.presentation.ui.statistics.components.MonthlyBarChart
import com.budgeteasy.presentation.ui.statistics.components.StatisticsCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    userId: Int,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadStatistics(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Estadísticas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, userId = userId)
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.expenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📭",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay gastos para mostrar",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Agrega gastos para ver estadísticas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // 💰 Resumen general
                StatisticsCard(
                    title = "Total Gastado",
                    value = "S/.${String.format("%.2f", state.totalGastado)}",
                    emoji = "💰"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatisticsCard(
                        title = "Promedio",
                        value = "S/.${String.format("%.2f", state.promedioGasto)}",
                        emoji = "📊",
                        modifier = Modifier.weight(1f)
                    )
                    StatisticsCard(
                        title = "Gastos",
                        value = state.expenses.size.toString(),
                        emoji = "🧾",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🍰 Gráfico de torta por categoría
                Text(
                    text = "Gastos por Categoría",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                CategoryPieChart(categoryData = state.gastosPorCategoria)

                Spacer(modifier = Modifier.height(24.dp))

                // 📊 Gráfico de barras mensual
                Text(
                    text = "Gastos Mensuales",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                MonthlyBarChart(monthlyData = state.gastosMensuales)

                Spacer(modifier = Modifier.height(24.dp))

                // 📈 Top categorías
                Text(
                    text = "Top Categorías",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))

                state.topCategorias.forEachIndexed { index, item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = item.categoria,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "${item.cantidad} gastos",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = "S/.${String.format("%.2f", item.total)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}