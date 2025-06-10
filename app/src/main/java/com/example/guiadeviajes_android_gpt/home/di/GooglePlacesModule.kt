package com.example.guiadeviajes_android_gpt.home.di
/**
 * GooglePlacesModule.kt
 *
 * Módulo de Dagger Hilt para proporcionar la dependencia de la API de Google Places.
 * Utiliza una instancia de Retrofit ya configurada para crear el servicio de GooglePlacesApi.
 */
import com.example.guiadeviajes_android_gpt.home.data.remote.GooglePlacesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GooglePlacesModule {

    /**
     * Provee la implementación de GooglePlacesApi usando Retrofit.
     *
     * @param retrofit Instancia de Retrofit configurada con la base URL de Google Places.
     * @return GooglePlacesApi servicio para realizar peticiones a Google Places.
     */
    @Provides
    @Singleton
    fun provideGooglePlacesApi(retrofit: Retrofit): GooglePlacesApi {
        // Crea el servicio Retrofit para Google Places
        return retrofit.create(GooglePlacesApi::class.java)
    }
}
