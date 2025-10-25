package com.budgeteasy.presentation.ui.budget.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.budgeteasy.presentation.theme.PrimaryGreen

@Composable
fun CreateBudgetScreen(
    navController: NavController,
    userId: Int,
    viewModel: CreateBudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var expandedPeriodo by remember { mutableStateOf(false) }
    val periodos = listOf("1 mes", "3 meses", "6 meses", "1 año")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Título
        Text(
            text = "Crear Presupuesto",
            style = MaterialTheme.typography.displaySmall,
            color = PrimaryGreen,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Nombre TextField
        TextField(
            value = uiState.nombre,
            onValueChange = { viewModel.onNombreChanged(it) },
            label = { Text("Nombre del Presupuesto") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        // Monto Planeado TextField
        TextField(
            value = uiState.montoPlaneado,
            onValueChange = { viewModel.onMontoPlaneadoChanged(it) },
            label = { Text("Monto a Gastar") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        // Periodo Dropdown
        Button(
            onClick = { expandedPeriodo = !expandedPeriodo },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Text("Período: ${uiState.periodo}")
        }

        DropdownMenu(
            expanded = expandedPeriodo,
            onDismissRequest = { expandedPeriodo = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            periodos.forEach { periodo ->
                DropdownMenuItem(
                    text = { Text(periodo) },
                    onClick = {
                        viewModel.onPeriodoChanged(periodo)
                        expandedPeriodo = false
                    }
                )
            }
        }

        // Descripción TextField
        TextField(
            value = uiState.descripcion,
            onValueChange = { viewModel.onDescripcionChanged(it) },
            label = { Text("Descripción (opcional)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            singleLine = true
        )

        // Error message
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Create Button
        Button(
            onClick = { viewModel.createBudget(userId) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(24.dp),
                    color = MaterialTheme.colorScheme.background
                )
            } else {
                Text("Crear Presupuesto")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back Button
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Volver")
        }
    }

    // Handle create success - Navega de vuelta al Dashboard
    if (uiState.isCreateSuccessful) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
    }
}