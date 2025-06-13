package com.example.guiadeviajes_android_gpt.map.core.database


import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CachedPlace::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cachedPlaceDao(): CachedPlaceDao
}