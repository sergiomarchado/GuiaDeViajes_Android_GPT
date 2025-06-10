package com.example.guiadeviajes_android_gpt.auth.di
/**
 * AuthModule.kt
 *
 * Módulo de Dagger Hilt para proporcionar instancias singleton de FirebaseAuth y FirebaseDatabase.
 * Las dependencias se instalan en el SingletonComponent para su uso en toda la aplicación.
 */
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
/**
 * Objeto que define las dependencias de autenticación en el grafo de Hilt.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    /**
     * Proporciona la instancia singleton de FirebaseAuth para gestionar la autenticación de usuarios.
     *
     * @return FirebaseAuth instancia única.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    /**
     * Proporciona la instancia singleton de FirebaseDatabase apuntando a la región europea.
     * Importante para la latencia y cumplimiento de GDPR.
     *
     * @return FirebaseDatabase instancia única configurada con la URL de la base de datos.
     */
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance(
            "https://guiaviajesia-default-rtdb.europe-west1.firebasedatabase.app"
        )
    }
}
