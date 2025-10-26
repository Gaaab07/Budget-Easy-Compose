package com.budgeteasy.presentation.ui.expense.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgeteasy.domain.model.Expense
import com.budgeteasy.domain.usecase.expense.GetRecentExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AllExpensesUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AllExpensesViewModel @Inject constructor(
    private val getRecentExpensesUseCase: GetRecentExpensesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AllExpensesUiState())
    val uiState: StateFlow<AllExpensesUiState> = _uiState.asStateFlow()

    private val _allExpenses = MutableStateFlow<List<Expense>>(emptyList())
    val allExpenses: StateFlow<List<Expense>> = _allExpenses.asStateFlow()

    fun loadAllExpenses(userId: Int) {
        viewModelScope.launch {
            _uiState.value = AllExpensesUiState(isLoading = true)
            try {
                // Cargar TODOS los gastos (sin límite)
                getRecentExpensesUseCase(userId, limit = 1000).collect { expenses ->
                    _allExpenses.value = expenses
                    _uiState.value = AllExpensesUiState(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = AllExpensesUiState(
                    isLoading = false,
                    errorMessage = "Error al cargar gastos: ${e.message}"
                )
            }
        }
    }
}