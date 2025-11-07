package com.budgeteasy.presentation.ui.auth.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgeteasy.data.biometric.BiometricAuthManager
import com.budgeteasy.domain.usecase.user.LoginUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    val userId: Int? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    @ApplicationContext context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    // 🔐 Biometric Auth Manager
    val biometricAuthManager = BiometricAuthManager(context)

    fun onEmailChanged(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun onContrasenaChanged(newContrasena: String) {
        _uiState.value = _uiState.value.copy(contrasena = newContrasena)
    }

    /**
     * Login tradicional con email y contraseña
     */
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
                        userId = user.id
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

    /**
     * 🔐 Login con autenticación biométrica
     * Usa las credenciales que ya están en los campos de texto
     */
    fun loginWithBiometric() {
        val currentState = _uiState.value

        // Verificar que haya credenciales disponibles
        if (currentState.email.isEmpty() || currentState.contrasena.isEmpty()) {
            _uiState.value = currentState.copy(
                errorMessage = "Primero inicia sesión con email y contraseña al menos una vez"
            )
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
                        userId = user.id
                    )
                } else {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = "Error de autenticación biométrica"
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