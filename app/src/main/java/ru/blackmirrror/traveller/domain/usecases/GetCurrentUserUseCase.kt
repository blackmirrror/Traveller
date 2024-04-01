package ru.blackmirrror.traveller.domain.usecases

import ru.blackmirrror.traveller.domain.models.UserResponse
import ru.blackmirrror.traveller.domain.repositories.AuthRepository

class GetCurrentUserUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(): UserResponse? {
        return authRepository.getCurrentUser()
    }
}