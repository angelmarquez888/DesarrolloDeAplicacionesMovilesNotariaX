package com.duoc.magicnotaria.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadoresTest {

    private val nombreValido = "Ana Márquez"
    private val rutValido = "12.345.678-9"
    private val correoValido = "ana@duoc.cl"
    private val claveValida = "123456"

    @Test
    fun `registro con datos correctos no devuelve error`() {
        val error = Validadores.validarRegistro(nombreValido, rutValido, correoValido, claveValida, claveValida)

        assertNull(error)
    }

    @Test
    fun `registro con un campo vacio pide completar todos los campos`() {
        val error = Validadores.validarRegistro("", rutValido, correoValido, claveValida, claveValida)

        assertEquals("Completa todos los campos", error)
    }

    @Test
    fun `rut sin formato valido devuelve error de rut`() {
        val error = Validadores.validarRegistro(nombreValido, "123", correoValido, claveValida, claveValida)

        assertEquals("El RUT no es válido (ej: 12.345.678-9)", error)
    }

    @Test
    fun `rut con digito verificador K y sin puntos es valido`() {
        val errorConPuntos = Validadores.validarRegistro(nombreValido, "9.876.543-K", correoValido, claveValida, claveValida)
        val errorSinPuntos = Validadores.validarRegistro(nombreValido, "12345678-9", correoValido, claveValida, claveValida)

        assertNull(errorConPuntos)
        assertNull(errorSinPuntos)
    }

    @Test
    fun `correo sin dominio devuelve error de correo`() {
        val error = Validadores.validarRegistro(nombreValido, rutValido, "ana@", claveValida, claveValida)

        assertEquals("El correo no es válido", error)
    }

    @Test
    fun `contrasena de menos de seis caracteres devuelve error`() {
        val error = Validadores.validarRegistro(nombreValido, rutValido, correoValido, "123", "123")

        assertEquals("La contraseña debe tener al menos 6 caracteres", error)
    }

    @Test
    fun `contrasenas distintas devuelve error de confirmacion`() {
        val error = Validadores.validarRegistro(nombreValido, rutValido, correoValido, "123456", "654321")

        assertEquals("Las contraseñas no coinciden", error)
    }
}