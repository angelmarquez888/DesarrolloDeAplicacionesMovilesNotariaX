package com.duoc.magicnotaria.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.duoc.magicnotaria.model.Usuario
import com.duoc.magicnotaria.repository.AuthRepository

class MainViewModel : ViewModel() {
    private val repository = AuthRepository()

    // Usuario que inició sesión (null = nadie). La pantalla se actualiza sola si cambia.
    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    // Devuelve null si todo salió bien, o el mensaje de error
    fun intentarLogin(correo: String, clave: String): String? {
        if (correo.isBlank() || clave.isBlank()) return "Completa todos los campos"
        val usuario = repository.login(correo.trim(), clave)
            ?: return "Correo o contraseña incorrectos"
        usuarioActual = usuario
        return null
    }

    fun registrar(
        nombre: String, rut: String, correo: String,
        clave: String, confirmar: String
    ): String? {
        if (nombre.isBlank() || rut.isBlank() || correo.isBlank() || clave.isBlank())
            return "Completa todos los campos"
        if (!correo.contains("@")) return "El correo no es válido"
        if (clave.length < 6) return "La contraseña debe tener al menos 6 caracteres"
        if (clave != confirmar) return "Las contraseñas no coinciden"

        val nuevo = Usuario(nombre.trim(), rut.trim(), correo.trim(), clave)
        if (!repository.registrar(nuevo)) return "Ese correo ya está registrado"
        usuarioActual = nuevo
        return null
    }

    fun cerrarSesion() {
        usuarioActual = null
    }
}