package com.example.guiadeviajes_android_gpt.home.di

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
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object TravelGuideModule {

    // ✅ Interceptor para logs de red (solo si es debug)
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    // ✅ Interceptor para añadir la API Key a cada petición
    @Provides
    @Singleton
    fun provideAuthorizationInterceptor(): Interceptor {
        return Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.API_KEY}")
                .build()
            chain.proceed(newRequest)
        }
    }

    // ✅ Cliente OkHttp con timeouts configurados y los interceptores añadidos
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // conexión
            .readTimeout(30, TimeUnit.SECONDS)    // lectura
            .writeTimeout(30, TimeUnit.SECONDS)   // escritura
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // ✅ Retrofit configurado con Moshi y el cliente
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ChatgptApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }

    // ✅ Instancia del servicio ChatgptApi
    @Provides
    @Singleton
    fun provideChatgptApi(retrofit: Retrofit): ChatgptApi {
        return retrofit.create(ChatgptApi::class.java)
    }
}
