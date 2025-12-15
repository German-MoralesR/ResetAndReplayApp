package com.example.resetandreplay.data.remote.dto

// Representa el cuerpo JSON que enviaremos al endpoint de registro del microservicio
data class RegisterRequest(
    val nombre: String,
    val correo: String,
    val telefono: String,
    val password: String,
    val securityQuestion: String,
    val securityAnswer: String
    // No necesitamos enviar foto_perfil ni rol al registrar.
    // El microservicio asignará un rol por defecto.
)