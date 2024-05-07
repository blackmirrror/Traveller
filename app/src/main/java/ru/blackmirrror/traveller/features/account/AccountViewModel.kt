package ru.blackmirrror.traveller.features.account

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
import ru.blackmirrror.traveller.domain.models.Subscribe
import ru.blackmirrror.traveller.domain.models.UserResponse
import ru.blackmirrror.traveller.domain.repositories.AuthRepository
import ru.blackmirrror.traveller.domain.repositories.MarkRepository

class AccountViewModel(
    private val authRepository: AuthRepository,
    private val markRepository: MarkRepository
): ViewModel() {

    private val _isGuest = MutableLiveData<Boolean>()
    val isGuest: LiveData<Boolean> = _isGuest

    private val _currentUser = MutableLiveData<UserResponse?>()
    val currentUser: LiveData<UserResponse?> = _currentUser

    private val _subscribes = MutableLiveData<List<UserResponse>?>()
    val subscribes: LiveData<List<UserResponse>?> = _subscribes
    init {
        initUser()
        initSubscribes()
    }

    private fun initUser() {
        _isGuest.postValue(authRepository.isGuest())
        _currentUser.postValue(authRepository.getCurrentUser())
    }

    private fun initSubscribes() {
        viewModelScope.launch {
            val res = markRepository.getSubscribesByUserId()
            if (res is ResultState.Success) {
                _subscribes.postValue(res.data)
            }
        }
    }

    fun deleteSubscribe(id: Long) {
        viewModelScope.launch {
            val res = markRepository.deleteSubscribe(id)
            if (res is ResultState.Success) {
                _subscribes.value = _subscribes.value?.filter { it.id != id }
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }
}