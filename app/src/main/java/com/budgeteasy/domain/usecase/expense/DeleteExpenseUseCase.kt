package com.budgeteasy.domain.usecase.expense

// Cambia la importación a la ruta de tu interfaz
import com.budgeteasy.domain.repository.IExpenseRepository
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository
) {
    /**
     * Elimina el gasto y revierte el monto al saldo del presupuesto (SUMAR).
     * @param expenseId ID del gasto a eliminar.
     * @return true si la operación transaccional fue exitosa.
     */
    suspend operator fun invoke(expenseId: Int): Boolean {
        // 1. Obtener el gasto original para obtener el monto y BudgetId
        val expenseToDelete = expenseRepository.getExpenseByIdSingle(expenseId)
            ?: return false // No podemos eliminar ni revertir si no existe

        val montoToRevert = expenseToDelete.monto
        val budgetId = expenseToDelete.budgetId

        // 2. Llamar a la función transaccional del repositorio.
        // (Esta función debe SUMAR el montoToRevert al saldo del Budget)
        return expenseRepository.deleteExpenseAndRevertBalance(
            expenseId = expenseId,
            budgetId = budgetId,
            montoToRevert = montoToRevert
        )
    }
}