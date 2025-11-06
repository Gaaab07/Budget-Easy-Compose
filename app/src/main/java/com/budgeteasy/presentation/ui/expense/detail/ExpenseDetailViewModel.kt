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

// Definimos los eventos que la UI necesita manejar
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

    // Variables de navegación (usando el método que ya tenías)
    private val expenseId: Int = savedStateHandle.get<Int>("expenseId") ?: 0
    private val budgetId: Int = savedStateHandle.get<Int>("budgetId") ?: 0

    // --- ESTADOS DE CARGA Y DATOS ---
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

    // --- ESTADOS DE EDICIÓN Y LÓGICA DE CAMBIOS (AÑADIDOS) ---
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

    // Control de diálogos de navegación
    private val _showUnsavedChangesDialog = MutableStateFlow(false)
    val showUnsavedChangesDialog: StateFlow<Boolean> = _showUnsavedChangesDialog.asStateFlow()

    // Canal para la comunicación de eventos a la UI (Snackbar, etc.)
    private val _eventFlow = MutableSharedFlow<ExpenseDetailEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    // Monto original para calcular la diferencia durante la actualización
    private var originalMonto: Double = 0.0

    init {
        loadExpense()
        observeEdits() // Inicia la detección de cambios
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

                // Usamos GetExpensesUseCase (asumiendo que devuelve un Flow)
                getExpensesUseCase(budgetId).collect { expenses ->
                    val expenseResult = expenses.find { it.id == expenseId }

                    if (expenseResult != null) {
                        _expense.value = expenseResult
                        // Inicializa los campos de edición
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

    // --- HANDLERS DE EDICIÓN ---

    fun onNameChange(name: String) {
        _expenseName.value = name
    }

    fun onMontoChange(monto: String) {
        // Simple validación para asegurar que solo se ingresen números y un punto decimal
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

    // --- LÓGICA DE DETECCIÓN DE CAMBIOS ---

    private fun observeEdits() {
        combine(
            _expenseName, _expenseMonto, _expenseCategory, _expenseNota, _expense
        ) { name, montoStr, category, nota, originalExpense ->

            if (originalExpense == null) return@combine false

            // Comparamos el monto como string formateado, que es lo que ve el usuario.
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

    // --- ACCIONES PRINCIPALES ---

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
                // 1. Crear el gasto actualizado
                val updatedExpense = original.copy(
                    nombre = _expenseName.value,
                    monto = montoNuevo,
                    categoria = _expenseCategory.value,
                    nota = _expenseNota.value,
                    // La fecha y el ID se mantienen
                )

                // 2. Calcular la diferencia (Ajuste = Nuevo monto - Monto Original)
                val montoAdjustment = montoNuevo - originalMonto

                // 3. Ejecutar el caso de uso transaccional
                val success = updateExpenseUseCase(
                    updatedExpense = updatedExpense,
                    budgetId = budgetId,
                    montoAdjustment = montoAdjustment
                )

                if (success) {
                    // Actualizar el estado local (para la próxima edición)
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

                // LLAMA al UseCase con el ID (asumiendo que tu UseCase requiere solo el ID)
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

    // --- LÓGICA DE NAVEGACIÓN HACIA ATRÁS ---

    /**
     * Maneja la navegación hacia atrás, mostrando un diálogo si hay cambios sin guardar.
     * @param navigateBack La acción de navegación real (generalmente navController.popBackStack()).
     */
    fun handleBackNavigation(navigateBack: () -> Unit) {
        if (_isModified.value) {
            // Guardamos la acción de navegación para ejecutarla después de la confirmación
            // NOTA: Usamos el estado del diálogo, no una variable auxiliar, para ser más directo.
            _showUnsavedChangesDialog.value = true
        } else {
            // No hay cambios, navegamos directamente
            navigateBack()
        }
    }

    fun confirmDiscard(navigateBack: () -> Unit) {
        // Descartar cambios y navegar
        _showUnsavedChangesDialog.value = false
        // Llama a la acción de navegación que se pasó
        navigateBack()
    }

    fun cancelDiscard() {
        // Cerrar el diálogo, sin navegar
        _showUnsavedChangesDialog.value = false
    }
}