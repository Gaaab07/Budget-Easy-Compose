package com.budgeteasy.domain.usecase.expense

import com.budgeteasy.domain.model.Expense
import com.budgeteasy.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExpensesUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository
) {
    operator fun invoke(budgetId: Int): Flow<List<Expense>> {
        return expenseRepository.getExpensesByBudget(budgetId)
    }
}
