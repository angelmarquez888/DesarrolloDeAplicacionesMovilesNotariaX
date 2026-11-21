package com.duoc.magicnotaria.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.magicnotaria.model.CategoriaTramite
import com.duoc.magicnotaria.model.Documento
import com.duoc.magicnotaria.model.Usuario
import com.duoc.magicnotaria.repository.AppDatabase
import com.duoc.magicnotaria.repository.AuthRepository
import com.duoc.magicnotaria.repository.NotariaRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

private val formatoRut = Regex("""^\d{1,2}\.?\d{3}\.?\d{3}-[\dkK]$""")

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AppDatabase.obtener(application).let {
        AuthRepository(it.usuarioDao(), it.sesionDao())
    }
    private val notariaRepository = NotariaRepository()

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set
    var cargandoSesion by mutableStateOf(true)
        private set
    var fotoPerfil by mutableStateOf<Bitmap?>(null)
        private set
    var categorias by mutableStateOf<List<CategoriaTramite>>(emptyList())
        private set
    var documentos by mutableStateOf<List<Documento>>(emptyList())
        private set
    var busqueda by mutableStateOf("")
        private set
    var cargando by mutableStateOf(false)
        private set
    var mensajeError by mutableStateOf<String?>(null)
        private set

    val documentosFiltrados: List<Documento>
        get() = documentos.filter { it.titulo.contains(busqueda, ignoreCase = true) }

    init {
        viewModelScope.launch {
            authRepository.crearUsuarioDePrueba()
            usuarioActual = authRepository.obtenerSesionActiva()
            cargandoSesion = false
            cargarDocumentos()
        }
        cargarCategorias()
    }

    fun iniciarSesion(correo: String, clave: String, alTerminar: (String?) -> Unit) {
        if (correo.isBlank() || clave.isBlank()) {
            alTerminar("Completa todos los campos")
            return
        }
        viewModelScope.launch {
            val usuario = authRepository.login(correo.trim().lowercase(), clave)
            usuarioActual = usuario
            if (usuario != null) cargarDocumentos()
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
                cargarDocumentos()
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
        viewModelScope.launch { authRepository.cerrarSesion() }
        usuarioActual = null
        fotoPerfil = null
        busqueda = ""
        documentos = emptyList()
    }

    fun cambiarFoto(foto: Bitmap) {
        fotoPerfil = foto
    }

    fun cambiarBusqueda(texto: String) {
        busqueda = texto
    }

    /** Crea un documento pendiente en la API a partir del trámite elegido. alTerminar recibe null si todo salió bien. */
    fun iniciarTramite(nombreTramite: String, alTerminar: (String?) -> Unit) {
        val correo = usuarioActual?.correo ?: return
        viewModelScope.launch {
            ejecutarConRed {
                val creado = notariaRepository.iniciarTramite(nombreTramite, correo)
                documentos = documentos + creado
            }
            alTerminar(mensajeError)
        }
    }

    /** Firma el documento en la API y actualiza la lista local. alTerminar recibe null si todo salió bien. */
    fun firmarDocumento(documento: Documento, alTerminar: (String?) -> Unit) {
        viewModelScope.launch {
            ejecutarConRed {
                val firmado = notariaRepository.firmarDocumento(documento)
                documentos = documentos.map { if (it.id == firmado.id) firmado else it }
            }
            alTerminar(mensajeError)
        }
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            ejecutarConRed { categorias = notariaRepository.obtenerCategorias() }
        }
    }

    private fun cargarDocumentos() {
        val correo = usuarioActual?.correo ?: return
        viewModelScope.launch {
            ejecutarConRed { documentos = notariaRepository.obtenerDocumentos(correo) }
        }
    }

    /** Ejecuta una llamada a la API mostrando "cargando" y traduciendo los fallos de red a un mensaje legible. */
    private suspend fun ejecutarConRed(llamada: suspend () -> Unit) {
        cargando = true
        mensajeError = null
        try {
            llamada()
        } catch (error: IOException) {
            mensajeError = "Sin conexión. Revisa tu internet e inténtalo de nuevo"
        } catch (error: HttpException) {
            mensajeError = "El servidor respondió con un error (${error.code()})"
        } finally {
            cargando = false
        }
    }
}