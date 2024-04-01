package ru.blackmirrror.traveller.core.di

import org.koin.dsl.module
import ru.blackmirrror.traveller.domain.usecases.CreateMarkUseCase
import ru.blackmirrror.traveller.domain.usecases.GetAllMarksUseCase
import ru.blackmirrror.traveller.domain.usecases.GetCurrentUserUseCase
import ru.blackmirrror.traveller.domain.usecases.GetMarksByParameterUseCase
import ru.blackmirrror.traveller.domain.usecases.IsGuestUseCase
import ru.blackmirrror.traveller.domain.usecases.IsLoginUserUseCase
import ru.blackmirrror.traveller.domain.usecases.LoginUserUseCase
import ru.blackmirrror.traveller.domain.usecases.LogoutUserUseCase
import ru.blackmirrror.traveller.domain.usecases.RegisterUserUseCase
import ru.blackmirrror.traveller.domain.usecases.RememberAsGuest

val domainModule = module {
    factory {
        GetAllMarksUseCase(markRepository = get())
    }

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
        IsGuestUseCase(authRepository = get())
    }

    factory {
        IsLoginUserUseCase(authRepository = get())
    }

    factory {
        RememberAsGuest(authRepository = get())
    }

    factory {
        GetCurrentUserUseCase(authRepository = get())
    }

    factory {
        LogoutUserUseCase(authRepository = get())
    }

    factory {
        GetMarksByParameterUseCase()
    }
}