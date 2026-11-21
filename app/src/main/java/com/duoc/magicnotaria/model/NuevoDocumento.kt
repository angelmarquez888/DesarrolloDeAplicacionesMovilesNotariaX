package com.duoc.magicnotaria.model

data class NuevoDocumento(
    val titulo: String,
    val detalle: String,
    val firmado: Boolean,
    val correoUsuario: String
)
