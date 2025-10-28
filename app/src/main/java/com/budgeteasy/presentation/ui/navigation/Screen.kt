package com.budgeteasy.presentation.ui.navigation

sealed class Screen(val route: String) {
    // Auth
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")

    // Dashboard (Bottom Nav)
    object Dashboard : Screen("dashboard/{userId}") {
        fun createRoute(userId: Int) = "dashboard/$userId"
    }

    // Budget List (Bottom Nav)
    object BudgetList : Screen("budget_list/{userId}") {
        fun createRoute(userId: Int) = "budget_list/$userId"
    }

    object CreateBudget : Screen("create_budget/{userId}") {
        fun createRoute(userId: Int) = "create_budget/$userId"
    }

    // Expense List (gastos de UN presupuesto específico)
    object ExpenseList : Screen("expense_list/{budgetId}/{budgetName}/{budgetMonto}") {
        fun createRoute(budgetId: Int, budgetName: String, budgetMonto: Double) =
            "expense_list/$budgetId/$budgetName/$budgetMonto"
    }

    object AddExpense : Screen("add_expense/{budgetId}") {
        fun createRoute(budgetId: Int) = "add_expense/$budgetId"
    }

    object ExpenseDetail : Screen("expense_detail/{expenseId}/{budgetId}") {
        fun createRoute(expenseId: Int, budgetId: Int) = "expense_detail/$expenseId/$budgetId"
    }

    // 🆕 NUEVAS RUTAS PARA BOTTOM NAV

    // All Expenses (Bottom Nav - ver TODOS los gastos del usuario)
    object AllExpenses : Screen("all_expenses/{userId}") {
        fun createRoute(userId: Int) = "all_expenses/$userId"
    }

    // Profile (Bottom Nav)
    object Profile : Screen("profile/{userId}") {
        fun createRoute(userId: Int) = "profile/$userId"
    }
    object Settings : Screen("settings") {
        // Sin parámetros porque no necesita userId
    }
}