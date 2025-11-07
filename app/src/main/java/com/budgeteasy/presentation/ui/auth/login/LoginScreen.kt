package com.budgeteasy.presentation.ui.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.budgeteasy.R
import com.budgeteasy.data.biometric.BiometricAuthManager
import com.budgeteasy.data.biometric.BiometricAuthResult
import com.budgeteasy.presentation.theme.PrimaryGreen
import com.budgeteasy.presentation.ui.navigation.Screen

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    // Biometric Manager
    val biometricAuthManager = remember { BiometricAuthManager(context) }

    // Estados locales
    var isBiometricAvailable by remember { mutableStateOf(false) }
    var biometricError by remember { mutableStateOf<String?>(null) }

    // Strings traducibles (se cargan UNA VEZ al inicio)
    val biometricTitle = stringResource(R.string.biometric_title)
    val biometricSubtitle = stringResource(R.string.biometric_subtitle)
    val cancelText = stringResource(R.string.cancel)
    val biometricErrorText = stringResource(R.string.biometric_error)
    val cannotUseBiometricText = stringResource(R.string.biometric_error)

    // Verificar disponibilidad de biometría al iniciar
    LaunchedEffect(Unit) {
        val availability = biometricAuthManager.isBiometricAvailable()
        isBiometricAvailable = availability is BiometricAuthResult.Success
    }

    // Función para autenticar con biometría
    val authenticateWithBiometric = {
        if (activity == null) {
            biometricError = cannotUseBiometricText
        } else {
            biometricAuthManager.authenticate(
                activity = activity,
                title = biometricTitle,
                subtitle = biometricSubtitle,
                negativeButtonText = cancelText,
                onSuccess = {
                    viewModel.loginWithBiometric()
                    biometricError = null
                },
                onError = { result ->
                    biometricError = when (result) {
                        is BiometricAuthResult.UserCancelled -> null
                        is BiometricAuthResult.AuthenticationError -> result.errorMessage
                        is BiometricAuthResult.BiometricNotAvailable -> result.reason
                        is BiometricAuthResult.HardwareError -> result.errorMessage
                        else -> biometricErrorText
                    }
                }
            )
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
        // Título
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displaySmall,
            color = PrimaryGreen,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = stringResource(R.string.login),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 🔐 BOTÓN DE HUELLA DIGITAL
        if (isBiometricAvailable) {
            Card(
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 24.dp)
                    .clickable { authenticateWithBiometric() },
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryGreen.copy(alpha = 0.1f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = stringResource(R.string.biometric_login),
                        tint = PrimaryGreen,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Text(
                text = stringResource(R.string.biometric_login),
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryGreen,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Divisor "O"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.or),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
        }

        // Error de biometría
        if (biometricError != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = biometricError ?: "",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Email TextField
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = PrimaryGreen,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = PrimaryGreen
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        // Contraseña TextField
        OutlinedTextField(
            value = uiState.contrasena,
            onValueChange = { viewModel.onContrasenaChanged(it) },
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = PrimaryGreen,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = PrimaryGreen
            ),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // ¿Olvidó Contraseña?
        Text(
            text = stringResource(R.string.forgot_password),
            style = MaterialTheme.typography.bodySmall,
            color = PrimaryGreen,
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 24.dp)
                .clickable {
                    navController.navigate(Screen.ForgotPassword.route)
                }
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
                    color = Color.White
                )
            } else {
                Text(
                    text = stringResource(R.string.login),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Register Link
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.dont_have_account),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.sign_up_here),
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
    }

    // Handle login success
    if (uiState.isLoginSuccessful && uiState.userId != null) {
        LaunchedEffect(Unit) {
            navController.navigate(Screen.Dashboard.createRoute(uiState.userId!!)) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }
}