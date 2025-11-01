package com.budgeteasy.data.repository

import com.budgeteasy.data.local.database.dao.UserDao
import com.budgeteasy.data.local.database.entity.UserEntity
import com.budgeteasy.domain.model.User
import com.budgeteasy.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) : IUserRepository {

    override suspend fun registerUser(user: User): Long {
        val userEntity = user.toEntity()
        return userDao.insertUser(userEntity)
    }

    override suspend fun loginUser(email: String, contrasena: String): User? {
        val userEntity = userDao.getUserByEmail(email)
        return if (userEntity != null && userEntity.contrasena == contrasena) {
            userEntity.toModel()
        } else {
            null
        }
    }

    override suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)?.toModel()
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user.toEntity())
    }

    override suspend fun deleteUser(user: User) {
        userDao.deleteUser(user.toEntity())
    }

    override suspend fun emailExists(email: String): Boolean {
        return userDao.emailExists(email)
    }

    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun updatePasswordByEmail(email: String, newPassword: String): Boolean {
        return try {
            // a) Busca al usuario por su email
            val user = userDao.getUserByEmail(email)

            if (user != null) {
                // b) Si el usuario existe, actualiza su contraseña
                val updatedRows = userDao.updateUser(user.copy(contrasena = newPassword))

                // 🚀 CAMBIO CLAVE AQUÍ: Retorna 'true' SÓLO si se actualizó 1 fila.
                updatedRows > 0 // Retorna true si updatedRows es 1 o más, indicando éxito
            } else {
                // c) Si el usuario no existe, retorna 'false'
                false
            }
        } catch (e: Exception) {
            // d) Si hay cualquier otro error, retorna 'false'
            e.printStackTrace()
            false
        }
    }

    private fun User.toEntity(): UserEntity {
        return UserEntity(
            id = this.id,
            nombre = this.nombre,
            apellidos = this.apellidos,
            numeroDeTelefono = this.numeroDeTelefono,
            idioma = this.idioma,
            email = this.email,
            contrasena = this.contrasena,
            fechaRegistro = this.fechaRegistro
        )
    }

    private fun UserEntity.toModel(): User {
        return User(
            id = this.id,
            nombre = this.nombre,
            apellidos = this.apellidos,
            numeroDeTelefono = this.numeroDeTelefono,
            idioma = this.idioma,
            email = this.email,
            contrasena = this.contrasena,
            fechaRegistro = this.fechaRegistro
        )
    }
}
