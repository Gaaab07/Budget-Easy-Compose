package com.budgeteasy.domain.repository

import com.budgeteasy.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    suspend fun registerUser(user: User): Long
    suspend fun loginUser(email: String, contrasena: String): User?
    suspend fun getUserById(userId: Int): User?
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
    suspend fun emailExists(email: String): Boolean
    fun getAllUsers(): Flow<List<User>>
}