package com.budgeteasy.presentation.ui.auth.register

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.budgeteasy.R
import com.budgeteasy.data.preferences.AppLanguage
import com.budgeteasy.presentation.theme.PrimaryGreen
import com.budgeteasy.presentation.ui.navigation.Screen

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var expandedIdioma by remember { mutableStateOf(false) }

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
            text = stringResource(R.string.register),
            style = MaterialTheme.typography.displaySmall,
            color = PrimaryGreen,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Nombre TextField
        TextField(
            value = uiState.nombre,
            onValueChange = { viewModel.onNombreChanged(it) },
            label = { Text(stringResource(R.string.name)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true
        )

        // Apellidos TextField
        TextField(
            value = uiState.apellidos,
            onValueChange = { viewModel.onApellidosChanged(it) },
            label = { Text(stringResource(R.string.last_name)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true
        )

        // Email TextField
        TextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true
        )

        // Contraseña TextField
        TextField(
            value = uiState.contrasena,
            onValueChange = { viewModel.onContrasenaChanged(it) },
            label = { Text(stringResource(R.string.password)) },
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
            label = { Text(stringResource(R.string.confirm_password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        // Teléfono TextField (eliminar si no lo necesitas en la BD)
        TextField(
            value = uiState.numeroDeTelefono,
            onValueChange = { viewModel.onNumeroDeTelefonoChanged(it) },
            label = { Text("Teléfono") }, // Este no está en strings porque no es crítico
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        // Selector de Idioma (Mejorado)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedIdioma = true }
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.language),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (uiState.selectedLanguage == AppLanguage.SPANISH) "🇪🇸" else "🇺🇸",
                            fontSize = 28.sp
                        )
                        Text(
                            text = uiState.selectedLanguage.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Seleccionar idioma",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Dropdown Menu
                DropdownMenu(
                    expanded = expandedIdioma,
                    onDismissRequest = { expandedIdioma = false },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = "🇪🇸", fontSize = 24.sp)
                                Text(text = "Español", style = MaterialTheme.typography.bodyLarge)
                            }
                        },
                        onClick = {
                            viewModel.onLanguageChanged(AppLanguage.SPANISH)
                            expandedIdioma = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = "🇺🇸", fontSize = 24.sp)
                                Text(text = "English", style = MaterialTheme.typography.bodyLarge)
                            }
                        },
                        onClick = {
                            viewModel.onLanguageChanged(AppLanguage.ENGLISH)
                            expandedIdioma = false
                        }
                    )
                }
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
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
            shape = RoundedCornerShape(12.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.background
                )
            } else {
                Text(
                    text = stringResource(R.string.register),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login Link
        Text(
            text = stringResource(R.string.already_have_account) + " " + stringResource(R.string.login_here),
            style = MaterialTheme.typography.bodyMedium,
            color = PrimaryGreen,
            modifier = Modifier.clickable {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }
        )
    }

    // Handle register success - Navega al Login y recrea activity para aplicar idioma
    if (uiState.isRegisterSuccessful) {
        LaunchedEffect(Unit) {
            // Recrear activity para aplicar el nuevo idioma
            (navController.context as? ComponentActivity)?.recreate()
        }
    }
}