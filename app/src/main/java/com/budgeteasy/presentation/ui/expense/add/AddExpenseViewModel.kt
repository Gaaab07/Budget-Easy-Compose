package com.budgeteasy.presentation.ui.expense.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgeteasy.domain.model.Expense
import com.budgeteasy.domain.usecase.expense.AddExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddExpenseUiState(
    val nombre: String = "",
    val monto: String = "",
    val nota: String = "",
    val categoria: String = "Otros",
    val fecha: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAddSuccessful: Boolean = false
)

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val addExpenseUseCase: AddExpenseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState

    fun onNombreChanged(newNombre: String) {
        _uiState.value = _uiState.value.copy(nombre = newNombre)
    }

    fun onMontoChanged(newMonto: String) {
        _uiState.value = _uiState.value.copy(monto = newMonto)
    }

    fun onNotaChanged(newNota: String) {
        _uiState.value = _uiState.value.copy(nota = newNota)
    }

    fun onFechaChanged(newFecha: Long) {
        _uiState.value = _uiState.value.copy(fecha = newFecha)
    }

    // 👇 NUEVO: Método para cambiar categoría
    fun onCategoriaChanged(newCategoria: String) {
        _uiState.value = _uiState.value.copy(categoria = newCategoria)
    }

    fun addExpense(budgetId: Int) {
        val currentState = _uiState.value

        if (currentState.nombre.isEmpty()) {
            _uiState.value = currentState.copy(errorMessage = "El nombre no puede estar vacío")
            return
        }

        if (currentState.monto.isEmpty()) {
            _uiState.value = currentState.copy(errorMessage = "El monto no puede estar vacío")
            return
        }

        val monto = currentState.monto.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            _uiState.value = currentState.copy(errorMessage = "El monto debe ser mayor a 0")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            try {
                val newExpense = Expense(
                    budgetId = budgetId,
                    nombre = currentState.nombre,
                    monto = monto,
                    fecha = currentState.fecha,
                    nota = currentState.nota,
                    categoria = currentState.categoria // 👈 NUEVO: incluir categoría
                )

                addExpenseUseCase(newExpense)

                _uiState.value = currentState.copy(
                    isLoading = false,
                    isAddSuccessful = true,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
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