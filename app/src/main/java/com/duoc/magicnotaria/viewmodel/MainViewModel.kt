package com.duoc.magicnotaria.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.magicnotaria.database.AppDatabase
import com.duoc.magicnotaria.model.Documento
import com.duoc.magicnotaria.model.Usuario
import com.duoc.magicnotaria.repository.AuthRepository
import com.duoc.magicnotaria.repository.NotariaRepository
import kotlinx.coroutines.launch

private val formatoRut = Regex("""^\d{1,2}\.?\d{3}\.?\d{3}-[\dkK]$""")

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(AppDatabase.obtener(application).usuarioDao())
    private val notariaRepository = NotariaRepository()

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set
    var fotoPerfil by mutableStateOf<Bitmap?>(null)
        private set
    var documentos by mutableStateOf(notariaRepository.obtenerDocumentos())
        private set
    var busqueda by mutableStateOf("")
        private set

    val categorias = notariaRepository.obtenerCategorias()

    val documentosFiltrados: List<Documento>
        get() = documentos.filter { it.titulo.contains(busqueda, ignoreCase = true) }

    init {
        viewModelScope.launch { authRepository.crearUsuarioDePrueba() }
    }

    fun iniciarSesion(correo: String, clave: String, alTerminar: (String?) -> Unit) {
        if (correo.isBlank() || clave.isBlank()) {
            alTerminar("Completa todos los campos")
            return
        }
        viewModelScope.launch {
            val usuario = authRepository.login(correo.trim().lowercase(), clave)
            usuarioActual = usuario
            alTerminar(if (usuario == null) "Correo o contraseña incorrectos" else null)
        }
    }

    fun registrar(
        nombre: String, rut: String, correo: String,
        clave: String, confirmar: String,
        alTerminar: (String?) -> Unit
    ) {
        val error = validarRegistro(nombre, rut, correo, clave, confirmar)
        if (error != null) {
            alTerminar(error)
            return
        }
        val nuevo = Usuario(
            correo = correo.trim().lowercase(), nombre = nombre.trim(),
            rut = rut.trim(), clave = clave
        )
        viewModelScope.launch {
            if (authRepository.registrar(nuevo)) {
                usuarioActual = nuevo
                alTerminar(null)
            } else {
                alTerminar("Ese correo ya está registrado")
            }
        }
    }

    private fun validarRegistro(
        nombre: String, rut: String, correo: String, clave: String, confirmar: String
    ): String? = when {
        nombre.isBlank() || rut.isBlank() || correo.isBlank() || clave.isBlank() ->
            "Completa todos los campos"
        !formatoRut.matches(rut.trim()) -> "El RUT no es válido (ej: 12.345.678-9)"
        !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() -> "El correo no es válido"
        clave.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
        clave != confirmar -> "Las contraseñas no coinciden"
        else -> null
    }

    fun cerrarSesion() {
        usuarioActual = null
        fotoPerfil = null
        busqueda = ""
    }

    fun cambiarFoto(foto: Bitmap) {
        fotoPerfil = foto
    }

    fun cambiarBusqueda(texto: String) {
        busqueda = texto
    }

    fun firmarDocumento(id: Int) {
        documentos = documentos.map { if (it.id == id) it.copy(firmado = true) else it }
    }
}