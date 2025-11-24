package com.example.resetandreplay.data.remote.dto

data class ResetPasswordRequest(
    val correo: String,
    val newPassword: String
)