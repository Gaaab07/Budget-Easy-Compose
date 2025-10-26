package com.budgeteasy.domain.usecase.expense

import com.budgeteasy.domain.model.Expense
import com.budgeteasy.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentExpensesUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository
) {
    operator fun invoke(userId: Int, limit: Int = 5): Flow<List<Expense>> {
        return expenseRepository.getRecentExpensesByUser(userId, limit)
    }
}