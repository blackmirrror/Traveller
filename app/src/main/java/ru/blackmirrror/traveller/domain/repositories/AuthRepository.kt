package ru.blackmirrror.traveller.domain.repositories

import ru.blackmirrror.traveller.domain.models.LoginResponse
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.UserRequest
import ru.blackmirrror.traveller.domain.models.UserResponse

interface AuthRepository {
    suspend fun register(user: UserRequest): ResultState<UserResponse>
    suspend fun login(user: UserRequest): ResultState<LoginResponse>
    fun logout()
    suspend fun isLoggingUser(): Boolean
    fun rememberAsGuest()
}