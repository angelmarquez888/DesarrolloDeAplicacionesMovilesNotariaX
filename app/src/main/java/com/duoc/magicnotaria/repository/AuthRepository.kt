package com.duoc.magicnotaria.repository

import com.duoc.magicnotaria.model.Usuario

class AuthRepository {

    // "Base de datos" en memoria: ya viene un usuario de prueba
    private val usuarios = mutableListOf(
        Usuario("Angel Marquez", "12.345.678-9", "an.marquez@duoc.cl", "123456")
    )

    fun login(correo: String, clave: String): Usuario? {
        return usuarios.find { it.correo == correo && it.clave == clave }
    }

    fun registrar(usuario: Usuario): Boolean {
        if (usuarios.any { it.correo == usuario.correo }) return false
        usuarios.add(usuario)
        return true
    }
}