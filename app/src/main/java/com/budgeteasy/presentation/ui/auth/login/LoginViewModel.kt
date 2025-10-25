package com.budgeteasy.presentation.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgeteasy.domain.usecase.user.LoginUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val contrasena: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val userId: Int? = null  // 👈 AGREGADO
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChanged(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun onContrasenaChanged(newContrasena: String) {
        _uiState.value = _uiState.value.copy(contrasena = newContrasena)
    }

    fun login() {
        val currentState = _uiState.value

        // Validaciones básicas
        if (currentState.email.isEmpty()) {
            _uiState.value = currentState.copy(errorMessage = "El email no puede estar vacío")
            return
        }

        if (currentState.contrasena.isEmpty()) {
            _uiState.value = currentState.copy(errorMessage = "La contraseña no puede estar vacía")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            try {
                val user = loginUserUseCase(currentState.email, currentState.contrasena)

                if (user != null) {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        errorMessage = null,
                        userId = user.id  // 👈 GUARDAR userId
                    )
                } else {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = "Email o contraseña incorrectos"
                    )
                }
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