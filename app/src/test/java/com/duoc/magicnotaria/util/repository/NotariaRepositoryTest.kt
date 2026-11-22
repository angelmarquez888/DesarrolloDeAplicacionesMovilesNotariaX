package com.duoc.magicnotaria.util.repository

import com.duoc.magicnotaria.model.Cita
import com.duoc.magicnotaria.model.Documento
import com.duoc.magicnotaria.model.NuevaCita
import com.duoc.magicnotaria.model.NuevoDocumento
import com.duoc.magicnotaria.model.Tramite
import com.duoc.magicnotaria.repository.NotariaApi
import com.duoc.magicnotaria.repository.NotariaRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotariaRepositoryTest {

    private val apiFalsa = ApiFalsa()
    private val repositorio = NotariaRepository(apiFalsa)

    @Test
    fun `obtenerCategorias agrupa los tramites por categoria`() {
        apiFalsa.tramites = listOf(
            Tramite(1, "Certificaciones", "Declaración Jurada"),
            Tramite(2, "Vehículos", "Transferencia de Vehículos"),
            Tramite(3, "Certificaciones", "Autenticación de Firmas")
        )

        val categorias = runBlocking { repositorio.obtenerCategorias() }

        assertEquals(2, categorias.size)
        val certificaciones = categorias.first { it.titulo == "Certificaciones" }
        assertEquals(listOf("Declaración Jurada", "Autenticación de Firmas"), certificaciones.tramites)
    }

    @Test
    fun `iniciarTramite envia a la API un documento sin firmar del usuario`() {
        val creado = runBlocking { repositorio.iniciarTramite("Mandato General", "ana@duoc.cl") }

        val enviado = requireNotNull(apiFalsa.documentoEnviado)
        assertEquals("Mandato General", enviado.titulo)
        assertEquals("ana@duoc.cl", enviado.correoUsuario)
        assertFalse(enviado.firmado)
        assertEquals("Mandato General", creado.titulo)
    }

    @Test
    fun `firmarDocumento pide a la API firmar el documento con su id`() {
        val pendiente = Documento(7, "Poder Notarial Amplio", "En proceso", false, "ana@duoc.cl")

        val firmado = runBlocking { repositorio.firmarDocumento(pendiente) }

        assertEquals(7, apiFalsa.idFirmado)
        assertTrue(firmado.firmado)
    }
}

private class ApiFalsa : NotariaApi {
    var tramites: List<Tramite> = emptyList()
    var documentoEnviado: NuevoDocumento? = null
    var idFirmado: Int? = null

    override suspend fun obtenerTramites() = tramites

    override suspend fun obtenerDocumentos(correoUsuario: String) = emptyList<Documento>()

    override suspend fun crearDocumento(documento: NuevoDocumento): Documento {
        documentoEnviado = documento
        return Documento(1, documento.titulo, documento.detalle, documento.firmado, documento.correoUsuario)
    }

    override suspend fun firmarDocumento(id: Int): Documento {
        idFirmado = id
        return Documento(id, "Documento", "Detalle", true, "ana@duoc.cl")
    }

    override suspend fun eliminarDocumento(id: Int) {}

    override suspend fun obtenerCitas(correoUsuario: String) = emptyList<Cita>()

    override suspend fun crearCita(cita: NuevaCita) =
        Cita(1, cita.motivo, cita.fecha, cita.hora, cita.correoUsuario)

    override suspend fun eliminarCita(id: Int) {}
}