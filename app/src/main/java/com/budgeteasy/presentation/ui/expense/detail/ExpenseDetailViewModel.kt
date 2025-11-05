package com.budgeteasy.presentation.ui.expense.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgeteasy.domain.model.Expense
import com.budgeteasy.domain.repository.IExpenseRepository // Usar tu interfaz
import com.budgeteasy.domain.usecase.expense.DeleteExpenseUseCase
import com.budgeteasy.domain.usecase.expense.UpdateExpenseUseCase // 👈 NUEVA IMPORTACIÓN
import com.budgeteasy.domain.usecase.expense.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseDetailViewModel @Inject constructor(
    private val expenseRepository: IExpenseRepository, // Usar tu interfaz
    private val getExpensesUseCase: GetExpensesUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase, // 👈 AÑADIDO
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val expenseId: Int = savedStateHandle.get<Int>("expenseId") ?: 0
    private val budgetId: Int = savedStateHandle.get<Int>("budgetId") ?: 0

    private val _expense = MutableStateFlow<Expense?>(null)
    val expense: StateFlow<Expense?> = _expense

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess

    init {
        loadExpense()
    }

    private fun loadExpense() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                getExpensesUseCase(budgetId).collect { expenses ->
                    _expense.value = expenses.find { it.id == expenseId }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar el gasto"
                _isLoading.value = false
            }
        }
    }

    fun deleteExpense() {
        // Obtenemos el ID de forma segura. Si _expense.value es null, salimos.
        val expenseIdToDelete = _expense.value?.id ?: return

        viewModelScope.launch {
            try {
                _isDeleting.value = true

                // 1. LLAMA al UseCase con el ID (como definimos en el Paso 1.1)
                val wasDeleted = deleteExpenseUseCase(expenseIdToDelete)

                if (wasDeleted) {
                    _deleteSuccess.value = true // Activa la navegación de vuelta
                    // También podrías emitir un mensaje a un SharedFlow aquí
                } else {
                    _error.value = "Error al eliminar y ajustar el balance."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al eliminar el gasto"
            } finally {
                _isDeleting.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}