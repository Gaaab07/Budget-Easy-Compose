package com.budgeteasy.presentation.ui.budget.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgeteasy.domain.model.Budget
import com.budgeteasy.domain.usecase.budget.GetBudgetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BudgetListUiState(
    val budgets: List<Budget> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class BudgetListViewModel @Inject constructor(
    private val getBudgetsUseCase: GetBudgetsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetListUiState())
    val uiState: StateFlow<BudgetListUiState> = _uiState

    fun loadBudgets(userId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                getBudgetsUseCase(userId).collect { budgets ->
                    _uiState.value = _uiState.value.copy(
                        budgets = budgets,
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