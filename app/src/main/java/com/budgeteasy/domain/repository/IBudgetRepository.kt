package com.budgeteasy.domain.repository

import com.budgeteasy.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface IBudgetRepository {
    suspend fun createBudget(budget: Budget): Long
    suspend fun getBudgetById(budgetId: Int): Budget?
    fun getActiveBudgetsByUser(userId: Int): Flow<List<Budget>>
    fun getBudgetsByUser(userId: Int): Flow<List<Budget>>
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
    suspend fun updateMontoGastado(budgetId: Int, nuevoMonto: Double)
    suspend fun getTotalGastadoByUser(userId: Int): Double?
}