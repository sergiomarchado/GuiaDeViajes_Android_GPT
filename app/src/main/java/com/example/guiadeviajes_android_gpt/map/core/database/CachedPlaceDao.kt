package com.example.guiadeviajes_android_gpt.map.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedPlaceDao {
    @Query("SELECT * FROM cached_places WHERE interestKey = :interestKey")
    suspend fun getPlacesForInterest(interestKey: String): List<CachedPlace>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<CachedPlace>)
}