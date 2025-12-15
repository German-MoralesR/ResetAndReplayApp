package com.example.resetandreplay.data.remote.dto

// Este DTO representa el cuerpo JSON que se enviará al endpoint /verify-answer
data class VerifyAnswerDto(
    val correo: String,
    val answer: String
)