package com.budgeteasy.presentation.ui.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.budgeteasy.presentation.theme.PrimaryGreen

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var expandedIdioma by remember { mutableStateOf(false) }
    val idiomas = listOf("es", "en", "pt")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Título
        Text(
            text = "Crear Cuenta",
            style = MaterialTheme.typography.displaySmall,
            color = PrimaryGreen,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Nombre TextField
        TextField(
            value = uiState.nombre,
            onValueChange = { viewModel.onNombreChanged(it) },
            label = { Text("Nombre") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true
        )

        // Apellidos TextField
        TextField(
            value = uiState.apellidos,
            onValueChange = { viewModel.onApellidosChanged(it) },
            label = { Text("Apellidos") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true
        )

        // Email TextField
        TextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true
        )

        // Contraseña TextField
        TextField(
            value = uiState.contrasena,
            onValueChange = { viewModel.onContrasenaChanged(it) },
            label = { Text("Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        // Confirmar Contraseña TextField
        TextField(
            value = uiState.confirmContrasena,
            onValueChange = { viewModel.onConfirmContrasenaChanged(it) },
            label = { Text("Confirmar Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        // Teléfono TextField
        TextField(
            value = uiState.numeroDeTelefono,
            onValueChange = { viewModel.onNumeroDeTelefonoChanged(it) },
            label = { Text("Teléfono") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true
        )

        // Idioma Dropdown
        Button(
            onClick = { expandedIdioma = !expandedIdioma },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(bottom = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Text("Idioma: ${uiState.idioma}")
        }

        DropdownMenu(
            expanded = expandedIdioma,
            onDismissRequest = { expandedIdioma = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            idiomas.forEach { idioma ->
                DropdownMenuItem(
                    text = { Text(idioma) },
                    onClick = {
                        viewModel.onIdiomaChanged(idioma)
                        expandedIdioma = false
                    }
                )
            }
        }

        // Error message
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Register Button
        Button(
            onClick = { viewModel.register() },
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
                Text("Registrarse")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login Link
        Text(
            text = "¿Ya tienes cuenta? Inicia sesión aquí",
            style = MaterialTheme.typography.bodyMedium,
            color = PrimaryGreen
        )
    }

    // Handle register success
    if (uiState.isRegisterSuccessful) {
        onRegisterSuccess()
    }
}