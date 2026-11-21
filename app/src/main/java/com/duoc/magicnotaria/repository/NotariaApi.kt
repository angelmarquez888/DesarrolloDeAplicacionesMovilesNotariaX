package com.duoc.magicnotaria.repository

import com.duoc.magicnotaria.model.Documento
import com.duoc.magicnotaria.model.NuevoDocumento
import com.duoc.magicnotaria.model.Tramite
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NotariaApi {

    @GET("tramites")
    suspend fun obtenerTramites(): List<Tramite>

    @GET("documentos")
    suspend fun obtenerDocumentos(@Query("correoUsuario") correoUsuario: String): List<Documento>

    @POST("documentos")
    suspend fun crearDocumento(@Body documento: NuevoDocumento): Documento

    @PUT("documentos/{id}/firmar")
    suspend fun firmarDocumento(@Path("id") id: Int): Documento
}