package ru.blackmirrror.traveller.features.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.blackmirrror.traveller.domain.models.Conflict
import ru.blackmirrror.traveller.domain.models.EmptyFields
import ru.blackmirrror.traveller.domain.models.ErrorType
import ru.blackmirrror.traveller.domain.models.NoContent
import ru.blackmirrror.traveller.domain.models.NoInternet
import ru.blackmirrror.traveller.domain.models.NotFound
import ru.blackmirrror.traveller.domain.models.OtherError
import ru.blackmirrror.traveller.domain.models.ResultState
import ru.blackmirrror.traveller.domain.models.ServerError
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

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun register(user: UserRequest) {
        viewModelScope.launch {
            when (val res = registerUserUseCase.invoke(user)) {
                is ResultState.Success -> {
                    _isRegister.postValue(true)
                }
                else -> {
                    handleError(res)
                }
            }
        }
    }

    private fun <T> handleError(result: ResultState<T>) {
        _isRegister.postValue(false)
        if (result is ResultState.Error) {
            when (result.error) {
                is EmptyFields -> _error.postValue("Проверьте, все ли поля заполнены")
                is NoInternet -> _error.postValue("Соединение потеряно, но вы можете войти в существующий аккаунт или гостем")
                is Conflict -> _error.postValue("Пользователь с таким именем уже существует")
                is OtherError -> _error.postValue("Упс, с данными что-то не так")
                is ServerError -> _error.postValue("Неполадки на сервере, уже работаем над этим")
            }
        }
    }

    fun rememberAsGuest() {
        rememberAsGuest.invoke()
    }
}