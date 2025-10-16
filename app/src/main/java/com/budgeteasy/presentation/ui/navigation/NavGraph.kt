package com.budgeteasy.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.budgeteasy.presentation.ui.auth.login.LoginScreen
import com.budgeteasy.presentation.ui.auth.register.RegisterScreen
import com.budgeteasy.presentation.ui.budget.create.CreateBudgetScreen
import com.budgeteasy.presentation.ui.budget.list.BudgetListScreen
import com.budgeteasy.presentation.ui.expense.add.AddExpenseScreen
import com.budgeteasy.presentation.ui.expense.list.ExpenseListScreen
import com.budgeteasy.presentation.ui.splash.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    // Por ahora, usamos userId hardcodeado (1)
                    navController.navigate(Screen.BudgetList.createRoute(1)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Register Screen
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Budget List Screen
        composable(
            Screen.BudgetList.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            BudgetListScreen(
                userId = userId,
                onBudgetClick = { budgetId ->
                    // Para este ejemplo, pasamos valores dummy para budgetName y budgetMonto
                    navController.navigate(Screen.ExpenseList.createRoute(budgetId, "Budget", 1000.0))
                },
                onCreateBudgetClick = {
                    navController.navigate(Screen.CreateBudget.createRoute(userId))
                },
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.BudgetList.route) { inclusive = true }
                    }
                }
            )
        }

        // Create Budget Screen
        composable(
            Screen.CreateBudget.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            CreateBudgetScreen(
                userId = userId,
                onCreateSuccess = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Expense List Screen
        composable(
            Screen.ExpenseList.route,
            arguments = listOf(
                navArgument("budgetId") { type = NavType.IntType },
                navArgument("budgetName") { type = NavType.StringType },
                navArgument("budgetMonto") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getInt("budgetId") ?: 0
            val budgetName = backStackEntry.arguments?.getString("budgetName") ?: "Budget"
            val budgetMonto = backStackEntry.arguments?.getFloat("budgetMonto")?.toDouble() ?: 0.0

            ExpenseListScreen(
                budgetId = budgetId,
                budgetName = budgetName,
                budgetMonto = budgetMonto,
                onAddExpenseClick = {
                    navController.navigate(Screen.AddExpense.createRoute(budgetId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Add Expense Screen
        composable(
            Screen.AddExpense.route,
            arguments = listOf(navArgument("budgetId") { type = NavType.IntType })
        ) { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getInt("budgetId") ?: 0
            AddExpenseScreen(
                budgetId = budgetId,
                onAddSuccess = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}