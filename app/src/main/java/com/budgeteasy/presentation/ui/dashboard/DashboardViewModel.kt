package com.budgeteasy.presentation.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgeteasy.domain.model.Budget
import com.budgeteasy.domain.model.User
import com.budgeteasy.domain.usecase.budget.GetBudgetsUseCase
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: Int = savedStateHandle.get<Int>("userId") ?: 0

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            try {
                _uiState.value = DashboardUiState.Loading

                // Cargar usuario
                val userData = getUserUseCase(userId)
                _user.value = userData

                // Cargar presupuestos
                getBudgetsUseCase(userId).collect { budgets ->
                    _uiState.value = DashboardUiState.Success(budgets)
                }
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(e.message ?: "Error desconocido")
            }
        }
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