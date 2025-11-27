package com.budgeteasy.presentation.ui.expense.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.budgeteasy.presentation.ui.expense.detail.components.CustomOutlinedTextField
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowDropDown


data class Category(val name: String, val icon: String)

private val CATEGORIES = listOf(
    Category("Restaurantes", "🍽️"),
    Category("Compras", "🛍️"),
    Category("Transporte", "🚗"),
    Category("Entretenimiento", "🎵"),
    Category("Salud", "💊"),
    Category("Educación", "📖"),
    Category("Servicios", "🔨"),
    Category("Hogar", "🏠"),
    Category("Otros", "💰")
)

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


    val expenseName by viewModel.expenseName.collectAsStateWithLifecycle()
    val expenseMonto by viewModel.expenseMonto.collectAsStateWithLifecycle()
    val expenseCategory by viewModel.expenseCategory.collectAsStateWithLifecycle()
    val expenseNota by viewModel.expenseNota.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }


    var expandedDropdown by remember { mutableStateOf(false) }

    var selectedCategory by remember {
        mutableStateOf(CATEGORIES.find { it.name == expenseCategory } ?: CATEGORIES[0])
    }


    LaunchedEffect(expenseCategory) {
        selectedCategory = CATEGORIES.find { it.name == expenseCategory } ?: CATEGORIES[0]
    }




    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ExpenseDetailEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }


    LaunchedEffect(deleteSuccess, isUpdateLoading) {
        if (deleteSuccess) {
            navController.popBackStack()
        }
    }


    val onBack: () -> Unit = {
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    CustomOutlinedTextField(
                        value = expenseName,
                        onValueChange = viewModel::onNameChange,
                        label = "Nombre del Gasto",
                        readOnly = isUpdateLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))


                    CustomOutlinedTextField(
                        value = expenseMonto,
                        onValueChange = viewModel::onMontoChange,
                        label = "Monto (S/.)",
                        keyboardType = KeyboardType.Decimal,
                        readOnly = isUpdateLoading,
                        useDecimalFormat = true
                    )


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
                                .clickable { expandedDropdown = true }
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Categoría",
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
                                        text = selectedCategory.icon,
                                        fontSize = 28.sp
                                    )
                                    Text(
                                        text = selectedCategory.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Seleccionar categoría",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }


                            DropdownMenu(
                                expanded = expandedDropdown,
                                onDismissRequest = { expandedDropdown = false },
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                CATEGORIES.forEach { category ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Text(
                                                    text = category.icon,
                                                    fontSize = 24.sp
                                                )
                                                Text(
                                                    text = category.name,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedCategory = category
                                            // Llama al ViewModel con el nuevo nombre de la categoría
                                            viewModel.onCategoryChange(category.name)
                                            expandedDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }


                    CustomOutlinedTextField(
                        value = expenseNota,
                        onValueChange = viewModel::onNotaChange,
                        label = "Nota",
                        readOnly = isUpdateLoading,
                        singleLine = false,
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(16.dp))


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


                    Button(
                        onClick = viewModel::updateExpense,

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


private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm a", Locale("es", "PE"))
    return sdf.format(Date(timestamp))
}