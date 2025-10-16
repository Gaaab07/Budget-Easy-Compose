package com.budgeteasy.domain.model

data class User(
    val id: Int = 0,
    val nombre: String,
    val apellidos: String,
    val numeroDeTelefono: String,
    val idioma: String,
    val email: String,
    val contrasena: String,
    val fechaRegistro: Long = System.currentTimeMillis()
)