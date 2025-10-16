package com.budgeteasy.domain.usecase.budget

import com.budgeteasy.domain.model.Budget
import com.budgeteasy.domain.repository.IBudgetRepository
import javax.inject.Inject

class CreateBudgetUseCase @Inject constructor(
    private val budgetRepository: IBudgetRepository
) {
    suspend operator fun invoke(budget: Budget): Long {
        return budgetRepository.createBudget(budget)
    }
}