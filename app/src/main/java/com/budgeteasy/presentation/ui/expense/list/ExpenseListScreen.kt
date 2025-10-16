package com.budgeteasy.presentation.ui.expense.list

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.budgeteasy.domain.model.Expense
import com.budgeteasy.presentation.theme.PrimaryGreen
import com.budgeteasy.presentation.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpenseListScreen(
    budgetId: Int,
    budgetName: String,
    budgetMonto: Double,
    viewModel: ExpenseListViewModel = hiltViewModel(),
    onAddExpenseClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadExpenses(budgetId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = budgetName,
            style = MaterialTheme.typography.displaySmall,
            color = PrimaryGreen,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryGreen)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Resumen",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.background
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Presupuesto",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.background
                        )
                        Text(
                            text = "\$${budgetMonto}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.background
                        )
                    }

                    Column {
                        Text(
                            text = "Gastado",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.background
                        )
                        Text(
                            text = "\$${uiState.totalGastado}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.background
                        )
                    }

                    Column {
                        Text(
                            text = "Disponible",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.background
                        )
                        val disponible = budgetMonto - uiState.totalGastado
                        Text(
                            text = "\$${disponible}",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (disponible >= 0) SuccessGreen else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Add Expense Button
        Button(
            onClick = { onAddExpenseClick() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Text("+ Agregar Gasto")
        }

        // Content
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            uiState.expenses.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay gastos. ¡Agrega uno!",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.expenses) { expense ->
                        ExpenseItem(expense = expense)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back Button
        Button(
            onClick = { onNavigateBack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Volver")
        }
    }
}

@Composable
fun ExpenseItem(
    expense: Expense
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
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
                text = expense.nombre,
                style = MaterialTheme.typography.titleLarge,
                color = PrimaryGreen
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Monto y Fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "\$${expense.monto}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )

                Text(
                    text = dateFormat.format(Date(expense.fecha)),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Nota si existe
            if (expense.nota.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nota: ${expense.nota}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}