package com.budgeteasy.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.budgeteasy.presentation.ui.splash.SplashScreen
import com.budgeteasy.presentation.ui.auth.login.LoginScreen
import com.budgeteasy.presentation.ui.auth.register.RegisterScreen
import com.budgeteasy.presentation.ui.dashboard.DashboardScreen
import com.budgeteasy.presentation.ui.budget.list.BudgetListScreen
import com.budgeteasy.presentation.ui.budget.create.CreateBudgetScreen
import com.budgeteasy.presentation.ui.expense.add.AddExpenseScreen
import com.budgeteasy.presentation.ui.expense.list.ExpenseListScreen
import com.budgeteasy.presentation.ui.expense.detail.ExpenseDetailScreen
import com.budgeteasy.presentation.ui.expense.all.AllExpensesScreen
import com.budgeteasy.presentation.ui.profile.ProfileScreen
import com.budgeteasy.presentation.ui.settings.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // SPLASH
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        // LOGIN
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        // REGISTER
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        // DASHBOARD - Pantalla principal con BottomNav
        composable(
            route = Screen.Dashboard.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            DashboardScreen(
                navController = navController,
                userId = userId
            )
        }

        // BUDGET LIST - Lista de presupuestos con BottomNav
        composable(
            route = Screen.BudgetList.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            BudgetListScreen(
                navController = navController,
                userId = userId
            )
        }

        // CREATE BUDGET
        composable(
            route = Screen.CreateBudget.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            CreateBudgetScreen(
                navController = navController,
                userId = userId
            )
        }

        // EXPENSE LIST (gastos de un presupuesto específico)
        composable(
            route = Screen.ExpenseList.route,
            arguments = listOf(
                navArgument("budgetId") { type = NavType.IntType },
                navArgument("budgetName") { type = NavType.StringType },
                navArgument("budgetMonto") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getInt("budgetId") ?: 0
            val budgetName = backStackEntry.arguments?.getString("budgetName") ?: ""
            val budgetMonto = backStackEntry.arguments?.getFloat("budgetMonto")?.toDouble() ?: 0.0
            ExpenseListScreen(
                navController = navController,
                budgetId = budgetId,
                budgetName = budgetName,
                budgetMonto = budgetMonto
            )
        }

        // ADD EXPENSE
        composable(
            route = Screen.AddExpense.route,
            arguments = listOf(
                navArgument("budgetId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getInt("budgetId") ?: 0
            AddExpenseScreen(
                navController = navController,
                budgetId = budgetId
            )
        }

        // EXPENSE DETAIL
        composable(
            route = Screen.ExpenseDetail.route,
            arguments = listOf(
                navArgument("expenseId") { type = NavType.IntType },
                navArgument("budgetId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getInt("expenseId") ?: 0
            val budgetId = backStackEntry.arguments?.getInt("budgetId") ?: 0
            ExpenseDetailScreen(
                navController = navController,
                expenseId = expenseId,
                budgetId = budgetId
            )
        }

        // ALL EXPENSES - Ver todos los gastos del usuario (BottomNav)
        composable(
            route = Screen.AllExpenses.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            AllExpensesScreen(
                navController = navController,
                userId = userId
            )
        }

        // PROFILE - Perfil del usuario (BottomNav)
        composable(
            route = Screen.Profile.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            ProfileScreen(
                navController = navController,
                userId = userId
            )
        }
        // Agregar en NavGraph.kt después de Profile:

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }


    }
}