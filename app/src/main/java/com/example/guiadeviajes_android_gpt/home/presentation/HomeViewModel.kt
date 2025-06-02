package com.example.guiadeviajes_android_gpt.home.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/*
* Proyecto: GuiaDeViajes_Android_GPT
* Creado por: Sergio Marchado Ropero
* Fecha: 02/06/2025
*/
@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {
    init {
        println("Soy el Home View Model!")
    }
}