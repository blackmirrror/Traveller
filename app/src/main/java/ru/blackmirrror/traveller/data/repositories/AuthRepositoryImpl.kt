package ru.blackmirrror.traveller.data.repositories

import okhttp3.MultipartBody
import ru.blackmirrror.traveller.data.local.UserSharedPreferences
import ru.blackmirrror.traveller.data.remote.ApiService
import ru.blackmirrror.traveller.domain.models.LoginResponse
import ru.blackmirrror.traveller.domain.models.NoInternet
import ru.blackmirrror.traveller.domain.models.OtherError
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.UserRequest
import ru.blackmirrror.traveller.domain.models.UserResponse
import ru.blackmirrror.traveller.domain.models.ValidationError
import ru.blackmirrror.traveller.domain.repositories.AuthRepository

class AuthRepositoryImpl(
    private val service: ApiService,
    private val userPrefs: UserSharedPreferences
)
    : AuthRepository {
    override suspend fun register(user: UserRequest): ResultState<UserResponse> {
        val res = service.registerUser(user)
        if (res.isSuccessful) {
            val body = res.body()
            if (body != null) {
                login(user)
                return ResultState.Success(body)
            }
            if (res.code() == 422)
                return ResultState.Error(ValidationError)
            return ResultState.Error(OtherError)
        }
        return ResultState.Error(NoInternet)
    }

    override suspend fun login(user: UserRequest): ResultState<LoginResponse> {
        val usernamePart = MultipartBody.Part.createFormData("username", user.username)
        val passwordPart = MultipartBody.Part.createFormData("password", user.password)

        val res = service.loginUser(usernamePart, passwordPart)

        if (res.isSuccessful) {
            val body = res.body()
            if (body != null) {
                userPrefs.username = user.username
                userPrefs.password = user.password
                userPrefs.currentToken = body.accessToken
                return ResultState.Success(body)
            }
            if (res.code() == 422)
                return ResultState.Error(ValidationError)
            return ResultState.Error(OtherError)
        }
        return ResultState.Error(NoInternet)
    }

    override fun logout() {
        userPrefs.clearPreferences()
    }

    override suspend fun isLoggingUser(): Boolean {
        if (userPrefs.isGuest == true) {
            return true
        }
        if (userPrefs.username != null && userPrefs.password != null) {
            return when (login(UserRequest(userPrefs.username!!, userPrefs.password!!))) {
                is ResultState.Success -> true
                else -> false
            }
        }
        return false
    }

    override fun rememberAsGuest() {
        userPrefs.isGuest = true
    }
}