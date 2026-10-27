package com.duoc.magicnotaria.model

data class Documento(
    val id: Int,
    val titulo: String,
    val detalle: String,
    val firmado: Boolean
)