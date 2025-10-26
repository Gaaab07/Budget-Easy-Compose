package com.budgeteasy.presentation.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgeteasy.domain.model.Budget
import com.budgeteasy.domain.model.Expense
import com.budgeteasy.domain.model.User
import com.budgeteasy.domain.usecase.budget.GetBudgetsUseCase
import com.budgeteasy.domain.usecase.expense.GetExpensesByBudgetUseCase
import com.budgeteasy.domain.usecase.user.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getExpensesByBudgetUseCase: GetExpensesByBudgetUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: Int = savedStateHandle.get<Int>("userId") ?: 0

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState

    private val _recentExpenses = MutableStateFlow<List<Expense>>(emptyList())
    val recentExpenses: StateFlow<List<Expense>> = _recentExpenses

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    // Presupuesto actualmente seleccionado (destacado)
    private val _selectedBudget = MutableStateFlow<Budget?>(null)
    val selectedBudget: StateFlow<Budget?> = _selectedBudget

    // Lista de todos los presupuestos
    private val _allBudgets = MutableStateFlow<List<Budget>>(emptyList())
    val allBudgets: StateFlow<List<Budget>> = _allBudgets

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        // Cargar usuario
        viewModelScope.launch {
            try {
                _uiState.value = DashboardUiState.Loading
                val userData = getUserUseCase(userId)
                _user.value = userData
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(e.message ?: "Error desconocido")
            }
        }

        // Cargar presupuestos
        viewModelScope.launch {
            try {
                getBudgetsUseCase(userId).collect { budgets ->
                    _allBudgets.value = budgets
                    _uiState.value = DashboardUiState.Success(budgets)

                    // ✅ ACTUALIZAR el presupuesto seleccionado con datos frescos
                    val currentSelectedId = _selectedBudget.value?.id

                    if (currentSelectedId != null) {
                        // Ya hay uno seleccionado: buscar su versión actualizada
                        val updatedBudget = budgets.find { it.id == currentSelectedId }
                        if (updatedBudget != null) {
                            _selectedBudget.value = updatedBudget
                            loadExpensesForBudget(updatedBudget.id)
                        } else {
                            // El presupuesto fue eliminado, seleccionar otro
                            selectDefaultBudget(budgets)
                        }
                    } else {
                        // Primera vez: seleccionar el de mayor monto
                        selectDefaultBudget(budgets)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    // Seleccionar presupuesto por defecto (el de mayor monto planeado)
    private fun selectDefaultBudget(budgets: List<Budget>) {
        if (budgets.isNotEmpty()) {
            val mainBudget = budgets.maxByOrNull { it.montoPlaneado }
            _selectedBudget.value = mainBudget
            mainBudget?.let { loadExpensesForBudget(it.id) }
        }
    }

    // Función para cargar gastos de un presupuesto específico
    private fun loadExpensesForBudget(budgetId: Int) {
        viewModelScope.launch {
            try {
                getExpensesByBudgetUseCase(budgetId, 5).collect { expenses ->
                    _recentExpenses.value = expenses
                }
            } catch (e: Exception) {
                // Log error pero no bloquea la UI
                _recentExpenses.value = emptyList()
            }
        }
    }

    // Función para cambiar el presupuesto seleccionado manualmente
    fun selectBudget(budget: Budget) {
        _selectedBudget.value = budget
        loadExpensesForBudget(budget.id)
    }

    // Refresca todo el dashboard
    fun refreshDashboard() {
        loadDashboard()
    }
}

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val budgets: List<Budget>) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}