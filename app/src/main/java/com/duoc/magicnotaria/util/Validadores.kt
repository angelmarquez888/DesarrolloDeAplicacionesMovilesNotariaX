package com.duoc.magicnotaria.util

object Validadores {

    private val formatoRut = Regex("""^\d{1,2}\.?\d{3}\.?\d{3}-[\dkK]$""")
    private val formatoCorreo = Regex("""^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$""")

    fun validarRegistro(
        nombre: String, rut: String, correo: String, clave: String, confirmar: String
    ): String? = when {
        nombre.isBlank() || rut.isBlank() || correo.isBlank() || clave.isBlank() ->
            "Completa todos los campos"
        !formatoRut.matches(rut.trim()) -> "El RUT no es válido (ej: 12.345.678-9)"
        !formatoCorreo.matches(correo.trim()) -> "El correo no es válido"
        clave.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
        clave != confirmar -> "Las contraseñas no coinciden"
        else -> null
    }
}