package ru.blackmirrror.traveller.features.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.UserRequest
import ru.blackmirrror.traveller.domain.repositories.AuthRepository
import ru.blackmirrror.traveller.domain.usecases.LoginUserUseCase
import ru.blackmirrror.traveller.domain.usecases.RememberAsGuest

class LoginViewModel(
    private val loginUserUseCase: LoginUserUseCase,
    private val rememberAsGuest: RememberAsGuest
): ViewModel() {

    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> = _isLogin

    fun login(user: UserRequest) {
        viewModelScope.launch {
            when (loginUserUseCase.invoke(user)) {
                is ResultState.Success -> {
                    _isLogin.postValue(true)
                }
                else -> {
                    _isLogin.postValue(false)
                }
            }
        }
    }

    fun rememberAsGuest() {
        rememberAsGuest.invoke()
    }
}