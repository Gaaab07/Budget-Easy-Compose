package com.budgeteasy.data.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor de autenticación biométrica
 */
@Singleton
class BiometricAuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Verifica si el dispositivo soporta autenticación biométrica
     */
    fun isBiometricAvailable(): BiometricAuthResult {
        val biometricManager = BiometricManager.from(context)

        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                BiometricAuthResult.Success
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                BiometricAuthResult.BiometricNotAvailable("El dispositivo no tiene sensor biométrico")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                BiometricAuthResult.BiometricNotAvailable("Sensor biométrico no disponible temporalmente")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                BiometricAuthResult.BiometricNotAvailable("No hay huellas digitales o rostros registrados")
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                BiometricAuthResult.BiometricNotAvailable("Se requiere actualización de seguridad")
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                BiometricAuthResult.BiometricNotAvailable("Autenticación biométrica no soportada")
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                BiometricAuthResult.BiometricNotAvailable("Estado desconocido")
            }
            else -> {
                BiometricAuthResult.BiometricNotAvailable("Error desconocido")
            }
        }
    }

    /**
     * Muestra el diálogo de autenticación biométrica
     * @param activity Actividad donde se mostrará el diálogo
     * @param title Título del diálogo
     * @param subtitle Subtítulo del diálogo
     * @param negativeButtonText Texto del botón de cancelar
     * @param onSuccess Callback cuando la autenticación es exitosa
     * @param onError Callback cuando hay un error
     */
    fun authenticate(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        negativeButtonText: String,
        onSuccess: () -> Unit,
        onError: (BiometricAuthResult) -> Unit
    ) {
        // Verificar disponibilidad
        val availability = isBiometricAvailable()
        if (availability !is BiometricAuthResult.Success) {
            onError(availability)
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    val result = when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        BiometricPrompt.ERROR_CANCELED -> {
                            BiometricAuthResult.UserCancelled
                        }
                        BiometricPrompt.ERROR_HW_UNAVAILABLE,
                        BiometricPrompt.ERROR_HW_NOT_PRESENT -> {
                            BiometricAuthResult.HardwareError(errString.toString())
                        }
                        else -> {
                            BiometricAuthResult.AuthenticationError(errorCode, errString.toString())
                        }
                    }

                    onError(result)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // No hacemos nada aquí, el usuario puede reintentar
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}