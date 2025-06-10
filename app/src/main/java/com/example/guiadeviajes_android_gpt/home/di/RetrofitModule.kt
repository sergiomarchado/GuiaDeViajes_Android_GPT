package com.example.guiadeviajes_android_gpt.home.di
/**
 * RetrofitModule.kt
 *
 * Módulo de Dagger Hilt para proporcionar un cliente OkHttp y una instancia de Retrofit
 * configurada para consumir la API de Google Maps.
 */
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule { // 👈 o NetworkModule, el nombre no importa mientras coincida con tu archivo

    /**
     * Proporciona un OkHttpClient singleton con logging de cuerpo de peticiones/respuestas.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // Interceptor para loggear detalles de red (útil en desarrollo)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /**
     * Proporciona una instancia de Retrofit singleton apuntando a Google Maps API.
     * Utiliza Gson para deserializar respuestas JSON en objetos Kotlin.
     *
     * @param okHttpClient Cliente HTTP personalizado con interceptors.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
