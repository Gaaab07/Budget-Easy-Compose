package com.budgeteasy.data.biometric

/**
 * Representa el resultado de la autenticación biométrica
 */
sealed class BiometricAuthResult {
    // Autenticación exitosa
    object Success : BiometricAuthResult()

    // Error de autenticación (huella incorrecta, rostro no reconocido, etc.)
    data class AuthenticationError(val errorCode: Int, val errorMessage: String) : BiometricAuthResult()

    // El dispositivo no soporta biometría o no está configurada
    data class BiometricNotAvailable(val reason: String) : BiometricAuthResult()

    // Usuario canceló la autenticación
    object UserCancelled : BiometricAuthResult()

    // Error de hardware (sensor no disponible)
    data class HardwareError(val errorMessage: String) : BiometricAuthResult()
}