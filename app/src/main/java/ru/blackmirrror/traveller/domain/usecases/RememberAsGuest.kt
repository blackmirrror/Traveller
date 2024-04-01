package ru.blackmirrror.traveller.domain.usecases

import ru.blackmirrror.traveller.domain.repositories.AuthRepository

class RememberAsGuest(private val authRepository: AuthRepository) {
    operator fun invoke() {
        authRepository.rememberAsGuest()
    }
}