package com.budgeteasy.presentation.ui.expense.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgeteasy.domain.model.Expense
import com.budgeteasy.domain.usecase.expense.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExpenseListUiState(
    val expenses: List<Expense> = emptyList(),
    val totalGastado: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseListUiState())
    val uiState: StateFlow<ExpenseListUiState> = _uiState

    fun loadExpenses(budgetId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                getExpensesUseCase(budgetId).collect { expenses ->
                    val totalGastado = expenses.sumOf { it.monto }
                    _uiState.value = _uiState.value.copy(
                        expenses = expenses,
                        totalGastado = totalGastado,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}