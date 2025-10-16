package com.budgeteasy.domain.usecase.expense

import com.budgeteasy.domain.model.Expense
import com.budgeteasy.domain.repository.IExpenseRepository
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository
) {
    suspend operator fun invoke(expense: Expense) {
        expenseRepository.deleteExpense(expense)
    }
}