package com.example.resetandreplay.data.remote.dto

// Representa el cuerpo de la petición para crear una nueva compra
data class CompraRequest(
    val id_usuario: Int,
    val detalles: List<DetalleRequest>
)

data class DetalleRequest(
    val id_producto: Int,
    val cantidad: Int,
    val precio: Double
)

// Representa una compra recibida desde el historial
data class CompraDto(
    val id_compra: Int,
    val id_usuario: Int,
    val fecha: String, // Recibimos la fecha como String desde el JSON
    val total: Double,
    val detalles: List<DetalleDto>
)

data class DetalleDto(
    val id_detalle: Int,
    val id_producto: Int,
    val cantidad: Int,
    val precio: Double,
    val subtotal: Double
)