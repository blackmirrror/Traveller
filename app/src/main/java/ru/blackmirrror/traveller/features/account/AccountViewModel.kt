package ru.blackmirrror.traveller.features.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.blackmirrror.traveller.domain.models.UserResponse
import ru.blackmirrror.traveller.domain.repositories.AuthRepository

class AccountViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _isGuest = MutableLiveData<Boolean>()
    val isGuest: LiveData<Boolean> = _isGuest

    private val _currentUser = MutableLiveData<UserResponse?>()
    val currentUser: LiveData<UserResponse?> = _currentUser
    init {
        initUser()
    }

    private fun initUser() {
        _isGuest.postValue(authRepository.isGuest())
        _currentUser.postValue(authRepository.getCurrentUser())
    }

    fun logout() {
        authRepository.logout()
    }
}