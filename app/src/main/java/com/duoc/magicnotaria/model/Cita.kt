package com.duoc.magicnotaria.model

data class Cita(
    val id: Int,
    val motivo: String,
    val fecha: String,
    val hora: String,
    val correoUsuario: String
)