package com.example.resetandreplay.data.remote.dto

// Representa el JSON que enviaremos al microservicio
data class LoginRequest(
    val correo: String,
    val password: String
)