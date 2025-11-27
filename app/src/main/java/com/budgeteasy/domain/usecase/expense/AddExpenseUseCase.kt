package com.budgeteasy.domain.usecase.expense

import com.budgeteasy.domain.model.Expense
import com.budgeteasy.domain.repository.IBudgetRepository
import com.budgeteasy.domain.repository.IExpenseRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository,
    private val budgetRepository: IBudgetRepository
) {
    suspend operator fun invoke(expense: Expense): Long {

        val expenseId = expenseRepository.addExpense(expense)


        val budget = budgetRepository.getBudgetById(expense.budgetId)


        if (budget != null) {
            val nuevoMontoGastado = budget.montoGastado + expense.monto
            budgetRepository.updateMontoGastado(expense.budgetId, nuevoMontoGastado)
        }

        return expenseId
    }
}