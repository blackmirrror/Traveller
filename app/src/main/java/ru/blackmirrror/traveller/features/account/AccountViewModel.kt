package ru.blackmirrror.traveller.features.account

import androidx.lifecycle.ViewModel
import ru.blackmirrror.traveller.domain.usecases.LogoutUserUseCase

class AccountViewModel(
    private val logoutUserUseCase: LogoutUserUseCase
): ViewModel() {

    fun logout() {
        logoutUserUseCase.invoke()
    }
}