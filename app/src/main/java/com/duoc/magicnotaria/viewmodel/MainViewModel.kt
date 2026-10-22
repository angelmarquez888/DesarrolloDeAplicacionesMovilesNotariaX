package com.duoc.magicnotaria.viewmodel

import androidx.lifecycle.ViewModel
import com.duoc.magicnotaria.repository.AuthRepository

class MainViewModel : ViewModel() {
    private val repository = AuthRepository()

    fun intentarLogin(email: String, clave: String): Boolean {
        return repository.verificarUsuario(email, clave)
    }
}