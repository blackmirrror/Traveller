package ru.blackmirrror.traveller.domain.usecases

import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.UserResponse
import ru.blackmirrror.traveller.domain.repositories.AuthRepository

class IsLoginUserUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): ResultState<UserResponse> {
        return authRepository.isLoginUser()
    }
}