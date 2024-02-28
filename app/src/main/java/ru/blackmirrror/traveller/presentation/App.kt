package ru.blackmirrror.traveller.presentation

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("f2c7ba30-2b02-4bc4-99fd-e7d8407d8bce")
    }
}