package com.budgeteasy.presentation.ui.expense.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.budgeteasy.presentation.ui.expense.detail.ExpenseDetailEvent
import com.budgeteasy.presentation.ui.expense.detail.components.CustomOutlinedTextField // <<-- IMPORTACIÓN CORRECTA
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
    navController: NavController,
    expenseId: Int,
    budgetId: Int,
    viewModel: ExpenseDetailViewModel = hiltViewModel()
) {
    // --- ESTADOS DE CARGA Y GASTO ---
    val expense by viewModel.expense.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val isDeleting by viewModel.isDeleting.collectAsStateWithLifecycle()
    val deleteSuccess by viewModel.deleteSuccess.collectAsStateWithLifecycle()
    val isUpdateLoading by viewModel.isUpdateLoading.collectAsStateWithLifecycle()
    val isModified by viewModel.isModified.collectAsStateWithLifecycle()
    val showUnsavedChangesDialog by viewModel.showUnsavedChangesDialog.collectAsStateWithLifecycle()

    // --- ESTADOS DE EDICIÓN ---
    val expenseName by viewModel.expenseName.collectAsStateWithLifecycle()
    val expenseMonto by viewModel.expenseMonto.collectAsStateWithLifecycle()
    val expenseCategory by viewModel.expenseCategory.collectAsStateWithLifecycle()
    val expenseNota by viewModel.expenseNota.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    // --- EFECTOS LATERALES ---

    // 1. Manejo de mensajes (SnackBar)
    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ExpenseDetailEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    // 2. Navegación de éxito (Eliminación/Modificación)
    LaunchedEffect(deleteSuccess, isUpdateLoading) {
        // La navegación se gestiona internamente en el ViewModel,
        // pero verificamos el éxito de la eliminación.
        if (deleteSuccess) {
            navController.popBackStack()
        }
    }

    // 3. Manejar la navegación hacia atrás con detección de cambios
    val onBack: () -> Unit = {
        // Llama a la lógica del ViewModel para verificar si hay cambios no guardados
        viewModel.handleBackNavigation { navController.popBackStack() }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle del gasto", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                actions = {
                    // Botón de eliminar
                    if (expense != null) {
                        IconButton(onClick = { showDeleteConfirmationDialog = true }, enabled = !isDeleting) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar gasto", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { paddingValues ->
        when {
            isLoading || expense == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    if (expense == null && !isLoading) {
                        Text(error ?: "Gasto no encontrado.", color = MaterialTheme.colorScheme.error)
                    } else {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            else -> {
                // --- CONTENIDO EDITABLE ---
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // 1. Nombre (Campo Editable)
                    CustomOutlinedTextField(
                        value = expenseName,
                        onValueChange = viewModel::onNameChange,
                        label = "Nombre del Gasto",
                        readOnly = isUpdateLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Monto (Campo Editable)
                    CustomOutlinedTextField(
                        value = expenseMonto,
                        onValueChange = viewModel::onMontoChange,
                        label = "Monto (S/.)",
                        keyboardType = KeyboardType.Decimal,
                        readOnly = isUpdateLoading
                    )

                    // Display fijo del monto original (opcional, para visualización de la diferencia)
                    Text(
                        text = "Original: S/.${String.format("%.2f", expense!!.monto)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Categoría (Campo Editable)
                    CustomOutlinedTextField(
                        value = expenseCategory,
                        onValueChange = viewModel::onCategoryChange,
                        label = "Categoría",
                        readOnly = isUpdateLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Nota (Campo Editable)
                    CustomOutlinedTextField(
                        value = expenseNota,
                        onValueChange = viewModel::onNotaChange,
                        label = "Nota",
                        readOnly = isUpdateLoading,
                        singleLine = false,
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 5. Fecha (Campo Fijo)
                    OutlinedTextField(
                        value = formatDate(expense!!.fecha),
                        onValueChange = {},
                        label = { Text("Fecha de Gasto") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // --- BOTONES DE ACCIÓN ---
                    Button(
                        onClick = viewModel::updateExpense,
                        // El botón se habilita solo si hay cambios y no está cargando
                        enabled = isModified && !isUpdateLoading && !isDeleting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
                    ) {
                        if (isUpdateLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Guardar Modificación", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error, // Fondo rojo
                            contentColor = Color.White // Texto blanco
                        )
                    ) {
                        Text(
                            text = "Cancelar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // --- DIÁLOGOS ---

    // 1. Diálogo de pérdida de cambios
    if (showUnsavedChangesDialog) {
        AlertDialog(
            onDismissRequest = viewModel::cancelDiscard,
            title = { Text("Descartar cambios") },
            text = { Text("Tienes cambios sin guardar. ¿Deseas salir y descartarlos?") },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDiscard(navController::popBackStack) }) {
                    Text("Descartar")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelDiscard) {
                    Text("Cancelar")
                }
            }
        )
    }

    // 2. Diálogo de confirmación de eliminación
    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text("Eliminar gasto") },
            text = { Text("¿Estás seguro de que deseas eliminar este gasto? Esta acción revertirá el monto en tu presupuesto.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExpense()
                        showDeleteConfirmationDialog = false
                    },
                    enabled = !isDeleting,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Eliminar", color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmationDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Función de formato de fecha (Mantenida)
private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm a", Locale("es", "PE"))
    return sdf.format(Date(timestamp))
}