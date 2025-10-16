package com.budgeteasy.domain.model

data class Budget(
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