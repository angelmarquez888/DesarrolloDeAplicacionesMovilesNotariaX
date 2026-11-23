package com.duoc.magicnotaria.repository

import com.duoc.magicnotaria.model.Sesion
import com.duoc.magicnotaria.model.Usuario

class AuthRepository(
    private val usuarioDao: UsuarioDao,
    private val sesionDao: SesionDao
) {

    suspend fun login(correo: String, clave: String): Usuario? {
        val usuario = usuarioDao.buscar(correo, clave) ?: return null
        guardarSesion(usuario)
        return usuario
    }

    suspend fun registrar(usuario: Usuario): Boolean {
        val creado = usuarioDao.insertar(usuario) != -1L
        if (creado) guardarSesion(usuario)
        return creado
    }

    suspend fun guardarSesion(usuario: Usuario) {
        sesionDao.guardar(Sesion(correo = usuario.correo))
    }

    suspend fun obtenerSesionActiva(): Usuario? {
        val sesion = sesionDao.obtener() ?: return null
        return usuarioDao.buscarPorCorreo(sesion.correo)
            ?: run { sesionDao.borrar(); null }
    }

    suspend fun cerrarSesion() = sesionDao.borrar()

    suspend fun crearUsuarioDePrueba() {
        usuarioDao.insertar(
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