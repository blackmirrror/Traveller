package ru.blackmirrror.traveller.domain.usecases

import ru.blackmirrror.traveller.domain.repositories.AuthRepository

class LogoutUserUseCase(private val authRepository: AuthRepository) {

    operator fun invoke() {
        authRepository.logout()
    }
}