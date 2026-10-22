package com.duoc.magicnotaria.repository

class AuthRepository {
    fun verificarUsuario(email: String, clave: String): Boolean {
        return email.isNotEmpty() && clave.isNotEmpty()
    }
}