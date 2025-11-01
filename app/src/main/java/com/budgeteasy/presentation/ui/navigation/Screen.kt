package com.budgeteasy.presentation.ui.navigation

sealed class Screen(val route: String) {
    // Auth (Las rutas sin argumentos DEBEN ser declaradas como data object)
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password") // <-- ¡Ruta Añadida!

    // Dashboard (Bottom Nav)
    data object Dashboard : Screen("dashboard/{userId}") {
        fun createRoute(userId: Int) = "dashboard/$userId"
    }

    // Budget List (Bottom Nav)
    data object BudgetList : Screen("budget_list/{userId}") {
        fun createRoute(userId: Int) = "budget_list/$userId"
    }

    data object CreateBudget : Screen("create_budget/{userId}") {
        fun createRoute(userId: Int) = "create_budget/$userId"
    }

    // Expense List (gastos de UN presupuesto específico)
    data object ExpenseList : Screen("expense_list/{budgetId}/{budgetName}/{budgetMonto}") {
        fun createRoute(budgetId: Int, budgetName: String, budgetMonto: Double) =
            "expense_list/$budgetId/$budgetName/$budgetMonto"
    }

    data object AddExpense : Screen("add_expense/{budgetId}") {
        fun createRoute(budgetId: Int) = "add_expense/$budgetId"
    }

    data object ExpenseDetail : Screen("expense_detail/{expenseId}/{budgetId}") {
        fun createRoute(expenseId: Int, budgetId: Int) = "expense_detail/$expenseId/$budgetId"
    }

    // 🆕 NUEVAS RUTAS PARA BOTTOM NAV

    // All Expenses (Bottom Nav - ver TODOS los gastos del usuario)
    data object AllExpenses : Screen("all_expenses/{userId}") {
        fun createRoute(userId: Int) = "all_expenses/$userId"
    }

    // Profile (Bottom Nav)
    data object Profile : Screen("profile/{userId}") {
        fun createRoute(userId: Int) = "profile/$userId"
    }
    data object Settings : Screen("settings") {
        // Sin parámetros porque no necesita userId
    }
}
