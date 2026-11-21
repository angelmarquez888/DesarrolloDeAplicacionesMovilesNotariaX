package com.duoc.magicnotaria.repository

import com.duoc.magicnotaria.model.Cita
import com.duoc.magicnotaria.model.Documento
import com.duoc.magicnotaria.model.NuevaCita
import com.duoc.magicnotaria.model.NuevoDocumento
import com.duoc.magicnotaria.model.Tramite
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    @DELETE("documentos/{id}")
    suspend fun eliminarDocumento(@Path("id") id: Int)

    @GET("citas")
    suspend fun obtenerCitas(@Query("correoUsuario") correoUsuario: String): List<Cita>

    @POST("citas")
    suspend fun crearCita(@Body cita: NuevaCita): Cita

    @DELETE("citas/{id}")
    suspend fun eliminarCita(@Path("id") id: Int)
}