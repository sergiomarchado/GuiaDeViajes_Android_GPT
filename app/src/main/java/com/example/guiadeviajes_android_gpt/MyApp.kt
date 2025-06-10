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

    /**
     * Se ejecuta al crear la aplicación.
     * Aquí inicializamos la API de Google Places si no está ya inicializada.
     */
    override fun onCreate() {
        super.onCreate()
        // Comprueba si la librería de Places no está ya inicializada
        if (!Places.isInitialized()) {
            // Inicializa Places con la API_KEY (BuildCongig -> localproperties)
            Places.initialize(applicationContext, BuildConfig.API_KEYG)
        }
    }
}