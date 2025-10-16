package com.budgeteasy.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = BudgetEntity::class,
            parentColumns = ["id"],
            childColumns = ["budgetId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val budgetId: Int,
    val nombre: String,
    val monto: Double,
    val fecha: Long,
    val nota: String = "",
    val categoria: String = "general",
    val fechaCreacion: Long = System.currentTimeMillis()
)