package com.duoc.magicnotaria.repository

import com.duoc.magicnotaria.model.CategoriaTramite
import com.duoc.magicnotaria.model.Documento
import com.duoc.magicnotaria.model.NuevoDocumento


class NotariaRepository(
    private val api: NotariaApi = RetrofitClient.notariaApi
) {

    suspend fun obtenerCategorias(): List<CategoriaTramite> =
        api.obtenerTramites()
            .groupBy { it.categoria }
            .map { (titulo, tramites) -> CategoriaTramite(titulo, tramites.map { it.nombre }) }

    suspend fun obtenerDocumentos(correoUsuario: String): List<Documento> =
        api.obtenerDocumentos(correoUsuario)

    suspend fun iniciarTramite(nombreTramite: String, correoUsuario: String): Documento =
        api.crearDocumento(
            NuevoDocumento(
                titulo = nombreTramite,
                detalle = "Trámite iniciado desde la app • En proceso",
                firmado = false,
                correoUsuario = correoUsuario
            )
        )

    suspend fun firmarDocumento(documento: Documento): Documento =
        api.firmarDocumento(documento.id)
}

