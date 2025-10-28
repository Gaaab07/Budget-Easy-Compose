package com.budgeteasy.presentation.ui.budget.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                uiState.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.errorMessage ?: if (currentLanguage == AppLanguage.SPANISH)
                                "Error desconocido"
                            else
                                "Unknown error",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                uiState.budgets.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.SPANISH)
                                "No tienes presupuestos. ¡Crea uno!"
                            else
                                "You have no budgets. Create one!",
                            style = MaterialTheme.typography.bodyLarge
                        )
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
    onClick: () -> Unit = {}
) {
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
            // Nombre
            Text(
                text = budget.nombre,
                style = MaterialTheme.typography.titleLarge,
                color = PrimaryGreen
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Periodo
            Text(
                text = if (currentLanguage == AppLanguage.SPANISH)
                    "Período: ${budget.periodo}"
                else
                    "Period: ${budget.periodo}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant // 🔥 Color adaptable
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
                    color = MaterialTheme.colorScheme.onSurface // 🔥 Color adaptable
                )
                Text(
                    text = if (currentLanguage == AppLanguage.SPANISH)
                        "Presupuesto: S/.${String.format("%.2f", budget.montoPlaneado)}"
                    else
                        "Budget: S/.${String.format("%.2f", budget.montoPlaneado)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface // 🔥 Color adaptable
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