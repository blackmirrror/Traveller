package ru.blackmirrror.traveller.core.di

import org.koin.dsl.module
import ru.blackmirrror.traveller.domain.usecases.CreateMarkUseCase
import ru.blackmirrror.traveller.domain.usecases.GetMarksByParameterUseCase
import ru.blackmirrror.traveller.domain.usecases.LoginUserUseCase
import ru.blackmirrror.traveller.domain.usecases.RegisterUserUseCase

val domainModule = module {

    factory {
        CreateMarkUseCase(markRepository = get())
    }

    factory {
        LoginUserUseCase(authRepository = get())
    }

    factory {
        RegisterUserUseCase(authRepository = get())
    }

    factory {
        GetMarksByParameterUseCase()
    }
}