package com.budgeteasy.presentation.ui.auth.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.budgeteasy.presentation.theme.PrimaryGreen
import com.budgeteasy.presentation.ui.navigation.Screen

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 1. Manejar el éxito del restablecimiento para navegar de vuelta al Login
    if (uiState.isPasswordResetSuccessful) {
        LaunchedEffect(Unit) {
            // Navega de vuelta a la pantalla de Login y borra el historial
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.ForgotPassword.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when (uiState.currentState) {
                RecoveryState.EMAIL_INPUT -> "Recuperar Contraseña"
                // RecoveryState.EMAIL_SENT (Eliminado)
                RecoveryState.RESET_PASSWORD -> "Establecer Nueva Contraseña"
            },
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryGreen,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        when (uiState.currentState) {
            RecoveryState.EMAIL_INPUT -> {
                EmailInputContent(
                    email = uiState.email,
                    onEmailChange = viewModel::onEmailChanged,
                    onSendClick = viewModel::initiatePasswordReset, // Llama al nuevo método del ViewModel
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage
                )
            }
            // RecoveryState.EMAIL_SENT -> { (Eliminado) }
            RecoveryState.RESET_PASSWORD -> {
                ResetPasswordContent(
                    newPassword = uiState.newPassword,
                    onNewPasswordChange = viewModel::onNewPasswordChanged,
                    confirmPassword = uiState.confirmPassword,
                    onConfirmPasswordChange = viewModel::onConfirmPasswordChanged,
                    onResetClick = viewModel::resetPassword,
                    errorMessage = uiState.errorMessage
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de Cancelar/Volver
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Cancelar / Volver al Login", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// --- Funciones auxiliares Composable para la UI ---

@Composable
private fun EmailInputContent(
    email: String,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Tu Email Registrado") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGreen)
    )
    if (errorMessage != null) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
    Button(
        onClick = onSendClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
        } else {
            Text("Iniciar Restablecimiento de Contraseña", color = Color.White)
        }
    }
}

// *** La función EmailSentContent fue eliminada ya que no se usa en este flujo directo. ***

@Composable
private fun ResetPasswordContent(
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    onResetClick: () -> Unit,
    errorMessage: String?
) {
    OutlinedTextField(
        value = newPassword,
        onValueChange = onNewPasswordChange,
        label = { Text("Nueva Contraseña") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGreen)
    )
    OutlinedTextField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        label = { Text("Confirmar Nueva Contraseña") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGreen)
    )
    if (errorMessage != null) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
    Button(
        onClick = onResetClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
    ) {
        Text("Restablecer Contraseña", color = Color.White)
    }
}