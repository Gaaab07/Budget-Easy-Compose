package com.budgeteasy.domain.usecase.expense

import com.budgeteasy.domain.model.Expense
import com.budgeteasy.domain.repository.IExpenseRepository
import javax.inject.Inject

class UpdateExpenseUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository
) {
    suspend operator fun invoke(
        updatedExpense: Expense,
        budgetId: Int,
        montoAdjustment: Double
    ): Boolean {
        // ...
        return expenseRepository.updateExpenseAndBudgetBalance(
            updatedExpense = updatedExpense,
            budgetId = budgetId,
            montoAdjustment = montoAdjustment
        )
    }
}