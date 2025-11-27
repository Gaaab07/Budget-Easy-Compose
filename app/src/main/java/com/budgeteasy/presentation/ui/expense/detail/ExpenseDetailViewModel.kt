package com.budgeteasy.presentation.ui.expense.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgeteasy.domain.model.Expense
import com.budgeteasy.domain.repository.IExpenseRepository // Usar tu interfaz
import com.budgeteasy.domain.usecase.expense.DeleteExpenseUseCase
import com.budgeteasy.domain.usecase.expense.UpdateExpenseUseCase
import com.budgeteasy.domain.usecase.expense.GetExpensesUseCase // Lo estás usando para cargar el gasto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class ExpenseDetailEvent {
    data class ShowMessage(val message: String) : ExpenseDetailEvent()
}

@HiltViewModel
class ExpenseDetailViewModel @Inject constructor(
    private val expenseRepository: IExpenseRepository,
    private val getExpensesUseCase: GetExpensesUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val expenseId: Int = savedStateHandle.get<Int>("expenseId") ?: 0
    private val budgetId: Int = savedStateHandle.get<Int>("budgetId") ?: 0


    private val _expense = MutableStateFlow<Expense?>(null)
    val expense: StateFlow<Expense?> = _expense.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess.asStateFlow()


    private val _expenseName = MutableStateFlow("")
    val expenseName: StateFlow<String> = _expenseName.asStateFlow()

    private val _expenseMonto = MutableStateFlow("")
    val expenseMonto: StateFlow<String> = _expenseMonto.asStateFlow()

    private val _expenseCategory = MutableStateFlow("")
    val expenseCategory: StateFlow<String> = _expenseCategory.asStateFlow()

    private val _expenseNota = MutableStateFlow("")
    val expenseNota: StateFlow<String> = _expenseNota.asStateFlow()

    private val _isModified = MutableStateFlow(false)
    val isModified: StateFlow<Boolean> = _isModified.asStateFlow()

    private val _isUpdateLoading = MutableStateFlow(false)
    val isUpdateLoading: StateFlow<Boolean> = _isUpdateLoading.asStateFlow()


    private val _showUnsavedChangesDialog = MutableStateFlow(false)
    val showUnsavedChangesDialog: StateFlow<Boolean> = _showUnsavedChangesDialog.asStateFlow()


    private val _eventFlow = MutableSharedFlow<ExpenseDetailEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    private var originalMonto: Double = 0.0

    init {
        loadExpense()
        observeEdits()
    }

    private fun loadExpense() {
        if (expenseId == 0) {
            _error.value = "ID de gasto no válido"
            _isLoading.value = false
            return
        }
        viewModelScope.launch {
            try {
                _isLoading.value = true


                getExpensesUseCase(budgetId).collect { expenses ->
                    val expenseResult = expenses.find { it.id == expenseId }

                    if (expenseResult != null) {
                        _expense.value = expenseResult

                        _expenseName.value = expenseResult.nombre
                        _expenseMonto.value = String.format("%.2f", expenseResult.monto)
                        _expenseCategory.value = expenseResult.categoria
                        _expenseNota.value = expenseResult.nota
                        originalMonto = expenseResult.monto // Guarda el monto original
                        _error.value = null
                    } else {
                        _error.value = "Gasto no encontrado."
                    }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar el gasto: ${e.message}"
                _isLoading.value = false
            }
        }
    }



    fun onNameChange(name: String) {
        _expenseName.value = name
    }

    fun onMontoChange(monto: String) {

        _expenseMonto.value = monto.replace(Regex("[^0-9.]"), "")
    }

    fun onCategoryChange(category: String) {
        _expenseCategory.value = category
    }

    fun onNotaChange(nota: String) {
        _expenseNota.value = nota
    }

    fun clearError() {
        _error.value = null
    }



    private fun observeEdits() {
        combine(
            _expenseName, _expenseMonto, _expenseCategory, _expenseNota, _expense
        ) { name, montoStr, category, nota, originalExpense ->

            if (originalExpense == null) return@combine false


            val originalMontoStr = String.format("%.2f", originalExpense.monto)

            // Compara cada campo con el valor original
            val isModified = name != originalExpense.nombre ||
                    category != originalExpense.categoria ||
                    nota != originalExpense.nota ||
                    montoStr != originalMontoStr

            return@combine isModified
        }.distinctUntilChanged().onEach { isModified ->
            _isModified.value = isModified
        }.launchIn(viewModelScope)
    }



    fun updateExpense() {
        if (!_isModified.value) {
            viewModelScope.launch {
                _eventFlow.emit(ExpenseDetailEvent.ShowMessage("No hay cambios para guardar."))
            }
            return
        }

        val original = _expense.value ?: return
        val montoNuevo = try {
            _expenseMonto.value.toDouble()
        } catch (e: NumberFormatException) {
            viewModelScope.launch {
                _eventFlow.emit(ExpenseDetailEvent.ShowMessage("El monto no es un número válido."))
            }
            return
        }

        if (montoNuevo <= 0.0) {
            viewModelScope.launch {
                _eventFlow.emit(ExpenseDetailEvent.ShowMessage("El monto debe ser mayor a cero."))
            }
            return
        }

        viewModelScope.launch {
            _isUpdateLoading.value = true
            try {

                val updatedExpense = original.copy(
                    nombre = _expenseName.value,
                    monto = montoNuevo,
                    categoria = _expenseCategory.value,
                    nota = _expenseNota.value,

                )


                val montoAdjustment = montoNuevo - originalMonto


                val success = updateExpenseUseCase(
                    updatedExpense = updatedExpense,
                    budgetId = budgetId,
                    montoAdjustment = montoAdjustment
                )

                if (success) {

                    _expense.value = updatedExpense
                    originalMonto = montoNuevo
                    _isModified.value = false
                    _eventFlow.emit(ExpenseDetailEvent.ShowMessage("Gasto actualizado con éxito."))
                } else {
                    _eventFlow.emit(ExpenseDetailEvent.ShowMessage("Error al actualizar el gasto. Inténtalo de nuevo."))
                }
            } catch (e: Exception) {
                _eventFlow.emit(ExpenseDetailEvent.ShowMessage("Error inesperado: ${e.localizedMessage}"))
            } finally {
                _isUpdateLoading.value = false
            }
        }
    }

    fun deleteExpense() {
        val expenseToDelete = _expense.value ?: return

        viewModelScope.launch {
            try {
                _isDeleting.value = true


                val wasDeleted = deleteExpenseUseCase(expenseToDelete.id)

                if (wasDeleted) {
                    _deleteSuccess.value = true // Activa la navegación de vuelta
                    _eventFlow.emit(ExpenseDetailEvent.ShowMessage("Gasto eliminado con éxito."))
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




    fun handleBackNavigation(navigateBack: () -> Unit) {
        if (_isModified.value) {

            _showUnsavedChangesDialog.value = true
        } else {

            navigateBack()
        }
    }

    fun confirmDiscard(navigateBack: () -> Unit) {
        // Descartar cambios y navegar
        _showUnsavedChangesDialog.value = false

        navigateBack()
    }

    fun cancelDiscard() {
        // Cerrar el diálogo, sin navegar
        _showUnsavedChangesDialog.value = false
    }
}