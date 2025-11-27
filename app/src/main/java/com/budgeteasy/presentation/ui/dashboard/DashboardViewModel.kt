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


    private val _selectedBudget = MutableStateFlow<Budget?>(null)
    val selectedBudget: StateFlow<Budget?> = _selectedBudget


    private val _allBudgets = MutableStateFlow<List<Budget>>(emptyList())
    val allBudgets: StateFlow<List<Budget>> = _allBudgets

    init {
        loadDashboard()
    }

    private fun loadDashboard() {

        viewModelScope.launch {
            try {
                _uiState.value = DashboardUiState.Loading
                val userData = getUserUseCase(userId)
                _user.value = userData
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(e.message ?: "Error desconocido")
            }
        }


        viewModelScope.launch {
            try {
                getBudgetsUseCase(userId).collect { budgets ->
                    _allBudgets.value = budgets
                    _uiState.value = DashboardUiState.Success(budgets)


                    val currentSelectedId = _selectedBudget.value?.id

                    if (currentSelectedId != null) {

                        val updatedBudget = budgets.find { it.id == currentSelectedId }
                        if (updatedBudget != null) {
                            _selectedBudget.value = updatedBudget
                            loadExpensesForBudget(updatedBudget.id)
                        } else {

                            selectDefaultBudget(budgets)
                        }
                    } else {

                        selectDefaultBudget(budgets)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }


    private fun selectDefaultBudget(budgets: List<Budget>) {
        if (budgets.isNotEmpty()) {
            val mainBudget = budgets.maxByOrNull { it.montoPlaneado }
            _selectedBudget.value = mainBudget
            mainBudget?.let { loadExpensesForBudget(it.id) }
        }
    }


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


    fun selectBudget(budget: Budget) {
        _selectedBudget.value = budget
        loadExpensesForBudget(budget.id)
    }


    fun refreshDashboard() {
        loadDashboard()
    }
}

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val budgets: List<Budget>) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}