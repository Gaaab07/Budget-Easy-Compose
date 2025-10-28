package com.budgeteasy.presentation.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.budgeteasy.data.preferences.AppLanguage
import com.budgeteasy.domain.model.Budget
import com.budgeteasy.domain.model.Expense
import com.budgeteasy.domain.model.User
import com.budgeteasy.presentation.ui.navigation.BottomNavigationBar
import com.budgeteasy.presentation.ui.navigation.Screen
import com.budgeteasy.presentation.utils.getCurrentLanguage

@Composable
fun DashboardScreen(
    navController: NavController,
    userId: Int,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user by viewModel.user.collectAsState()
    val recentExpenses by viewModel.recentExpenses.collectAsState()
    val selectedBudget by viewModel.selectedBudget.collectAsState()
    val allBudgets by viewModel.allBudgets.collectAsState()
    val currentLanguage = getCurrentLanguage()

    LaunchedEffect(Unit) {
        viewModel.refreshDashboard()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, userId = userId)
        }
    ) { padding ->
        when (uiState) {
            is DashboardUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is DashboardUiState.Success -> {
                val budgets = (uiState as DashboardUiState.Success).budgets
                DashboardContent(
                    budgets = budgets,
                    selectedBudget = selectedBudget,
                    recentExpenses = recentExpenses,
                    user = user,
                    userId = userId,
                    navController = navController,
                    currentLanguage = currentLanguage,
                    onRefresh = { viewModel.refreshDashboard() },
                    onBudgetSelected = { budget -> viewModel.selectBudget(budget) },
                    padding = padding
                )
            }

            is DashboardUiState.Error -> {
                val message = (uiState as DashboardUiState.Error).message
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.SPANISH)
                                "Error: $message"
                            else
                                "Error: $message",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text(if (currentLanguage == AppLanguage.SPANISH) "Volver" else "Back")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(
    budgets: List<Budget>,
    selectedBudget: Budget?,
    recentExpenses: List<Expense>,
    user: User?,
    userId: Int,
    navController: NavController,
    currentLanguage: AppLanguage,
    onRefresh: () -> Unit,
    onBudgetSelected: (Budget) -> Unit,
    padding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding)
    ) {
        // Header
        item {
            DashboardHeader(
                user = user,
                userId = userId,
                navController = navController,
                currentLanguage = currentLanguage
            )
        }

        // Presupuesto destacado
        if (selectedBudget != null) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                MainBudgetCard(
                    budget = selectedBudget,
                    allBudgets = budgets,
                    currentLanguage = currentLanguage,
                    onBudgetSelected = onBudgetSelected
                )
            }
        }

        // Título: Gastos Recientes
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.SPANISH)
                        "Gastos Recientes"
                    else
                        "Recent Expenses",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (selectedBudget != null) {
                    Text(
                        text = selectedBudget.nombre,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Gastos recientes
        if (recentExpenses.isEmpty()) {
            item {
                Text(
                    text = if (currentLanguage == AppLanguage.SPANISH)
                        "No hay gastos registrados aún"
                    else
                        "No expenses registered yet",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        } else {
            items(recentExpenses) { expense ->
                RecentExpenseItem(
                    name = expense.nombre,
                    category = expense.categoria,
                    amount = expense.monto,
                    onClick = {
                        navController.navigate(
                            Screen.ExpenseDetail.createRoute(expense.id, expense.budgetId)
                        )
                    }
                )
            }
        }

        // Botón agregar gasto
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val budgetToUse = selectedBudget ?: budgets.firstOrNull()
                    if (budgetToUse != null) {
                        navController.navigate(Screen.AddExpense.createRoute(budgetToUse.id))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (currentLanguage == AppLanguage.SPANISH) "Añadir nuevo" else "Add New",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Título: Mis Presupuestos
        item {
            Text(
                text = if (currentLanguage == AppLanguage.SPANISH)
                    "Mis Presupuestos"
                else
                    "My Budgets",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Lista de presupuestos
        items(budgets) { budget ->
            BudgetDashboardCard(
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

        // Botón crear presupuesto
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate(Screen.CreateBudget.createRoute(userId))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (currentLanguage == AppLanguage.SPANISH) "Añadir Presupuesto" else "Add Budget",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DashboardHeader(
    user: User?,
    userId: Int,
    navController: NavController,
    currentLanguage: AppLanguage
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = if (currentLanguage == AppLanguage.SPANISH) "Vacaciones" else "Vacations",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user?.nombre ?: if (currentLanguage == AppLanguage.SPANISH) "Usuario" else "User",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }

        Surface(
            modifier = Modifier
                .size(48.dp)
                .clickable { },
            shape = RoundedCornerShape(50),
            color = Color(0xFF8D6E63)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.nombre?.firstOrNull()?.uppercase() ?: "U",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MainBudgetCard(
    budget: Budget,
    allBudgets: List<Budget>,
    currentLanguage: AppLanguage,
    onBudgetSelected: (Budget) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val montoGastado = budget.montoGastado
    val montoPlaneado = budget.montoPlaneado
    val montoRestante = (montoPlaneado - montoGastado).coerceAtLeast(0.0)

    val progress = if (montoPlaneado > 0) {
        (montoGastado / montoPlaneado).coerceIn(0.0, 1.0).toFloat()
    } else {
        0f
    }

    val porcentaje = (progress * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(enabled = allBudgets.size > 1) { expanded = true },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF66BB6A)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = budget.nombre,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (currentLanguage == AppLanguage.SPANISH)
                    "Presupuesto Total"
                else
                    "Total Budget",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
            Text(
                text = "S/.${String.format("%.2f", montoPlaneado)}",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (currentLanguage == AppLanguage.SPANISH) "Gastado" else "Spent",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "S/.${String.format("%.2f", montoGastado)}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (currentLanguage == AppLanguage.SPANISH)
                            "Disponible"
                        else
                            "Available",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "S/.${String.format("%.2f", montoRestante)}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (currentLanguage == AppLanguage.SPANISH)
                    "$porcentaje% del presupuesto usado"
                else
                    "$porcentaje% of budget used",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            if (allBudgets.size > 1) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.SPANISH)
                            "👆 Toca la tarjeta para cambiar de presupuesto"
                        else
                            "👆 Tap card to change budget",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Text(
                text = if (currentLanguage == AppLanguage.SPANISH)
                    "Selecciona un presupuesto"
                else
                    "Select a budget",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            allBudgets.forEach { budgetOption ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = budgetOption.nombre,
                                    fontWeight = if (budgetOption.id == budget.id) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.SPANISH)
                                        "S/.${String.format("%.2f", budgetOption.montoPlaneado)} • Gastado: S/.${String.format("%.2f", budgetOption.montoGastado)}"
                                    else
                                        "S/.${String.format("%.2f", budgetOption.montoPlaneado)} • Spent: S/.${String.format("%.2f", budgetOption.montoGastado)}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            if (budgetOption.id == budget.id) {
                                Text(
                                    text = "✓",
                                    fontSize = 18.sp,
                                    color = Color(0xFF66BB6A),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    onClick = {
                        onBudgetSelected(budgetOption)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun RecentExpenseItem(
    name: String,
    category: String,
    amount: Double,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = getCategoryEmoji(category),
                        fontSize = 20.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = category,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
        Text(
            text = "-S/.${String.format("%.2f", kotlin.math.abs(amount))}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE53935)
        )
    }
}

@Composable
private fun BudgetDashboardCard(
    budget: Budget,
    currentLanguage: AppLanguage,
    onClick: () -> Unit
) {
    val progress = if (budget.montoPlaneado > 0) {
        (budget.montoGastado / budget.montoPlaneado).coerceIn(0.0, 1.0).toFloat()
    } else {
        0f
    }

    val colors = listOf(
        Color(0xFF66BB6A),
        Color(0xFFFFA726),
        Color(0xFF42A5F5)
    )

    val colorIndex = budget.id % colors.size
    val cardColor = colors[colorIndex]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = budget.nombre,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "S/.${String.format("%.2f", budget.montoPlaneado)}",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (currentLanguage == AppLanguage.SPANISH)
                    "Gastado: S/.${String.format("%.2f", budget.montoGastado)}"
                else
                    "Spent: S/.${String.format("%.2f", budget.montoGastado)}",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (currentLanguage == AppLanguage.SPANISH)
                    "${(progress * 100).toInt()}% usado"
                else
                    "${(progress * 100).toInt()}% used",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        }
    }
}

private fun getCategoryEmoji(category: String): String {
    return when (category) {
        "Restaurantes", "Restaurants" -> "🍽️"
        "Compras", "Shopping" -> "🛍️"
        "Transporte", "Transport" -> "🚗"
        "Entretenimiento", "Entertainment" -> "🎵"
        "Salud", "Health" -> "💊"
        "Educación", "Education" -> "📖"
        "Servicios", "Services" -> "🔨"
        "Hogar", "Home" -> "🏠"
        else -> "💰"
    }
}