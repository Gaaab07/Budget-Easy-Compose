// presentation/ui/budget/list/BudgetListScreen.kt
package com.budgeteasy.presentation.ui.budget.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.budgeteasy.data.preferences.AppLanguage
import com.budgeteasy.domain.model.Budget
import com.budgeteasy.presentation.theme.PrimaryGreen
import com.budgeteasy.presentation.ui.navigation.BottomNavigationBar
import com.budgeteasy.presentation.ui.navigation.Screen
import com.budgeteasy.presentation.utils.getCurrentLanguage

@Composable
fun BudgetListScreen(
    navController: NavController,
    userId: Int,
    viewModel: BudgetListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentLanguage = getCurrentLanguage()

    // ⭐ Mostrar mensaje de éxito al eliminar
    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            // Aquí podrías mostrar un Snackbar
            viewModel.clearDeleteSuccess()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadBudgets(userId)
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, userId = userId)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.SPANISH)
                        "Mis Presupuestos"
                    else
                        "My Budgets",
                    style = MaterialTheme.typography.displaySmall,
                    color = PrimaryGreen
                )

                Button(
                    onClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(if (currentLanguage == AppLanguage.SPANISH) "Salir" else "Logout")
                }
            }

            // Create Budget Button
            Button(
                onClick = {
                    navController.navigate(Screen.CreateBudget.createRoute(userId))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text(
                    if (currentLanguage == AppLanguage.SPANISH)
                        "+ Crear Presupuesto"
                    else
                        "+ Create Budget"
                )
            }

            // ⭐ Mostrar mensaje de error si existe
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.clearErrorMessage() }) {
                            Text("✕")
                        }
                    }
                }
            }

            // Content
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryGreen)
                    }
                }
                uiState.budgets.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "💰",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (currentLanguage == AppLanguage.SPANISH)
                                    "No tienes presupuestos. ¡Crea uno!"
                                else
                                    "You have no budgets. Create one!",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.budgets) { budget ->
                            BudgetCard(
                                budget = budget,
                                currentLanguage = currentLanguage,
                                isDeleting = uiState.deletingBudgetId == budget.id,
                                onEditClick = {
                                    // ⭐ Navegar a pantalla de edición
                                    navController.navigate(
                                        Screen.EditBudget.createRoute(budget.id)
                                    )
                                },
                                onDeleteClick = {
                                    // ⭐ Eliminar presupuesto
                                    viewModel.deleteBudget(budget)
                                },
                                onClick = {
                                    navController.navigate(
                                        Screen.ExpenseList.createRoute(
                                            budget.id,
                                            budget.nombre,
                                            budget.montoPlaneado
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetCard(
    budget: Budget,
    currentLanguage: AppLanguage,
    isDeleting: Boolean = false,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    // ⭐ Estado para mostrar el menú y dialog de confirmación
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // ⭐ Dialog de confirmación de eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Text(text = "⚠️", style = MaterialTheme.typography.displayMedium)
            },
            title = {
                Text(
                    text = if (currentLanguage == AppLanguage.SPANISH)
                        "Eliminar Presupuesto"
                    else
                        "Delete Budget"
                )
            },
            text = {
                Column {
                    Text(
                        text = if (currentLanguage == AppLanguage.SPANISH)
                            "¿Estás seguro de que deseas eliminar el presupuesto \"${budget.nombre}\"?"
                        else
                            "Are you sure you want to delete the budget \"${budget.nombre}\"?"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.SPANISH)
                            "⚠️ Esta acción también eliminará todos los gastos asociados."
                        else
                            "⚠️ This action will also delete all associated expenses.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.SPANISH)
                            "Esta acción no se puede deshacer."
                        else
                            "This action cannot be undone.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        if (currentLanguage == AppLanguage.SPANISH)
                            "Eliminar"
                        else
                            "Delete"
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        if (currentLanguage == AppLanguage.SPANISH)
                            "Cancelar"
                        else
                            "Cancel"
                    )
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // ⭐ Header con nombre y menú de opciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = budget.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryGreen,
                    modifier = Modifier.weight(1f)
                )

                // ⭐ Menú de opciones (Editar/Eliminar)
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        // Opción Editar
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = PrimaryGreen
                                    )
                                    Text(
                                        if (currentLanguage == AppLanguage.SPANISH)
                                            "Editar"
                                        else
                                            "Edit"
                                    )
                                }
                            },
                            onClick = {
                                showMenu = false
                                onEditClick()
                            }
                        )

                        // Opción Eliminar
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.SPANISH)
                                            "Eliminar"
                                        else
                                            "Delete",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            onClick = {
                                showMenu = false
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Periodo
            Text(
                text = if (currentLanguage == AppLanguage.SPANISH)
                    "Período: ${budget.periodo}"
                else
                    "Period: ${budget.periodo}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progreso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.SPANISH)
                        "Gastado: S/.${String.format("%.2f", budget.montoGastado)}"
                    else
                        "Spent: S/.${String.format("%.2f", budget.montoGastado)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (currentLanguage == AppLanguage.SPANISH)
                        "Presupuesto: S/.${String.format("%.2f", budget.montoPlaneado)}"
                    else
                        "Budget: S/.${String.format("%.2f", budget.montoPlaneado)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            val progressPercentage = if (budget.montoPlaneado > 0) {
                (budget.montoGastado / budget.montoPlaneado).coerceIn(0.0, 1.0).toFloat()
            } else {
                0f
            }

            LinearProgressIndicator(
                progress = progressPercentage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = PrimaryGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ⭐ Mostrar indicador de carga al eliminar
            if (isDeleting) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = PrimaryGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.SPANISH)
                            "Eliminando..."
                        else
                            "Deleting...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                // Ver Detalles Button
                Button(
                    onClick = { onClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text(
                        if (currentLanguage == AppLanguage.SPANISH)
                            "Ver Gastos"
                        else
                            "View Expenses"
                    )
                }
            }
        }
    }
}