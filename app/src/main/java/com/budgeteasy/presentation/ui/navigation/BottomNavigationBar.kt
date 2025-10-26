package com.budgeteasy.presentation.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// Sealed class para las rutas del Bottom Nav
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val emoji: String
) {
    object Home : BottomNavItem(
        route = "dashboard",
        title = "Inicio",
        emoji = "🏠"  // Hogar
    )

    object Budgets : BottomNavItem(
        route = "budget_list",
        title = "Presupuestos",
        emoji = "💰"  // Dinero (consistente con getCategoryIconForExpense)
    )

    object Expenses : BottomNavItem(
        route = "all_expenses",
        title = "Gastos",
        emoji = "📊"  // Gráfico
    )

    object Profile : BottomNavItem(
        route = "profile",
        title = "Perfil",
        emoji = "👤"  // Persona
    )
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    userId: Int
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Budgets,
        BottomNavItem.Expenses,
        BottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute?.startsWith(item.route) == true

            NavigationBarItem(
                icon = {
                    Text(
                        text = item.emoji,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal
                    )
                },
                label = { Text(item.title) },
                selected = selected,
                onClick = {
                    if (!selected) {
                        val route = when (item) {
                            is BottomNavItem.Home -> Screen.Dashboard.createRoute(userId)
                            is BottomNavItem.Budgets -> Screen.BudgetList.createRoute(userId)
                            is BottomNavItem.Expenses -> Screen.AllExpenses.createRoute(userId)
                            is BottomNavItem.Profile -> Screen.Profile.createRoute(userId)
                        }

                        navController.navigate(route) {
                            popUpTo(Screen.Dashboard.createRoute(userId)) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}