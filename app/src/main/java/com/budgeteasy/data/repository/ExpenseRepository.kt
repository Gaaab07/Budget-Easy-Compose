package com.budgeteasy.data.repository

import com.budgeteasy.data.local.database.dao.ExpenseDao
import com.budgeteasy.data.local.database.dao.BudgetDao // 👈 NUEVA IMPORTACIÓN
import com.budgeteasy.data.local.database.BudgetDatabase // 👈 NUEVA IMPORTACIÓN
import androidx.room.withTransaction // 👈 NUEVA IMPORTACIÓN
import com.budgeteasy.data.local.database.entity.ExpenseEntity
import com.budgeteasy.domain.model.Expense
import com.budgeteasy.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao, // 👈 AÑADIDO
    private val database: BudgetDatabase // 👈 AÑADIDO (Nombre de tu base de datos)
) : IExpenseRepository {

    override suspend fun getExpenseByIdSingle(expenseId: Int): Expense? {
        // Asumo que tu ExpenseDao ya tiene un método para obtener una entidad por ID de forma síncrona
        return expenseDao.getExpenseById(expenseId)?.toModel()
    }

    // 2. Implementación Transaccional de Modificación (Update)
    override suspend fun updateExpenseAndBudgetBalance(
        updatedExpense: Expense,
        budgetId: Int,
        montoAdjustment: Double // Esta es la diferencia neta (New - Old)
    ): Boolean = try {
        database.withTransaction {
            expenseDao.updateExpense(updatedExpense.toEntity())

            // 🚨 CAMBIO AQUÍ: Llamamos a la función con '+' en el DAO
            budgetDao.adjustMontoGastado(budgetId, montoAdjustment)
        }
        true
    } catch (e: Exception) { false }

    override suspend fun deleteExpenseAndRevertBalance(
        expenseId: Int,
        budgetId: Int,
        montoToRevert: Double
    ): Boolean = try {
        database.withTransaction {
            expenseDao.deleteExpenseById(expenseId)
            budgetDao.adjustMontoGastado(budgetId, -montoToRevert)
        }
        true
    } catch (e: Exception) { false }

    override suspend fun addExpense(expense: Expense): Long {
        val expenseEntity = expense.toEntity()
        return expenseDao.insertExpense(expenseEntity)
    }

    override suspend fun getExpenseById(expenseId: Int): Expense? {
        return expenseDao.getExpenseById(expenseId)?.toModel()
    }

    override fun getExpensesByBudget(budgetId: Int): Flow<List<Expense>> {
        return expenseDao.getExpensesByBudget(budgetId).map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense.toEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense.toEntity())
    }

    override suspend fun getTotalGastadoByBudget(budgetId: Int): Double? {
        return expenseDao.getTotalGastadoByBudget(budgetId)
    }

    override suspend fun getExpensesByDateRange(
        budgetId: Int,
        fechaInicio: Long,
        fechaFin: Long
    ): List<Expense> {
        return expenseDao.getExpensesByDateRange(budgetId, fechaInicio, fechaFin)
            .map { it.toModel() }
    }

    override suspend fun deleteExpensesByBudget(budgetId: Int) {
        expenseDao.deleteExpensesByBudget(budgetId)
    }

    override suspend fun getRecentExpenses(limit: Int): List<Expense> {
        return expenseDao.getRecentExpenses(limit).map { it.toModel() }
    }

    override fun getRecentExpensesByUser(userId: Int, limit: Int): Flow<List<Expense>> {
        return expenseDao.getRecentExpensesByUser(userId, limit).map { entities ->
            entities.map { it.toModel() }
        }
    }

    // 🆕 NUEVO: Gastos recientes de UN presupuesto específico
    override fun getRecentExpensesByBudget(budgetId: Int, limit: Int): Flow<List<Expense>> {
        return expenseDao.getRecentExpensesByBudget(budgetId, limit).map { entities ->
            entities.map { it.toModel() }  // ✅ CORREGIDO: toModel() en vez de toDomain()
        }
    }

    private fun Expense.toEntity(): ExpenseEntity {
        return ExpenseEntity(
            id = this.id,
            budgetId = this.budgetId,
            nombre = this.nombre,
            monto = this.monto,
            fecha = this.fecha,
            nota = this.nota,
            categoria = this.categoria,
            fechaCreacion = this.fechaCreacion
        )
    }

    private fun ExpenseEntity.toModel(): Expense {
        return Expense(
            id = this.id,
            budgetId = this.budgetId,
            nombre = this.nombre,
            monto = this.monto,
            fecha = this.fecha,
            nota = this.nota,
            categoria = this.categoria,
            fechaCreacion = this.fechaCreacion
        )
    }
}