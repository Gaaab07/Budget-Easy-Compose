package com.budgeteasy.presentation.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgeteasy.presentation.theme.PrimaryGreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        delay(2000) // Mostrar splash por 2 segundos
        onSplashComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo/Título
        Text(
            text = "💰",
            fontSize = 80.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = "BudgetEasy",
            style = MaterialTheme.typography.displayLarge,
            color = PrimaryGreen,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Gestiona tus presupuestos fácilmente",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        // Loading indicator
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            color = PrimaryGreen
        )
    }
}