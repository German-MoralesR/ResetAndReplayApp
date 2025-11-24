package com.example.resetandreplay.data.remote.dto

// Representa una reseña recibida desde el microservicio
data class ResenaDto(
    val id: Long,
    val idProducto: Int,
    val idUsuario: Int,
    val texto: String,
    val calificacion: Int,
    val fecha: String // La recibimos como String
)

// Representa el cuerpo de la petición para crear una nueva reseña
data class ResenaRequest(
    val idProducto: Int,
    val idUsuario: Int,
    val texto: String,
    val calificacion: Int
)