package ru.blackmirrror.traveller.domain.repositories

import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.UserRequest
import ru.blackmirrror.traveller.domain.models.UserResponse

interface AuthRepository {
    suspend fun register(user: UserRequest): ResultState<UserResponse>
    suspend fun login(user: UserRequest): ResultState<UserResponse>
    suspend fun isLoginUser(): ResultState<UserResponse>
    fun getCurrentUser(): UserResponse?
    fun logout()
    fun isGuest(): Boolean
    fun rememberAsGuest()
}