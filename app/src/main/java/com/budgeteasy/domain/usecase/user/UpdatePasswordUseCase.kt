package com.budgeteasy.domain.usecase.user

import com.budgeteasy.domain.repository.IUserRepository
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(email: String, newPassword: String): Boolean {
        return userRepository.updatePasswordByEmail(email, newPassword)
    }
}
