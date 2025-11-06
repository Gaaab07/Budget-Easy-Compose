package com.budgeteasy.presentation.ui.expense.detail.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Componente de campo de texto con estilo personalizado para la aplicación.
 * Garantiza que el campo es editable por defecto.
 *
 * @param useDecimalFormat Si es true, formatea el valor para mostrar separadores de miles (punto)
 * y decimales (coma), limpiando la entrada antes de enviarla al ViewModel.
 */
@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    readOnly: Boolean = false, // Permite la edición por defecto
    singleLine: Boolean = true,
    maxLines: Int = 1,
    // 🆕 Parámetro para el formateo
    useDecimalFormat: Boolean = false,
    // 🆕 Placeholder (lo extrañaba tu componente, lo añado para consistencia)
    placeholder: String = ""
) {
    // Definimos el formato local (asumiendo formato tipo Perú: 1.000,00)
    val decimalFormat = remember {
        val symbols = DecimalFormatSymbols(Locale("es", "PE")) // Usamos la localización de Perú
        symbols.decimalSeparator = ','
        symbols.groupingSeparator = '.'
        DecimalFormat("#,##0.00", symbols) // Formato con separador de miles (.), decimal (,) y 2 decimales
    }

    // 1. Determinar el valor a mostrar al usuario (Formateado o Limpio)
    val displayedValue = if (useDecimalFormat && value.isNotEmpty()) {
        try {
            // Reemplazar el punto por coma para que el DecimalFormat lo maneje como un número Double con la configuración local
            val valueToParse = value.replace('.', ',')
            // Esto solo es una limpieza adicional para asegurar que sea un Double
            val cleanedForParsing = valueToParse.replace(Regex("[^0-9,-]"), "")

            val parsedDouble = cleanedForParsing.replace(',', '.').toDouble()

            // Formatear el Double a String con el separador de miles y decimales
            decimalFormat.format(parsedDouble)
        } catch (e: NumberFormatException) {
            value // Si no es un número válido aún (ej: solo un '-'), mostrarlo tal cual
        }
    } else {
        value
    }

    OutlinedTextField(
        value = displayedValue, // Usar el valor formateado para la visualización
        onValueChange = { newValue ->
            if (useDecimalFormat && keyboardType == KeyboardType.Decimal) {
                // 2. Limpieza de la entrada del usuario antes de enviarla al ViewModel

                // Paso 1: Quitar todos los puntos (separadores de miles visuales)
                val cleanedStep1 = newValue.replace(".", "")

                // Paso 2: Reemplazar la coma (separador decimal ingresado por el usuario) por punto
                val cleanedStep2 = cleanedStep1.replace(',', '.')

                // Paso 3: Permitir solo números y UN punto decimal
                val finalCleanedValue = cleanedStep2.replace(Regex("[^0-9.]"), "")

                // Notificar al ViewModel con el valor limpio (e.g., "1000.00")
                onValueChange(finalCleanedValue)
            } else {
                // Si no se usa el formato decimal, se envía el valor original
                onValueChange(newValue)
            }
        },
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        readOnly = readOnly,
        singleLine = singleLine,
        maxLines = maxLines,
        // Usar keyboardOptions para permitir el Decimal
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
        modifier = modifier
    )
}