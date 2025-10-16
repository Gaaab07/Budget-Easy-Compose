package com.budgeteasy.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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
}