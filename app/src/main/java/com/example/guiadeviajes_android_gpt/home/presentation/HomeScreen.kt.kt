package com.example.guiadeviajes_android_gpt.home.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
){
    Text(text ="Soy la Home Screen!")
}

