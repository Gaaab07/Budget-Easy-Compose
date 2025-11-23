// presentation/ui/budget/edit/EditBudgetViewModel.kt
package com.budgeteasy.presentation.ui.budget.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgeteasy.domain.model.Budget
import com.budgeteasy.domain.usecase.budget.GetBudgetByIdUseCase
import com.budgeteasy.domain.usecase.budget.UpdateBudgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditBudgetUiState(
    val budgetId: Int = 0,
    val nombre: String = "",
    val montoPlaneado: String = "",
    val montoGastado: Double = 0.0,
    val periodo: String = "",
    val descripcion: String = "",
    val fechaInicio: Long = 0,
    val fechaFin: Long = 0,
    val userId: Int = 0,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val updateSuccess: Boolean = false
)

@HiltViewModel
class EditBudgetViewModel @Inject constructor(
    private val getBudgetByIdUseCase: GetBudgetByIdUseCase,
    private val updateBudgetUseCase: UpdateBudgetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditBudgetUiState())
    val uiState: StateFlow<EditBudgetUiState> = _uiState

    fun loadBudget(budgetId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val budget = getBudgetByIdUseCase(budgetId)

                if (budget != null) {
                    _uiState.value = EditBudgetUiState(
                        budgetId = budget.id,
                        nombre = budget.nombre,
                        montoPlaneado = budget.montoPlaneado.toString(),
                        montoGastado = budget.montoGastado,
                        periodo = budget.periodo,
                        descripcion = budget.descripcion,
                        fechaInicio = budget.fechaInicio,
                        fechaFin = budget.fechaFin,
                        userId = budget.userId,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Presupuesto no encontrado"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar: ${e.message}"
                )
            }
        }
    }

    fun onNombreChanged(nombre: String) {
        _uiState.value = _uiState.value.copy(nombre = nombre, errorMessage = null)
    }

    fun onMontoPlaneadoChanged(monto: String) {
        // Solo permitir números y punto decimal
        if (monto.isEmpty() || monto.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.value = _uiState.value.copy(montoPlaneado = monto, errorMessage = null)
        }
    }

    fun onPeriodoChanged(periodo: String) {
        _uiState.value = _uiState.value.copy(periodo = periodo, errorMessage = null)
    }

    fun onDescripcionChanged(descripcion: String) {
        _uiState.value = _uiState.value.copy(descripcion = descripcion, errorMessage = null)
    }

    fun updateBudget() {
        val state = _uiState.value

        // Validaciones
        if (state.nombre.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El nombre no puede estar vacío")
            return
        }

        val montoPlaneado = state.montoPlaneado.toDoubleOrNull()
        if (montoPlaneado == null || montoPlaneado <= 0) {
            _uiState.value = state.copy(errorMessage = "El monto debe ser mayor a 0")
            return
        }

        // Validar que el nuevo monto no sea menor al gastado
        if (montoPlaneado < state.montoGastado) {
            _uiState.value = state.copy(
                errorMessage = "El monto planeado no puede ser menor al monto ya gastado (S/.${String.format("%.2f", state.montoGastado)})"
            )
            return
        }

        if (state.periodo.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El período no puede estar vacío")
            return
        }

        // Guardar cambios
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null)

            try {
                val updatedBudget = Budget(
                    id = state.budgetId,
                    userId = state.userId,
                    nombre = state.nombre,
                    montoPlaneado = montoPlaneado,
                    montoGastado = state.montoGastado, // NO se modifica
                    periodo = state.periodo,
                    descripcion = state.descripcion,
                    fechaInicio = state.fechaInicio,
                    fechaFin = state.fechaFin
                )

                updateBudgetUseCase(updatedBudget)

                _uiState.value = state.copy(
                    isSaving = false,
                    updateSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isSaving = false,
                    errorMessage = "Error al guardar: ${e.message}"
                )
            }
        }
    }
}
