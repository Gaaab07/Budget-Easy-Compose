package com.budgeteasy.domain.model

data class Expense(
    val id: Int = 0,
    val budgetId: Int,
    val nombre: String,
    val monto: Double,
    val fecha: Long,
    val nota: String = "",
    val categoria: String = "general",
    val fechaCreacion: Long = System.currentTimeMillis()
)