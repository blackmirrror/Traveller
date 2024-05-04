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
            createMarkUseCase = get(),
            getMarksByParameterUseCase = get(),
            authRepository = get(),
            markRepository = get()
        )
    }

    viewModel {
        RegisterViewModel(
            registerUserUseCase = get(),
            authRepository = get()
        )
    }

    viewModel {
        LoginViewModel(
            loginUserUseCase = get(),
            authRepository = get()
        )
    }

    viewModel {
        AccountViewModel(
            authRepository = get()
        )
    }
}