package com.example.guiadeviajes_android_gpt.home.di

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

    @Provides
    @Singleton
    fun provideGooglePlacesApi(retrofit: Retrofit): GooglePlacesApi {
        return retrofit.create(GooglePlacesApi::class.java)
    }
}
