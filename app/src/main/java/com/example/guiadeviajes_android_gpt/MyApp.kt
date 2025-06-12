package com.example.guiadeviajes_android_gpt
/**
 * MyApp.kt
 *
 * Clase Application que inicializa la librería de Google Places al arrancar la aplicación.
 * Se utiliza Hilt para habilitar la inyección de dependencias en toda la app.
 */
import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp



@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Inicializa Places SDK con la clave que inyectaste en BuildConfig.API_KEYG
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.API_KEYG)
        }
    }
}