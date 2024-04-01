package ru.blackmirrror.traveller.features.login

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
import ru.blackmirrror.traveller.domain.usecases.LoginUserUseCase
import ru.blackmirrror.traveller.domain.usecases.RememberAsGuest

class LoginViewModel(
    private val loginUserUseCase: LoginUserUseCase,
    private val rememberAsGuest: RememberAsGuest
): ViewModel() {

    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> = _isLogin

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun login(user: UserRequest) {
        viewModelScope.launch {
            when (val res = loginUserUseCase.invoke(user)) {
                is ResultState.Success -> {
                    _isLogin.postValue(true)
                }
                else -> {
                    handleError(res)
                }
            }
        }
    }

    fun rememberAsGuest() {
        rememberAsGuest.invoke()
    }

    private fun <T> handleError(result: ResultState<T>) {
        _isLogin.postValue(false)
        if (result is ResultState.Error) {
            when (result.error) {
                is EmptyFields -> _error.postValue("Проверьте, все ли поля заполнены")
                is NoInternet -> _error.postValue("Соединение потеряно, загрузим сохраненные данные")
                is NotFound -> _error.postValue("Проверьте имя и пароль, что-то из этого не верно")
                is OtherError -> _error.postValue("Упс, с данными что-то не так")
                is ServerError -> _error.postValue("Неполадки на сервере, уже работаем над этим")
            }
        }
    }
}