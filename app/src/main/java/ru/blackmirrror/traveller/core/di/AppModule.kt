package ru.blackmirrror.traveller.core.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.blackmirrror.traveller.features.account.AccountViewModel
import ru.blackmirrror.traveller.features.login.LoginViewModel
import ru.blackmirrror.traveller.features.map.MapViewModel
import ru.blackmirrror.traveller.features.register.RegisterViewModel

val appModule = module {
    viewModel {
        MapViewModel(
            getAllMarksUseCase = get(),
            createMarkUseCase = get(),
            isGuestUseCase = get(),
            isLoginUserUseCase = get(),
            getMarksByParameterUseCase = get()
        )
    }

    viewModel {
        RegisterViewModel(
            registerUserUseCase = get(),
            rememberAsGuest = get()
        )
    }

    viewModel {
        LoginViewModel(
            loginUserUseCase = get(),
            rememberAsGuest = get()
        )
    }

    viewModel {
        AccountViewModel(
            logoutUserUseCase = get(),
            isGuestUseCase = get(),
            getCurrentUserUseCase = get()
        )
    }
}