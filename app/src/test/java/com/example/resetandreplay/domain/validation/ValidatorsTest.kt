package com.example.resetandreplay.domain.validation

import org.junit.Assert.*
import org.junit.Test

class ValidatorsTest {

    // --- Pruebas para validateEmail ---

    @Test
    fun `validateEmail con email correcto no devuelve error`() {
        // Ejecutamos la función a probar
        val error = validateEmail(email = "test@valido.com")
        // Verificamos que el resultado es el esperado (null)
        assertNull("Un email válido no debería retornar error", error)
    }

    @Test
    fun validateEmailEmptyOrNull() {
        val error = validateEmail(email = "")
        assertEquals("El email es obligatorio", error)
    }

    @Test
    fun `validateEmail con formato inválido devuelve mensaje de error`() {
        val error = validateEmail(email = "email-invalido")
        assertEquals("Formato de email inválido", error)
    }

    // --- Pruebas para validateStrongPassword ---

    @Test
    fun `validateStrongPassword con contraseña correcta no devuelve error`() {
        val error = validateStrongPassword("Admin123!")
        assertNull("Una contraseña válida no debería retornar error", error)
    }

    @Test
    fun `validateStrongPassword sin mayúscula devuelve mensaje de error`() {
        val error = validateStrongPassword("admin123!")
        assertEquals("Debe incluir una mayúscula", error)
    }

    @Test
    fun `validateStrongPassword sin número devuelve mensaje de error`() {
        val error = validateStrongPassword("AdminPass!")
        assertEquals("Debe incluir un número", error)
    }
}