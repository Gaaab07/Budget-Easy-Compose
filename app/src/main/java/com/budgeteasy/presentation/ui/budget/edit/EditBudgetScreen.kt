// presentation/ui/budget/edit/EditBudgetScreen.kt
package com.budgeteasy.presentation.ui.budget.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.budgeteasy.data.preferences.AppLanguage
import com.budgeteasy.presentation.theme.PrimaryGreen
import com.budgeteasy.presentation.utils.getCurrentLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetScreen(
    navController: NavController,
    budgetId: Int,
    viewModel: EditBudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentLanguage = getCurrentLanguage()


    LaunchedEffect(budgetId) {
        viewModel.loadBudget(budgetId)
    }


    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (currentLanguage == AppLanguage.SPANISH)
                            "✏️ Editar Presupuesto"
                        else
                            "✏️ Edit Budget"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            // Pantalla de carga mientras se obtienen los datos
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                    Text(
                        text = if (currentLanguage == AppLanguage.SPANISH)
                            "Cargando datos..."
                        else
                            "Loading data...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = if (currentLanguage == AppLanguage.SPANISH)
                        "Modifica los datos de tu presupuesto"
                    else
                        "Modify your budget data",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))


                OutlinedTextField(
                    value = uiState.nombre,
                    onValueChange = viewModel::onNombreChanged,
                    label = {
                        Text(
                            if (currentLanguage == AppLanguage.SPANISH)
                                "Nombre del presupuesto"
                            else
                                "Budget name"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )


                OutlinedTextField(
                    value = uiState.montoPlaneado,
                    onValueChange = viewModel::onMontoPlaneadoChanged,
                    label = {
                        Text(
                            if (currentLanguage == AppLanguage.SPANISH)
                                "Monto planeado"
                            else
                                "Planned amount"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    prefix = { Text("S/. ") },
                    shape = RoundedCornerShape(12.dp),
                    supportingText = {
                        Text(
                            if (currentLanguage == AppLanguage.SPANISH)
                                "Solo números. Ejemplo: 5000"
                            else
                                "Numbers only. Example: 5000"
                        )
                    }
                )


                if (uiState.montoGastado > 0 && uiState.montoPlaneado.toDoubleOrNull() != null) {
                    val nuevoMonto = uiState.montoPlaneado.toDouble()
                    if (nuevoMonto < uiState.montoGastado) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "⚠️",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Column {
                                    Text(
                                        text = if (currentLanguage == AppLanguage.SPANISH)
                                            "Monto no válido"
                                        else
                                            "Invalid amount",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.SPANISH)
                                            "El monto planeado no puede ser menor al monto ya gastado (S/.${String.format("%.2f", uiState.montoGastado)})"
                                        else
                                            "Planned amount cannot be less than spent amount (S/.${String.format("%.2f", uiState.montoGastado)})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }

                // Campo: Periodo
                OutlinedTextField(
                    value = uiState.periodo,
                    onValueChange = viewModel::onPeriodoChanged,
                    label = {
                        Text(
                            if (currentLanguage == AppLanguage.SPANISH)
                                "Período"
                            else
                                "Period"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    supportingText = {
                        Text(
                            if (currentLanguage == AppLanguage.SPANISH)
                                "Ejemplo: Mensual, Semanal, Anual"
                            else
                                "Example: Monthly, Weekly, Annual"
                        )
                    }
                )

                // Campo: Descripción (opcional)
                OutlinedTextField(
                    value = uiState.descripcion,
                    onValueChange = viewModel::onDescripcionChanged,
                    label = {
                        Text(
                            if (currentLanguage == AppLanguage.SPANISH)
                                "Descripción (opcional)"
                            else
                                "Description (optional)"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp)
                )

                // 💡 Información del monto gastado (SOLO LECTURA)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "💡",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Column {
                            Text(
                                text = if (currentLanguage == AppLanguage.SPANISH)
                                    "Monto gastado actual"
                                else
                                    "Current spent amount",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "S/.${String.format("%.2f", uiState.montoGastado)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (currentLanguage == AppLanguage.SPANISH)
                                    "Este valor no se puede modificar manualmente. Se calcula automáticamente sumando todos los gastos registrados."
                                else
                                    "This value cannot be modified manually. It's calculated automatically by summing all registered expenses.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // Mensaje de error general
                uiState.errorMessage?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "❌",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de acción (Cancelar y Guardar)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón Cancelar
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !uiState.isSaving
                    ) {
                        Text(
                            if (currentLanguage == AppLanguage.SPANISH)
                                "Cancelar"
                            else
                                "Cancel",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    // Botón Guardar Cambios
                    Button(
                        onClick = { viewModel.updateBudget() },
                        enabled = !uiState.isSaving,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                if (currentLanguage == AppLanguage.SPANISH)
                                    "💾 Guardar"
                                else
                                    "💾 Save",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Espacio adicional al final
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
