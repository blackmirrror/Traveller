package ru.blackmirrror.traveller.data.repositories

import android.content.Context
import ru.blackmirrror.traveller.data.local.UserSharedPreferences
import ru.blackmirrror.traveller.data.models.NetworkUtils
import ru.blackmirrror.traveller.data.remote.ApiService
import ru.blackmirrror.traveller.domain.models.NoInternet
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.UserRequest
import ru.blackmirrror.traveller.domain.models.UserResponse
import ru.blackmirrror.traveller.domain.repositories.AuthRepository

class AuthRepositoryImpl(
    private val context: Context,
    private val service: ApiService,
    private val userPrefs: UserSharedPreferences
)
    : AuthRepository {
    override suspend fun register(user: UserRequest): ResultState<UserResponse> {
        return if (NetworkUtils.isInternetConnected(context)) {
            val res = service.registerUser(user)
            if (res.isSuccessful)
                login(user)
            else
                ApiUtils.handleResponse(res)
        } else {
            ResultState.Error(NoInternet)
        }
    }

    override suspend fun login(user: UserRequest): ResultState<UserResponse> {
        return if (NetworkUtils.isInternetConnected(context)) {
            val res = service.loginUser(user)
            if (res.isSuccessful) {
                val body = res.body()
                if (body != null) {
                    userPrefs.id = body.id
                    userPrefs.username = user.username
                    userPrefs.password = user.password
                    ResultState.Success(body)
                }
                ApiUtils.handleResponse(res)
            }
            ApiUtils.handleResponse(res)
        } else {
            loginLocal()
        }
    }

    override suspend fun isLoginUser(): ResultState<UserResponse> {
        if (userPrefs.id != -1L && userPrefs.username != null && userPrefs.password != null) {
            return login(UserRequest(userPrefs.username!!, userPrefs.password!!))
        }
        return ResultState.Error(NoInternet)
    }

    override fun getCurrentUser(): UserResponse? {
        if (userPrefs.id != -1L && userPrefs.username != null) {
            return UserResponse(userPrefs.id, userPrefs.username!!)
        }
        return null
    }

    private fun loginLocal(): ResultState<UserResponse> {
        return if (userPrefs.id != -1L && userPrefs.username != null && userPrefs.password != null) {
            ResultState.Error(NoInternet, UserResponse(userPrefs.id, userPrefs.username!!))
        } else
            ResultState.Error(NoInternet)
    }

    override fun logout() {
        userPrefs.clearPreferences()
    }

    override fun isGuest(): Boolean {
        return userPrefs.isGuest?: false
    }

    override fun rememberAsGuest() {
        userPrefs.isGuest = true
    }
}