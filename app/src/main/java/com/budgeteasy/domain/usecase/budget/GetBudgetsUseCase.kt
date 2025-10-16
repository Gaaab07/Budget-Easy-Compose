package com.budgeteasy.domain.usecase.budget

import com.budgeteasy.domain.model.Budget
import com.budgeteasy.domain.repository.IBudgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBudgetsUseCase @Inject constructor(
    private val budgetRepository: IBudgetRepository
) {
    operator fun invoke(userId: Int): Flow<List<Budget>> {
        return budgetRepository.getBudgetsByUser(userId)
    }
}