package com.example.guiadeviajes_android_gpt.auth.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        // ✅ Usamos la URL de la región europea (¡muy importante!)
        return FirebaseDatabase.getInstance(
            "https://guiaviajesia-default-rtdb.europe-west1.firebasedatabase.app"
        )
    }
}
