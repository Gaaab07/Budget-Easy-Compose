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

    useDecimalFormat: Boolean = false,

    placeholder: String = ""
) {

    val decimalFormat = remember {
        val symbols = DecimalFormatSymbols(Locale("es", "PE"))
        symbols.decimalSeparator = ','
        symbols.groupingSeparator = '.'
        DecimalFormat("#,##0.00", symbols)
    }


    val displayedValue = if (useDecimalFormat && value.isNotEmpty()) {
        try {

            val valueToParse = value.replace('.', ',')

            val cleanedForParsing = valueToParse.replace(Regex("[^0-9,-]"), "")

            val parsedDouble = cleanedForParsing.replace(',', '.').toDouble()


            decimalFormat.format(parsedDouble)
        } catch (e: NumberFormatException) {
            value
        }
    } else {
        value
    }

    OutlinedTextField(
        value = displayedValue,
        onValueChange = { newValue ->
            if (useDecimalFormat && keyboardType == KeyboardType.Decimal) {



                val cleanedStep1 = newValue.replace(".", "")


                val cleanedStep2 = cleanedStep1.replace(',', '.')


                val finalCleanedValue = cleanedStep2.replace(Regex("[^0-9.]"), "")


                onValueChange(finalCleanedValue)
            } else {

                onValueChange(newValue)
            }
        },
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        readOnly = readOnly,
        singleLine = singleLine,
        maxLines = maxLines,

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