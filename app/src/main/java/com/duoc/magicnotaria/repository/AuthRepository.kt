package com.duoc.magicnotaria.repository

import com.duoc.magicnotaria.database.UsuarioDao
import com.duoc.magicnotaria.model.Usuario

class AuthRepository(private val dao: UsuarioDao) {

    suspend fun login(correo: String, clave: String): Usuario? = dao.buscar(correo, clave)

    suspend fun registrar(usuario: Usuario): Boolean = dao.insertar(usuario) != -1L

    suspend fun crearUsuarioDePrueba() {
        registrar(
            Usuario(
                correo = "an.marquez@duoc.cl",
                nombre = "Angel Marquez",
                rut = "12.345.678-9",
                clave = "123456",
                direccion = "Av. Providencia 1234"
            )
        )
    }
}