package ru.blackmirrror.traveller.features.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.blackmirrror.traveller.domain.models.UserResponse
import ru.blackmirrror.traveller.domain.usecases.GetCurrentUserUseCase
import ru.blackmirrror.traveller.domain.usecases.IsGuestUseCase
import ru.blackmirrror.traveller.domain.usecases.LogoutUserUseCase

class AccountViewModel(
    private val logoutUserUseCase: LogoutUserUseCase,
    private val isGuestUseCase: IsGuestUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
): ViewModel() {

    private val _isGuest = MutableLiveData<Boolean>()
    val isGuest: LiveData<Boolean> = _isGuest

    private val _currentUser = MutableLiveData<UserResponse?>()
    val currentUser: LiveData<UserResponse?> = _currentUser
    init {
        initUser()
    }

    private fun initUser() {
        _isGuest.postValue(isGuestUseCase.invoke())
        _currentUser.postValue(getCurrentUserUseCase.invoke())
    }

    fun logout() {
        logoutUserUseCase.invoke()
    }
}