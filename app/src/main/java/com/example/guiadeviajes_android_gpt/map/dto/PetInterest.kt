package com.example.guiadeviajes_android_gpt.map.dto

import com.google.android.gms.maps.model.LatLng

data class PetInterest(
    val id: String,
    val name: String,
    val position: LatLng,
    val category: String,
    val address: String?,
    val phoneNumber: String?,
    val website: String?,
    val photos: List<String> = emptyList()
)
