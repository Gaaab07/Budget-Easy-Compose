package com.budgeteasy.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.budgeteasy.data.local.database.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    // Archivo: com/budgeteasy/data/local/database/dao/BudgetDao.kt

    // 🆕 FUNCIÓN CLAVE: Ahora actualiza el MONTO GASTADO
    @Query("UPDATE budgets SET montoGastado = :nuevoMonto WHERE id = :budgetId")
    suspend fun updateMontoGastado(budgetId: Int, nuevoMonto: Double)
    // 🆕 FUNCIÓN CLAVE (Renombrada): Utilizada para sumar/restar en transacciones
    @Query("UPDATE budgets SET montoGastado = montoGastado + :adjustment WHERE id = :budgetId")
    suspend fun adjustMontoGastado(budgetId: Int, adjustment: Double) // <-- Renombrado a adjustMontoGastado

    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteExpenseById(expenseId: Int)
    @Insert
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE id = :budgetId")
    suspend fun getBudgetById(budgetId: Int): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE userId = :userId AND activo = 1")
    fun getActiveBudgetsByUser(userId: Int): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE userId = :userId")
    fun getBudgetsByUser(userId: Int): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE userId = :userId ORDER BY fechaCreacion DESC")
    suspend fun getBudgetsByUserSuspend(userId: Int): List<BudgetEntity>

    @Query("SELECT SUM(montoGastado) FROM budgets WHERE userId = :userId")
    suspend fun getTotalGastadoByUser(userId: Int): Double?
}
