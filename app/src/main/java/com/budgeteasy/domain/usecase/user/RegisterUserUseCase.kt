package com.budgeteasy.domain.usecase.user

import com.budgeteasy.domain.model.User
import com.budgeteasy.domain.repository.IUserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(user: User): Long {
        return userRepository.registerUser(user)
    }
}