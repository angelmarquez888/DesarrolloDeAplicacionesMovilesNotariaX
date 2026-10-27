package com.duoc.magicnotaria.model

data class Usuario(
    val nombre: String,
    val rut: String,
    val correo: String,
    val clave: String,
    val direccion: String = "Sin dirección registrada"
)
