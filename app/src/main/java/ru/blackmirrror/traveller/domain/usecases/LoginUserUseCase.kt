package ru.blackmirrror.traveller.domain.usecases

import ru.blackmirrror.traveller.domain.models.EmptyFields
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.UserRequest
import ru.blackmirrror.traveller.domain.models.UserResponse
import ru.blackmirrror.traveller.domain.repositories.AuthRepository

class LoginUserUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(user: UserRequest): ResultState<UserResponse> {
        if (user.username.isEmpty() || user.password.isEmpty())
            return ResultState.Error(EmptyFields)
        return authRepository.login(user)
    }
}