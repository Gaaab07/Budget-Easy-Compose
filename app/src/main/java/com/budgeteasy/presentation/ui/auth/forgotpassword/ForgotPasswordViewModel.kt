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


enum class RecoveryState {
    EMAIL_INPUT,
    RESET_PASSWORD
}


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

) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState


    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }


    fun onNewPasswordChanged(password: String) {
        _uiState.update { it.copy(newPassword = password, errorMessage = null) }
    }


    fun onConfirmPasswordChanged(password: String) {
        _uiState.update { it.copy(confirmPassword = password, errorMessage = null) }
    }


    fun initiatePasswordReset() {
        if (!isValidEmail(_uiState.value.email)) {
            _uiState.update { it.copy(errorMessage = "Por favor, ingrese un email válido.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {

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


    fun resetPassword() {
        val state = _uiState.value


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

            val wasPasswordUpdated = updatePasswordUseCase(
                email = state.email,
                newPassword = state.newPassword
            )


            if (wasPasswordUpdated) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isPasswordResetSuccessful = true
                    )
                }
            } else {

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

        return email.isNotBlank() && email.contains("@") && email.contains(".")
    }
}