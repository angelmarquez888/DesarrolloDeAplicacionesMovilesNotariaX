package com.duoc.magicnotaria.viewmodel

import android.app.Application
import android.graphics.Bitmap
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
import com.duoc.magicnotaria.model.Cita
import com.duoc.magicnotaria.util.NotificadorNotaria
import com.duoc.magicnotaria.util.Validadores

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AppDatabase.obtener(application).let {
        AuthRepository(it.usuarioDao(), it.sesionDao())
    }
    private val notariaRepository = NotariaRepository()

    private val notificador = NotificadorNotaria(application)

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

    var citas by mutableStateOf<List<Cita>>(emptyList())
        private set
    var busqueda by mutableStateOf("")
        private set
    var cargando by mutableStateOf(false)
        private set
    var mensajeError by mutableStateOf<String?>(null)
        private set

    val documentosFiltrados: List<Documento>
        get() = documentos.filter { it.titulo.contains(busqueda, ignoreCase = true) }
    val documentosPendientes: List<Documento>
        get() = documentos.filter { !it.firmado }

    init {
        viewModelScope.launch {
            authRepository.crearUsuarioDePrueba()
            usuarioActual = authRepository.obtenerSesionActiva()
            cargandoSesion = false
            cargarDatosDelUsuario()
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
            if (usuario != null) cargarDatosDelUsuario()
            alTerminar(if (usuario == null) "Correo o contraseña incorrectos" else null)
        }
    }

    fun registrar(
        nombre: String, rut: String, correo: String,
        clave: String, confirmar: String,
        alTerminar: (String?) -> Unit
    ) {
        val error = Validadores.validarRegistro(nombre, rut, correo, clave, confirmar)
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
                cargarDatosDelUsuario()
                alTerminar(null)
            } else {
                alTerminar("Ese correo ya está registrado")
            }
        }
    }


    fun cerrarSesion() {
        viewModelScope.launch { authRepository.cerrarSesion() }
        usuarioActual = null
        fotoPerfil = null
        busqueda = ""
        documentos = emptyList()
        citas = emptyList()
    }

    fun cambiarFoto(foto: Bitmap) {
        fotoPerfil = foto
    }

    fun cambiarBusqueda(texto: String) {
        busqueda = texto
    }


    fun iniciarTramite(nombreTramite: String, alTerminar: (String?) -> Unit) {
        val correo = usuarioActual?.correo ?: return
        viewModelScope.launch {
            ejecutarConRed {

                val creado = notariaRepository.iniciarTramite(nombreTramite, correo)
                documentos = documentos + creado
            }
            if (mensajeError == null) {
                notificador.mostrar("Trámite iniciado", "«$nombreTramite» quedó pendiente de firma")
            }
            alTerminar(mensajeError)
        }
    }


    fun firmarDocumento(documento: Documento, alTerminar: (String?) -> Unit) {
        viewModelScope.launch {
            ejecutarConRed {
                val firmado = notariaRepository.firmarDocumento(documento)
                documentos = documentos.map { if (it.id == firmado.id) firmado else it }
                if (mensajeError == null) {
                    notificador.mostrar("Documento firmado", "«${documento.titulo}» fue firmado correctamente")
                }
            }
            alTerminar(mensajeError)
        }
    }
    fun eliminarDocumento(documento: Documento, alTerminar: (String?) -> Unit) {
        viewModelScope.launch {
            ejecutarConRed {
                notariaRepository.eliminarDocumento(documento.id)
                documentos = documentos.filter { it.id != documento.id }
            }
            alTerminar(mensajeError)
        }
    }

    fun agendarCita(motivo: String, fecha: String, hora: String, alTerminar: (String?) -> Unit) {
        if (motivo.isBlank() || fecha.isBlank() || hora.isBlank()) {
            alTerminar("Completa el motivo, la fecha y la hora")
            return
        }
        val correo = usuarioActual?.correo ?: return
        viewModelScope.launch {
            ejecutarConRed {
                val creada = notariaRepository.agendarCita(motivo.trim(), fecha, hora, correo)
                citas = citas + creada
                if (mensajeError == null) {
                    notificador.mostrar("Cita agendada", "$motivo: $fecha a las $hora")
                }
            }
            alTerminar(mensajeError)
        }
    }

    fun cancelarCita(cita: Cita, alTerminar: (String?) -> Unit) {
        viewModelScope.launch {
            ejecutarConRed {
                notariaRepository.cancelarCita(cita.id)
                citas = citas.filter { it.id != cita.id }
            }
            alTerminar(mensajeError)
        }
    }
    fun avisarFirmasPendientes() {
        val cantidad = documentosPendientes.size
        val mensaje = when (cantidad) {
            0 -> "No tienes firmas pendientes"
            1 -> "Tienes 1 documento esperando tu firma"
            else -> "Tienes $cantidad documentos esperando tu firma"
        }
        notificador.mostrar("Firmas pendientes", mensaje)
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            ejecutarConRed { categorias = notariaRepository.obtenerCategorias() }
        }
    }

    private fun cargarDatosDelUsuario() {
        val correo = usuarioActual?.correo ?: return
        viewModelScope.launch {
            ejecutarConRed { documentos = notariaRepository.obtenerDocumentos(correo) }
            ejecutarConRed { citas = notariaRepository.obtenerCitas(correo) }
        }
    }

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