package com.duoc.magicnotaria.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.duoc.magicnotaria.model.Documento
import com.duoc.magicnotaria.model.Usuario
import com.duoc.magicnotaria.repository.AuthRepository
import com.duoc.magicnotaria.repository.NotariaRepository

class MainViewModel : ViewModel() {
    private val repository = AuthRepository()
    private val notariaRepository = NotariaRepository()

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

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
        busqueda = ""
    }

    val categorias = notariaRepository.obtenerCategorias()

    var documentos by mutableStateOf(notariaRepository.obtenerDocumentos())
        private set

    var busqueda by mutableStateOf("")
        private set

    fun cambiarBusqueda(texto: String) {
        busqueda = texto
    }

    val documentosFiltrados: List<Documento>
        get() = documentos.filter { it.titulo.contains(busqueda, ignoreCase = true) }

    fun firmarDocumento(id: Int) {
        documentos = documentos.map { if (it.id == id) it.copy(firmado = true) else it }
    }
}