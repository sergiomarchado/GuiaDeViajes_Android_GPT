package com.example.guiadeviajes_android_gpt

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (!Places.isInitialized()) {
            // Aquí usas la API_KEY que has definido en tu BuildConfig
            Places.initialize(applicationContext, BuildConfig.API_KEYG)
        }
    }
}