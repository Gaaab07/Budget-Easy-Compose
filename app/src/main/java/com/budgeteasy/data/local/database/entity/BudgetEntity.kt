package com.budgeteasy.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val nombre: String,
    val montoPlaneado: Double,
    val montoGastado: Double = 0.0,
    val periodo: String,
    val fechaInicio: Long,
    val fechaFin: Long,
    val descripcion: String = "",
    val activo: Boolean = true,
    val fechaCreacion: Long = System.currentTimeMillis()
)