package ru.blackmirrror.traveller.features.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.UserRequest
import ru.blackmirrror.traveller.domain.repositories.AuthRepository
import ru.blackmirrror.traveller.domain.usecases.RegisterUserUseCase
import ru.blackmirrror.traveller.domain.usecases.RememberAsGuest

class RegisterViewModel(
    private val registerUserUseCase: RegisterUserUseCase,
    private val rememberAsGuest: RememberAsGuest
): ViewModel() {

    private val _isRegister = MutableLiveData<Boolean>()
    val isRegister: LiveData<Boolean> = _isRegister

    fun register(user: UserRequest) {
        viewModelScope.launch {
            when (registerUserUseCase.invoke(user)) {
                is ResultState.Success -> {
                    _isRegister.postValue(true)
                }
                else -> {
                    _isRegister.postValue(false)
                }
            }
        }
    }

    fun rememberAsGuest() {
        rememberAsGuest.invoke()
    }
}