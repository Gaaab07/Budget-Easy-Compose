package com.budgeteasy.presentation.ui.auth.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import com.budgeteasy.domain.usecase.user.UpdatePasswordUseCase // <-- 1. IMPORTA EL USECASE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Estados del flujo de la interfaz de usuario (EMAIL_SENT eliminado)
enum class RecoveryState {
    EMAIL_INPUT,
    RESET_PASSWORD
}

// Data class que representa el estado completo de la UI
data class ForgotPasswordUiState(
    val email: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentState: RecoveryState = RecoveryState.EMAIL_INPUT,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isPasswordResetSuccessful: Boolean = false
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val updatePasswordUseCase: UpdatePasswordUseCase
    // Aquí irían inyecciones de dependencias como AuthRepository o UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState

    // Maneja los cambios en el campo de email
    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    // Maneja los cambios en el campo de nueva contraseña
    fun onNewPasswordChanged(password: String) {
        _uiState.update { it.copy(newPassword = password, errorMessage = null) }
    }

    // Maneja los cambios en el campo de confirmación de contraseña
    fun onConfirmPasswordChanged(password: String) {
        _uiState.update { it.copy(confirmPassword = password, errorMessage = null) }
    }

    /**
     * Simula el proceso de inicio de recuperación (salta el envío de email).
     * Pasa directamente al estado de RESET_PASSWORD.
     */
    fun initiatePasswordReset() {
        if (!isValidEmail(_uiState.value.email)) {
            _uiState.update { it.copy(errorMessage = "Por favor, ingrese un email válido.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            // SIMULACIÓN: Simplemente pasamos al estado de restablecimiento de contraseña
            delay(1000)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentState = RecoveryState.RESET_PASSWORD,
                    errorMessage = null
                )
            }
        }
    }

    /**
     * Simula el proceso final de restablecimiento de contraseña.
     */
    fun resetPassword() {
        val state = _uiState.value

        // --- VALIDACIONES (esto ya lo tienes bien) ---
        if (state.newPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres.") }
            return
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            // --- LÓGICA REAL (reemplaza el delay) ---

            // 3. LLAMA AL CASO DE USO con el email y la nueva contraseña
            val wasPasswordUpdated = updatePasswordUseCase(
                email = state.email,
                newPassword = state.newPassword
            )

            // 4. ACTUALIZA LA UI SEGÚN EL RESULTADO
            if (wasPasswordUpdated) {
                // Éxito: La contraseña se cambió en la base de datos
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isPasswordResetSuccessful = true // Indica a la UI que navegue
                    )
                }
            } else {
                // Error: El usuario no existía o hubo un fallo en la DB
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No se pudo actualizar la contraseña. El email podría no existir."
                    )
                }
            }
        }
    }


    private fun isValidEmail(email: String): Boolean {
        // Validación de email básica (sólo para fines de UI)
        return email.isNotBlank() && email.contains("@") && email.contains(".")
    }
}