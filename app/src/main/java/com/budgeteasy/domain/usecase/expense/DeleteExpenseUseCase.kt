package com.budgeteasy.domain.usecase.expense


import com.budgeteasy.domain.repository.IExpenseRepository
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository
) {

    suspend operator fun invoke(expenseId: Int): Boolean {

        val expenseToDelete = expenseRepository.getExpenseByIdSingle(expenseId)
            ?: return false

        val montoToRevert = expenseToDelete.monto
        val budgetId = expenseToDelete.budgetId



        return expenseRepository.deleteExpenseAndRevertBalance(
            expenseId = expenseId,
            budgetId = budgetId,
            montoToRevert = montoToRevert
        )
    }
}