package com.example.guiadeviajes_android_gpt.promotions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guiadeviajes_android_gpt.promotions.data.Promotion
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import com.example.guiadeviajes_android_gpt.promotions.data.PromotionsRepository

@HiltViewModel
class PromotionsViewModel @Inject constructor(
    repository: PromotionsRepository
) : ViewModel() {
    val promotions: StateFlow<List<Promotion>> = repository.observePromotions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}