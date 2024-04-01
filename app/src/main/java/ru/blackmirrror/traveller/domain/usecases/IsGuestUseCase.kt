package ru.blackmirrror.traveller.domain.usecases

import ru.blackmirrror.traveller.domain.repositories.AuthRepository

class IsGuestUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(): Boolean {
        return authRepository.isGuest()
    }
}