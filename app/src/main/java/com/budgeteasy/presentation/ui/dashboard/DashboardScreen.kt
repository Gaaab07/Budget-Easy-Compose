package com.budgeteasy.presentation.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.budgeteasy.domain.model.Budget
import com.budgeteasy.domain.model.User
import com.budgeteasy.presentation.ui.navigation.Screen

@Composable
fun DashboardScreen(
    navController: NavController,
    userId: Int,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user by viewModel.user.collectAsState()

    when (uiState) {
        is DashboardUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
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
                user = user,
                userId = userId,
                navController = navController,
                onRefresh = { viewModel.refreshDashboard() }
            )
        }

        is DashboardUiState.Error -> {
            val message = (uiState as DashboardUiState.Error).message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Error: $message",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Volver")
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(
    budgets: List<Budget>,
    user: User?,
    userId: Int,
    navController: NavController,
    onRefresh: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 56.dp)
    ) {
        // Header con usuario
        item {
            DashboardHeader(user = user, userId = userId, navController = navController)
        }

        // Presupuesto destacado (el de mayor monto)
        if (budgets.isNotEmpty()) {
            val mainBudget = budgets.maxByOrNull { it.montoPlaneado }
            if (mainBudget != null) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    MainBudgetCard(budget = mainBudget)
                }
            }
        }

        // Título: Gastos Recientes
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Gastos Recientes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Gastos recientes (simulados por ahora)
        val recentExpenses = listOf(
            Triple("Cena en El Sabor", "Restaurantes", -45.00),
            Triple("Ropa nueva", "Compras", -120.00),
            Triple("Viaje en taxi", "Transporte", -15.00),
            Triple("Concierto de música", "Entretenimiento", -30.00),
            Triple("Concierto de música", "Entretenimiento", -60.00)
        )

        items(recentExpenses) { (name, category, amount) ->
            RecentExpenseItem(name = name, category = category, amount = amount)
        }

        // Botón para agregar gasto
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (budgets.isNotEmpty()) {
                        val budget = budgets.first()
                        navController.navigate(Screen.AddExpense.createRoute(budget.id))
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
                    contentDescription = "Agregar",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Añadir nuevo", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Título: Mis Presupuestos
        item {
            Text(
                text = "Mis Presupuestos",
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

        // Botón para crear presupuesto
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
                    contentDescription = "Agregar",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Añadir Presupuesto", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DashboardHeader(
    user: User?,
    userId: Int,
    navController: NavController
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
                text = "Vacaciones",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user?.nombre ?: "Usuario",
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
private fun MainBudgetCard(budget: Budget) {
    val progress = if (budget.montoPlaneado > 0) {
        (budget.montoGastado / budget.montoPlaneado).coerceIn(0.0, 1.0).toFloat()
    } else {
        0f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF66BB6A)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = budget.nombre,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Estás aquí",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "S/.${String.format("%.2f", budget.montoPlaneado)}",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Consumiste",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "S/.${String.format("%.2f", budget.montoGastado)}",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
                Text(
                    text = "S/.${String.format("%.2f", budget.montoPlaneado)}",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun RecentExpenseItem(
    name: String,
    category: String,
    amount: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
                        text = getCategoryIcon(category),
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
            text = "S/.${String.format("%.2f", kotlin.math.abs(amount))}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE53935)
        )
    }
}

@Composable
private fun BudgetDashboardCard(
    budget: Budget,
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
                text = "Consumiste",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
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
                text = "S/.${String.format("%.2f", budget.montoGastado)}",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        }
    }
}

private fun getCategoryIcon(category: String): String {
    return when (category) {
        "Restaurantes" -> "🍽️"
        "Compras" -> "🛍️"
        "Transporte" -> "🚗"
        "Entretenimiento" -> "🎵"
        else -> "💰"
    }
}