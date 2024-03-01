package ru.blackmirrror.traveller.domain.usecases

import ru.blackmirrror.traveller.domain.repositories.AuthRepository

class IsLoggingUserUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Boolean {
        return authRepository.isLoggingUser()
    }
}