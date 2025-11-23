package com.budgeteasy.domain.usecase.budget

import com.budgeteasy.domain.model.Budget
import com.budgeteasy.domain.repository.IBudgetRepository
import javax.inject.Inject

class GetBudgetByIdUseCase @Inject constructor(
    private val budgetRepository: IBudgetRepository
) {
    suspend operator fun invoke(budgetId: Int): Budget? {
        return budgetRepository.getBudgetById(budgetId)
    }
}