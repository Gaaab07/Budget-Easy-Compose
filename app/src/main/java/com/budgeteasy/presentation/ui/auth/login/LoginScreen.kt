package com.budgeteasy.presentation.ui.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.budgeteasy.presentation.theme.PrimaryGreen
import com.budgeteasy.presentation.ui.navigation.Screen

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkTheme = isSystemInDarkTheme() // 🔥 Detectar tema

    // 🔥 Colores adaptativos para TextField
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val labelColor = if (isDarkTheme) Color.White.copy(alpha = 0.7f) else Color.Gray

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título
        Text(
            text = "BudgetEasy",
            style = MaterialTheme.typography.displaySmall,
            color = PrimaryGreen,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Email TextField - 🔥 MÁS CLARO
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textColor, // 🔥 Blanco en dark, negro en light
                unfocusedTextColor = textColor,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = PrimaryGreen,
                unfocusedLabelColor = labelColor,
                cursorColor = PrimaryGreen
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        // Contraseña TextField - 🔥 MÁS CLARO
        OutlinedTextField(
            value = uiState.contrasena,
            onValueChange = { viewModel.onContrasenaChanged(it) },
            label = { Text("Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textColor, // 🔥 Blanco en dark, negro en light
                unfocusedTextColor = textColor,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = PrimaryGreen,
                unfocusedLabelColor = labelColor,
                cursorColor = PrimaryGreen
            ),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // Error message
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Login Button
        Button(
            onClick = { viewModel.login() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(24.dp),
                    color = MaterialTheme.colorScheme.background
                )
            } else {
                Text("Iniciar Sesión")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Register Link
        Text(
            text = "¿No tienes cuenta? Regístrate aquí",
            style = MaterialTheme.typography.bodyMedium,
            color = PrimaryGreen,
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable {
                    navController.navigate(Screen.Register.route)
                }
        )
    }

    // Handle login success - NAVEGA AL DASHBOARD
    if (uiState.isLoginSuccessful && uiState.userId != null) {
        LaunchedEffect(Unit) {
            navController.navigate(Screen.Dashboard.createRoute(uiState.userId!!)) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }
}