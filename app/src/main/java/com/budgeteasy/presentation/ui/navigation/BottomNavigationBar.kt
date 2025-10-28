package com.budgeteasy.presentation.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Sealed class para las rutas del Bottom Nav
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "dashboard",
        title = "Inicio",
        icon = Icons.Default.Home
    )
    object Budgets : BottomNavItem(
        route = "budget_list",
        title = "Presupuestos",
        icon = Icons.Default.AccountBalanceWallet
    )
    object Expenses : BottomNavItem(
        route = "all_expenses",
        title = "Gastos",
        icon = Icons.Default.Receipt
    )
    object Profile : BottomNavItem(
        route = "profile",
        title = "Perfil",
        icon = Icons.Default.Person
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
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
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
