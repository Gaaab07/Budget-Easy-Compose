
package com.budgeteasy.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val apellidos: String,
    val numeroDeTelefono: String,
    val idioma: String,
    val email: String,
    val contrasena: String,
    val fechaRegistro: Long = System.currentTimeMillis()
)

