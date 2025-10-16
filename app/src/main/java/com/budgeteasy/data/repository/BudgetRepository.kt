package com.budgeteasy.data.repository

import com.budgeteasy.data.local.database.dao.BudgetDao
import com.budgeteasy.data.local.database.entity.BudgetEntity
import com.budgeteasy.domain.model.Budget
import com.budgeteasy.domain.repository.IBudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao
) : IBudgetRepository {

    override suspend fun createBudget(budget: Budget): Long {
        val budgetEntity = budget.toEntity()
        return budgetDao.insertBudget(budgetEntity)
    }

    override suspend fun getBudgetById(budgetId: Int): Budget? {
        return budgetDao.getBudgetById(budgetId)?.toModel()
    }

    override fun getActiveBudgetsByUser(userId: Int): Flow<List<Budget>> {
        return budgetDao.getActiveBudgetsByUser(userId).map { entities ->
            entities.map { it.toModel() }
        }
    }

    override fun getBudgetsByUser(userId: Int): Flow<List<Budget>> {
        return budgetDao.getBudgetsByUser(userId).map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget.toEntity())
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget.toEntity())
    }

    override suspend fun updateMontoGastado(budgetId: Int, nuevoMonto: Double) {
        budgetDao.updateMontoGastado(budgetId, nuevoMonto)
    }

    override suspend fun getTotalGastadoByUser(userId: Int): Double? {
        return budgetDao.getTotalGastadoByUser(userId)
    }

    private fun Budget.toEntity(): BudgetEntity {
        return BudgetEntity(
            id = this.id,
            userId = this.userId,
            nombre = this.nombre,
            montoPlaneado = this.montoPlaneado,
            montoGastado = this.montoGastado,
            periodo = this.periodo,
            fechaInicio = this.fechaInicio,
            fechaFin = this.fechaFin,
            descripcion = this.descripcion,
            activo = this.activo,
            fechaCreacion = this.fechaCreacion
        )
    }

    private fun BudgetEntity.toModel(): Budget {
        return Budget(
            id = this.id,
            userId = this.userId,
            nombre = this.nombre,
            montoPlaneado = this.montoPlaneado,
            montoGastado = this.montoGastado,
            periodo = this.periodo,
            fechaInicio = this.fechaInicio,
            fechaFin = this.fechaFin,
            descripcion = this.descripcion,
            activo = this.activo,
            fechaCreacion = this.fechaCreacion
        )
    }
}