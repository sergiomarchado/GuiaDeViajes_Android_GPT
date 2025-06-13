package com.example.guiadeviajes_android_gpt.map.core.database
import androidx.room.Entity

@Entity(tableName = "cached_places", primaryKeys = ["placeId", "interestKey"])
data class CachedPlace(
    val placeId: String,
    val interestKey: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val category: String,
    val address: String?,
    val phoneNumber: String?,
    val website: String?
)