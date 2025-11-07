// presentation/ui/statistics/StatisticsViewModel.kt
package com.budgeteasy.presentation.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgeteasy.domain.model.Expense
import com.budgeteasy.domain.usecase.expense.GetRecentExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class CategoryData(
    val categoria: String,
    val total: Double,
    val cantidad: Int,
    val porcentaje: Float
)

data class MonthlyData(
    val mes: String,
    val total: Double
)

data class StatisticsUiState(
    val isLoading: Boolean = false,
    val expenses: List<Expense> = emptyList(),
    val totalGastado: Double = 0.0,
    val promedioGasto: Double = 0.0,
    val gastosPorCategoria: List<CategoryData> = emptyList(),
    val gastosMensuales: List<MonthlyData> = emptyList(),
    val topCategorias: List<CategoryData> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getRecentExpensesUseCase: GetRecentExpensesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    fun loadStatistics(userId: Int) {
        viewModelScope.launch {
            _uiState.value = StatisticsUiState(isLoading = true)

            try {
                getRecentExpensesUseCase(userId, limit = 1000).collect { expenses ->
                    if (expenses.isEmpty()) {
                        _uiState.value = StatisticsUiState(
                            isLoading = false,
                            expenses = emptyList()
                        )
                        return@collect
                    }

                    // Calcular total gastado
                    val totalGastado = expenses.sumOf { it.monto }
                    val promedioGasto = totalGastado / expenses.size

                    // Agrupar por categoría
                    val gastosPorCategoria = expenses
                        .groupBy { it.categoria }
                        .map { (categoria, gastos) ->
                            val total = gastos.sumOf { it.monto }
                            CategoryData(
                                categoria = categoria,
                                total = total,
                                cantidad = gastos.size,
                                porcentaje = (total / totalGastado * 100).toFloat()
                            )
                        }
                        .sortedByDescending { it.total }

                    // Agrupar por mes
                    val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                    val monthFormat = SimpleDateFormat("MMM yyyy", Locale("es", "ES"))

                    val gastosMensuales = expenses
                        .groupBy {
                            dateFormat.format(Date(it.fecha))
                        }
                        .map { (mes, gastos) ->
                            val date = dateFormat.parse(mes) ?: Date()
                            MonthlyData(
                                mes = monthFormat.format(date),
                                total = gastos.sumOf { it.monto }
                            )
                        }
                        .sortedBy { it.mes }

                    // Top 5 categorías
                    val topCategorias = gastosPorCategoria.take(5)

                    _uiState.value = StatisticsUiState(
                        isLoading = false,
                        expenses = expenses,
                        totalGastado = totalGastado,
                        promedioGasto = promedioGasto,
                        gastosPorCategoria = gastosPorCategoria,
                        gastosMensuales = gastosMensuales,
                        topCategorias = topCategorias
                    )
                }
            } catch (e: Exception) {
                _uiState.value = StatisticsUiState(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }
}