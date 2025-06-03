package com.example.guiadeviajes_android_gpt.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guiadeviajes_android_gpt.profile.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase
) : ViewModel() {

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        _isLoading.value = true

        val userRef = database.getReference("users").child(uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue(UserProfile::class.java)
                profile?.let {
                    _userProfile.value = it
                }
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMessage.value = error.message
                _isLoading.value = false
            }
        })
    }

    fun updateUserProfile(profile: UserProfile) {
        // Validaciones básicas
        if (profile.firstName.isBlank()) {
            _errorMessage.value = "El nombre no puede estar vacío."
            return
        }
        if (profile.lastName.isBlank()) {
            _errorMessage.value = "Los apellidos no pueden estar vacíos."
            return
        }

        val uid = firebaseAuth.currentUser?.uid ?: return
        _isLoading.value = true

        val userRef = database.getReference("users").child(uid)
        userRef.setValue(profile)
            .addOnSuccessListener {
                _userProfile.value = profile
                _isLoading.value = false
            }
            .addOnFailureListener {
                _errorMessage.value = it.message
                _isLoading.value = false
            }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
