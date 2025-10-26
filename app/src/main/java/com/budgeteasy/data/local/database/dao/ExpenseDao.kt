package com.budgeteasy.data.local.database.dao

import androidx.room.*
import com.budgeteasy.data.local.database.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    suspend fun getExpenseById(expenseId: Int): ExpenseEntity?

    @Query("SELECT * FROM expenses WHERE budgetId = :budgetId ORDER BY fecha DESC")
    fun getExpensesByBudget(budgetId: Int): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE budgetId = :budgetId ORDER BY fecha DESC")
    suspend fun getExpensesByBudgetSuspend(budgetId: Int): List<ExpenseEntity>

    @Query("SELECT SUM(monto) FROM expenses WHERE budgetId = :budgetId")
    suspend fun getTotalGastadoByBudget(budgetId: Int): Double?

    @Query("SELECT * FROM expenses WHERE budgetId = :budgetId AND fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha DESC")
    suspend fun getExpensesByDateRange(budgetId: Int, fechaInicio: Long, fechaFin: Long): List<ExpenseEntity>

    @Query("DELETE FROM expenses WHERE budgetId = :budgetId")
    suspend fun deleteExpensesByBudget(budgetId: Int)

    @Query("SELECT * FROM expenses ORDER BY fecha DESC LIMIT :limit")
    suspend fun getRecentExpenses(limit: Int = 10): List<ExpenseEntity>

    @Query("""
    SELECT e.* FROM expenses e
    JOIN budgets b ON e.budgetId = b.id
    WHERE b.userId = :userId
    ORDER BY e.fecha DESC
    LIMIT :limit
    """)
    fun getRecentExpensesByUser(userId: Int, limit: Int = 10): Flow<List<ExpenseEntity>>

    // 🆕 NUEVO: Query para traer gastos recientes de UN presupuesto específico
    @Query("""
        SELECT * FROM expenses
        WHERE budgetId = :budgetId
        ORDER BY fecha DESC
        LIMIT :limit
    """)
    fun getRecentExpensesByBudget(budgetId: Int, limit: Int = 5): Flow<List<ExpenseEntity>>
}
