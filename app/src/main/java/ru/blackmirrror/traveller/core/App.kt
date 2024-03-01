package ru.blackmirrror.traveller.core

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.blackmirrror.traveller.core.di.appModule
import ru.blackmirrror.traveller.core.di.dataModule
import ru.blackmirrror.traveller.core.di.domainModule

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("f2c7ba30-2b02-4bc4-99fd-e7d8407d8bce")
        startKoin {
            androidContext(this@App)
            modules(dataModule, domainModule, appModule)
        }
    }
}