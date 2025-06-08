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
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object TravelGuideModule {

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

    @Provides
    @Singleton
    @Named("ChatGptOkHttpClient")
    fun provideChatGptOkHttpClient(
        @Named("ChatGptAuthInterceptor") authInterceptor: Interceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("ChatGptRetrofit")
    fun provideChatGptRetrofit(
        @Named("ChatGptOkHttpClient") client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ChatgptApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideChatgptApi(
        @Named("ChatGptRetrofit") retrofit: Retrofit
    ): ChatgptApi {
        return retrofit.create(ChatgptApi::class.java)
    }
}
