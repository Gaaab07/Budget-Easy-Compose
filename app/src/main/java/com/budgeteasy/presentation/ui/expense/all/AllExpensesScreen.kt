package com.budgeteasy.presentation.ui.expense.all

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.budgeteasy.domain.model.Expense
import com.budgeteasy.presentation.theme.PrimaryGreen
import com.budgeteasy.presentation.ui.navigation.BottomNavigationBar
import com.budgeteasy.presentation.ui.navigation.Screen
import com.budgeteasy.presentation.utils.getCurrentLanguage
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllExpensesScreen(
    navController: NavController,
    userId: Int,
    viewModel: AllExpensesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val expenses by viewModel.allExpenses.collectAsState()
    val currentLanguage = getCurrentLanguage()

    LaunchedEffect(Unit) {
        viewModel.loadAllExpenses(userId)
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
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PrimaryGreen,
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.SPANISH)
                            "Mis Gastos"
                        else
                            "My Expenses",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.SPANISH)
                            "Historial completo"
                        else
                            "Full history",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

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
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚠️ ${uiState.errorMessage}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                expenses.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "📊",
                                fontSize = 64.sp
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.SPANISH)
                                    "No hay gastos registrados"
                                else
                                    "No expenses registered",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.SPANISH)
                                    "Agrega gastos desde tus presupuestos"
                                else
                                    "Add expenses from your budgets",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    // ⭐ CARD DE RESUMEN CON BOTÓN DE ESTADÍSTICAS
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (currentLanguage == AppLanguage.SPANISH)
                                            "Total Gastado"
                                        else
                                            "Total Spent",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "S/.${String.format("%.2f", expenses.sumOf { it.monto })}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.SPANISH)
                                            "${expenses.size} gastos"
                                        else
                                            "${expenses.size} expenses",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Text(
                                    text = "💰",
                                    fontSize = 48.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // ⭐ BOTÓN DE ESTADÍSTICAS
                            Button(
                                onClick = {
                                    navController.navigate(Screen.Statistics.createRoute(userId))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryGreen
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "📊",
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (currentLanguage == AppLanguage.SPANISH)
                                        "Ver Estadísticas Detalladas"
                                    else
                                        "View Detailed Statistics",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Lista de gastos
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(expenses) { expense ->
                            ExpenseItemCard(
                                expense = expense,
                                currentLanguage = currentLanguage,
                                onClick = {
                                    navController.navigate(
                                        Screen.ExpenseDetail.createRoute(expense.id, expense.budgetId)
                                    )
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseItemCard(
    expense: Expense,
    currentLanguage: AppLanguage,
    onClick: () -> Unit
) {
    val locale = if (currentLanguage == AppLanguage.SPANISH)
        Locale("es", "ES")
    else
        Locale("en", "US")
    val dateFormat = SimpleDateFormat("dd MMM yyyy", locale)
    val categoryIcon = getCategoryIconForExpense(expense.categoria)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Icono de categoría
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = PrimaryGreen.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categoryIcon,
                        fontSize = 24.sp
                    )
                }

                Column {
                    Text(
                        text = expense.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = dateFormat.format(Date(expense.fecha)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (expense.categoria.isNotEmpty()) {
                        Text(
                            text = translateCategory(expense.categoria, currentLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryGreen
                        )
                    }
                }
            }

            Text(
                text = "-S/.${String.format("%.2f", expense.monto)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

// Traducir categorías
fun translateCategory(category: String, language: AppLanguage): String {
    if (language == AppLanguage.ENGLISH) {
        return when (category) {
            "Restaurantes" -> "Restaurants"
            "Compras" -> "Shopping"
            "Transporte" -> "Transport"
            "Entretenimiento" -> "Entertainment"
            "Salud" -> "Health"
            "Educación" -> "Education"
            "Servicios" -> "Services"
            "Hogar" -> "Home"
            "Otros" -> "Other"
            else -> category
        }
    }
    return category
}

// Función para obtener el icono según la categoría
fun getCategoryIconForExpense(category: String): String {
    return when (category) {
        "Restaurantes", "Restaurants" -> "🍽️"
        "Compras", "Shopping" -> "🛍️"
        "Transporte", "Transport" -> "🚗"
        "Entretenimiento", "Entertainment" -> "🎵"
        "Salud", "Health" -> "💊"
        "Educación", "Education" -> "📖"
        "Servicios", "Services" -> "🔨"
        "Hogar", "Home" -> "🏠"
        "Otros", "Other" -> "💰"
        else -> "💰"
    }
}