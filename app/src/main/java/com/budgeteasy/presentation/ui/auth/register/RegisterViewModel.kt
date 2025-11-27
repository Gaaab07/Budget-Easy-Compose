package com.budgeteasy.presentation.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgeteasy.data.preferences.AppLanguage
import com.budgeteasy.data.preferences.LanguageManager
import com.budgeteasy.domain.model.User
import com.budgeteasy.domain.usecase.user.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val nombre: String = "",
    val apellidos: String = "",
    val email: String = "",
    val contrasena: String = "",
    val confirmContrasena: String = "",
    val numeroDeTelefono: String = "",
    val selectedLanguage: AppLanguage = AppLanguage.SPANISH,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccessful: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val languageManager: LanguageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onNombreChanged(newNombre: String) {
        _uiState.value = _uiState.value.copy(nombre = newNombre)
    }

    fun onApellidosChanged(newApellidos: String) {
        _uiState.value = _uiState.value.copy(apellidos = newApellidos)
    }

    fun onEmailChanged(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun onContrasenaChanged(newContrasena: String) {
        _uiState.value = _uiState.value.copy(contrasena = newContrasena)
    }

    fun onConfirmContrasenaChanged(newConfirmContrasena: String) {
        _uiState.value = _uiState.value.copy(confirmContrasena = newConfirmContrasena)
    }

    fun onNumeroDeTelefonoChanged(newNumeroDeTelefono: String) {
        _uiState.value = _uiState.value.copy(numeroDeTelefono = newNumeroDeTelefono)
    }

    fun onLanguageChanged(newLanguage: AppLanguage) {
        _uiState.value = _uiState.value.copy(selectedLanguage = newLanguage)
    }

    fun register() {
        val currentState = _uiState.value

        // Validaciones
        if (currentState.nombre.isEmpty()) {
            _uiState.value = currentState.copy(errorMessage = "El nombre no puede estar vacío")
            return
        }

        if (currentState.apellidos.isEmpty()) {
            _uiState.value = currentState.copy(errorMessage = "Los apellidos no pueden estar vacíos")
            return
        }

        if (currentState.email.isEmpty()) {
            _uiState.value = currentState.copy(errorMessage = "El email no puede estar vacío")
            return
        }

        if (!isValidEmail(currentState.email)) {
            _uiState.value = currentState.copy(errorMessage = "El email no es válido")
            return
        }

        if (currentState.contrasena.isEmpty()) {
            _uiState.value = currentState.copy(errorMessage = "La contraseña no puede estar vacía")
            return
        }

        if (currentState.contrasena.length < 6) {
            _uiState.value = currentState.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres")
            return
        }

        if (currentState.contrasena != currentState.confirmContrasena) {
            _uiState.value = currentState.copy(errorMessage = "Las contraseñas no coinciden")
            return
        }

        if (currentState.numeroDeTelefono.isEmpty()) {
            _uiState.value = currentState.copy(errorMessage = "El teléfono no puede estar vacío")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            try {
                val newUser = User(
                    nombre = currentState.nombre,
                    apellidos = currentState.apellidos,
                    email = currentState.email,
                    contrasena = currentState.contrasena,
                    numeroDeTelefono = currentState.numeroDeTelefono,
                    idioma = currentState.selectedLanguage.code // Guardar código de idioma
                )

                registerUserUseCase(newUser)


                languageManager.setLanguage(currentState.selectedLanguage)

                _uiState.value = currentState.copy(
                    isLoading = false,
                    isRegisterSuccessful = true,
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

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
}