package com.example.resetandreplay.data.remote.dto

// ¡Importante! Los nombres de los campos DEBEN COINCIDIR con el JSON del microservicio.
data class UserDto(
    val id_usuario: Int,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val foto_perfil: String?,
    val rol: RolDto
)