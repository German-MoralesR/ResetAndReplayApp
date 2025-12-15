package com.example.resetandreplay.data.remote.dto

// Este DTO representa la respuesta completa del endpoint de login
data class LoginResponseDto(
    val token: String,
    val usuario: UserDto
)