package com.budgeteasy.domain.usecase.expense

// Cambia la importación a la ruta de tu interfaz
import com.budgeteasy.domain.repository.IExpenseRepository
import com.budgeteasy.domain.model.Expense
import javax.inject.Inject

class UpdateExpenseUseCase @Inject constructor(
    private val expenseRepository: IExpenseRepository
) {
    suspend operator fun invoke(updatedExpense: Expense): Boolean {
        // 1. Obtener el gasto original para saber cuánto se cambió el monto.
        val originalExpense = expenseRepository.getExpenseByIdSingle(updatedExpense.id)
            ?: return false

        // 2. Calcular el ajuste neto que debe hacerse al saldo del Budget.
        val oldMonto = originalExpense.monto
        val newMonto = updatedExpense.monto

        // La diferencia: si New > Old, Adjustment es positivo (se resta MÁS del presupuesto).
        // Si New < Old, Adjustment es negativo (se suma al presupuesto).
        val montoAdjustment = newMonto - oldMonto

        // 3. Llamar a la función transaccional del repositorio.
        return expenseRepository.updateExpenseAndBudgetBalance(
            updatedExpense = updatedExpense,
            budgetId = updatedExpense.budgetId,
            montoAdjustment = montoAdjustment
        )
    }
}