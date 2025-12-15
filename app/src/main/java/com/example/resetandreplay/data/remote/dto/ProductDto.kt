package com.example.resetandreplay.data.remote.dto

// Representa un producto tal como lo envía el microservicio
data class ProductDto(
    val id_producto: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val sku: String,
    val categoria: CategoriaDto,
    val plataforma: PlataformaDto,
    val estado: EstadoDto,
    val fotos: List<FotoDto>,
    val photoUrl: String?
)

data class CategoriaDto(
    val id_cat: Int,
    val nombre: String
)

data class PlataformaDto(
    val id_plat: Int,
    val nombre: String
)

data class EstadoDto(
    val id_estado: Int,
    val nombre: String
)

data class FotoDto(
    val id_fo: Int,
    val nombre: String,
    val foto: String // URL de la foto
)