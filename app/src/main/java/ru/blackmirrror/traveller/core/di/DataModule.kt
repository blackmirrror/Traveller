package ru.blackmirrror.traveller.core.di

import org.koin.dsl.module
import ru.blackmirrror.traveller.data.local.UserSharedPreferences
import ru.blackmirrror.traveller.data.remote.ApiFactory
import ru.blackmirrror.traveller.data.remote.ApiService
import ru.blackmirrror.traveller.domain.repositories.AuthRepository
import ru.blackmirrror.traveller.data.repositories.AuthRepositoryImpl
import ru.blackmirrror.traveller.data.repositories.MarkRepositoryImpl
import ru.blackmirrror.traveller.domain.repositories.MarkRepository

val dataModule = module {
    single<MarkRepository> {
        MarkRepositoryImpl(
            service = get(),
            authRepository = get()
        )
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            context = get(),
            service = get(),
            userPrefs = get()
        )
    }

    factory {
        UserSharedPreferences(context = get())
    }

    single<ApiService> {
        ApiFactory.create()
    }
}