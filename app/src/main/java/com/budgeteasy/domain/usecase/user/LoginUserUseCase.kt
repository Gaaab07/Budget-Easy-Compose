package com.budgeteasy.domain.usecase.user

import com.budgeteasy.domain.model.User
import com.budgeteasy.domain.repository.IUserRepository
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(email: String, contrasena: String): User? {
        return userRepository.loginUser(email, contrasena)
    }
}