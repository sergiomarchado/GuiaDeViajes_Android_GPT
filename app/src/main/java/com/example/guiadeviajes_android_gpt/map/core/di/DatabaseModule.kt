package com.example.guiadeviajes_android_gpt.map.core.di

import android.content.Context
import androidx.room.Room
import com.example.guiadeviajes_android_gpt.map.core.database.AppDatabase
import com.example.guiadeviajes_android_gpt.map.core.database.CachedPlaceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "places-db"
        ).build()
    }

    @Provides
    fun provideCachedPlaceDao(db: AppDatabase): CachedPlaceDao = db.cachedPlaceDao()
}
