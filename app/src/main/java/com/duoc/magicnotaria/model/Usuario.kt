package com.duoc.magicnotaria.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey val correo: String,
    val nombre: String,
    val rut: String,
    val clave: String,
    val direccion: String = "Sin dirección registrada"
)
