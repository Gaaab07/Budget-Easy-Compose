package com.budgeteasy.presentation.ui.budget.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgeteasy.domain.model.Budget
import com.budgeteasy.domain.usecase.budget.CreateBudgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateBudgetUiState(
    val nombre: String = "",
    val montoPlaneado: String = "",
    val periodo: String = "1 mes",
    val descripcion: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isCreateSuccessful: Boolean = false
)

@HiltViewModel
class CreateBudgetViewModel @Inject constructor(
    private val createBudgetUseCase: CreateBudgetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateBudgetUiState())
    val uiState: StateFlow<CreateBudgetUiState> = _uiState

    fun onNombreChanged(newNombre: String) {
        _uiState.value = _uiState.value.copy(nombre = newNombre)
    }

    fun onMontoPlaneadoChanged(newMonto: String) {
        _uiState.value = _uiState.value.copy(montoPlaneado = newMonto)
    }

    fun onPeriodoChanged(newPeriodo: String) {
        _uiState.value = _uiState.value.copy(periodo = newPeriodo)
    }

    fun onDescripcionChanged(newDescripcion: String) {
        _uiState.value = _uiState.value.copy(descripcion = newDescripcion)
    }

    fun createBudget(userId: Int) {
        val currentState = _uiState.value

        if (currentState.nombre.isEmpty()) {
            _uiState.value = currentState.copy(errorMessage = "El nombre no puede estar vacío")
            return
        }

        if (currentState.montoPlaneado.isEmpty()) {
            _uiState.value = currentState.copy(errorMessage = "El monto no puede estar vacío")
            return
        }

        val monto = currentState.montoPlaneado.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            _uiState.value = currentState.copy(errorMessage = "El monto debe ser mayor a 0")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            try {
                val fechaActual = System.currentTimeMillis()
                val fechaFin = calcularFechaFin(fechaActual, currentState.periodo)

                val newBudget = Budget(
                    userId = userId,
                    nombre = currentState.nombre,
                    montoPlaneado = monto,
                    periodo = currentState.periodo,
                    fechaInicio = fechaActual,
                    fechaFin = fechaFin,
                    descripcion = currentState.descripcion
                )

                createBudgetUseCase(newBudget)

                _uiState.value = currentState.copy(
                    isLoading = false,
                    isCreateSuccessful = true,
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

    private fun calcularFechaFin(fechaInicio: Long, periodo: String): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = fechaInicio

        when (periodo) {
            "1 mes" -> calendar.add(java.util.Calendar.MONTH, 1)
            "3 meses" -> calendar.add(java.util.Calendar.MONTH, 3)
            "6 meses" -> calendar.add(java.util.Calendar.MONTH, 6)
            "1 año" -> calendar.add(java.util.Calendar.YEAR, 1)
            else -> calendar.add(java.util.Calendar.MONTH, 1)
        }

        return calendar.timeInMillis
    }
}
