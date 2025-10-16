package com.budgeteasy.domain.repository

import com.budgeteasy.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface IExpenseRepository {
    suspend fun addExpense(expense: Expense): Long
    suspend fun getExpenseById(expenseId: Int): Expense?
    fun getExpensesByBudget(budgetId: Int): Flow<List<Expense>>
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    suspend fun getTotalGastadoByBudget(budgetId: Int): Double?
    suspend fun getExpensesByDateRange(budgetId: Int, fechaInicio: Long, fechaFin: Long): List<Expense>
    suspend fun deleteExpensesByBudget(budgetId: Int)
    suspend fun getRecentExpenses(limit: Int = 10): List<Expense>
}