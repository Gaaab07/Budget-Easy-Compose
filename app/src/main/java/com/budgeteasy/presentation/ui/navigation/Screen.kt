package com.budgeteasy.presentation.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object BudgetList : Screen("budget_list/{userId}") {
        fun createRoute(userId: Int) = "budget_list/$userId"
    }
    object CreateBudget : Screen("create_budget/{userId}") {
        fun createRoute(userId: Int) = "create_budget/$userId"
    }
    object ExpenseList : Screen("expense_list/{budgetId}/{budgetName}/{budgetMonto}") {
        fun createRoute(budgetId: Int, budgetName: String, budgetMonto: Double) =
            "expense_list/$budgetId/$budgetName/$budgetMonto"
    }
    object AddExpense : Screen("add_expense/{budgetId}") {
        fun createRoute(budgetId: Int) = "add_expense/$budgetId"
    }
    object Dashboard : Screen("dashboard/{userId}") {
        fun createRoute(userId: Int) = "dashboard/$userId"
    }

    object ExpenseDetail : Screen("expense_detail/{expenseId}/{budgetId}") {
        fun createRoute(expenseId: Int, budgetId: Int) = "expense_detail/$expenseId/$budgetId"
    }


}