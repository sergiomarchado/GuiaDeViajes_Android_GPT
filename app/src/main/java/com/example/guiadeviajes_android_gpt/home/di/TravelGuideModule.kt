package com.example.guiadeviajes_android_gpt.home.di
/**
 * TravelGuideModule.kt
 *
 * Módulo de Dagger Hilt para proporcionar dependencias relacionadas con la API de ChatGPT.
 * Incluye interceptor de autenticación, cliente OkHttp con logging y Retrofit configurado.
 */
import com.example.guiadeviajes_android_gpt.BuildConfig
import com.example.guiadeviajes_android_gpt.home.data.remote.ChatgptApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Define dependencias singleton para consumo de la API de ChatGPT.
 */
@InstallIn(SingletonComponent::class)
@Module
object TravelGuideModule {

    /**
     * Interceptor que añade el header Authorization con la API key de ChatGPT.
     */
    @Provides
    @Singleton
    @Named("ChatGptAuthInterceptor")
    fun provideChatGptAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.API_KEY}")
                .build()
            chain.proceed(newRequest)
        }
    }

    /**
     * Cliente OkHttp configurado con timeouts, auth interceptor y logging.
     * El logging level depende de si estamos en DEBUG o no.
     */
    @Provides
    @Singleton
    @Named("ChatGptOkHttpClient")
    fun provideChatGptOkHttpClient(
        @Named("ChatGptAuthInterceptor") authInterceptor: Interceptor

    ): OkHttpClient {
        // Interceptor para loguear peticiones y respuestas
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            // Timeout de conexión
            .connectTimeout(30, TimeUnit.SECONDS)
            // Timeout de lectura
            .readTimeout(30, TimeUnit.SECONDS)
            // Timeout de escritura
            .writeTimeout(30, TimeUnit.SECONDS)
            // Añade interceptor de autenticación
            .addInterceptor(authInterceptor)
            // Añade interceptor de logging
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /**
     * Retrofit configurado con base URL de ChatGPT, Moshi converter y cliente OkHttp.
     */
    @Provides
    @Singleton
    @Named("ChatGptRetrofit")
    fun provideChatGptRetrofit(
        @Named("ChatGptOkHttpClient") client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ChatgptApi.BASE_URL) // URL base de la API
            .addConverterFactory(MoshiConverterFactory.create())  // Convierte JSON a DTOs
            .client(client) // Cliente OkHttp configurado
            .build()
    }

    /**
     * Provee la implementación de la interfaz ChatgptApi a partir de Retrofit.
     */
    @Provides
    @Singleton
    fun provideChatgptApi(
        @Named("ChatGptRetrofit") retrofit: Retrofit
    ): ChatgptApi {
        return retrofit.create(ChatgptApi::class.java)
    }
}
