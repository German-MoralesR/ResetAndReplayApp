package com.example.resetandreplay.data.repository

import com.example.resetandreplay.data.local.user.UserDao
import com.example.resetandreplay.data.local.user.UserEntity
import com.example.resetandreplay.data.remote.RetrofitClient
import com.example.resetandreplay.data.remote.dto.LoginRequest
import com.example.resetandreplay.data.remote.dto.RegisterRequest
import com.example.resetandreplay.data.remote.dto.ResetPasswordRequest

class UserRepository(
    private val userDao: UserDao
) {
    // Obtenemos una instancia de nuestro cliente de API
    private val apiService = RetrofitClient.instance
    suspend fun login(email: String, password: String): Result<UserEntity> {
        try {
            // 1. Creamos el objeto que enviaremos en el body
            val loginRequest = LoginRequest(correo = email, password = password)

            // 2. Llamamos al nuevo endpoint POST
            val response = apiService.login(loginRequest)

            if (response.isSuccessful) {
                val userDto = response.body()
                if (userDto != null) {
                    // Convertimos el DTO a la Entidad local
                    val userEntity = UserEntity(
                        id = userDto.id_usuario.toLong(),
                        name = userDto.nombre,
                        email = userDto.correo,
                        phone = "",
                        password = "",
                        isAdmin = userDto.rol.nombre.equals("ADMIN", ignoreCase = true)
                    )
                    return Result.success(userEntity)
                } else {
                    return Result.failure(Exception("Respuesta vacía del servidor."))
                }
            } else {
                // Si el código es 401 (Unauthorized) o cualquier otro error
                return Result.failure(Exception("Email o contraseña incorrectos."))
            }
        } catch (e: Exception) {
            return Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    suspend fun register(name: String, email: String, phone: String, password: String): Result<Long> {
        try {
            // 1. Creamos el objeto DTO con los datos del formulario de registro
            // El campo 'phone' no está en nuestro DTO, el microservicio no lo pide por ahora.
            val registerRequest = RegisterRequest(
                nombre = name,
                correo = email,
                telefono = phone,
                password = password
            )

            // 2. Llamamos al nuevo endpoint de la API
            val response = apiService.registerUser(registerRequest)

            // 3. Procesamos la respuesta del servidor
            if (response.isSuccessful) {
                val newUserDto = response.body()
                // Si el registro es exitoso, devolvemos el ID del nuevo usuario.
                // Si el DTO es nulo, usamos -1L como señal de un problema inesperado.
                return Result.success(newUserDto?.id_usuario?.toLong() ?: -1L)
            } else {
                // Manejo de errores específicos del servidor
                // El código 409 Conflict es común para "usuario ya existe"
                if (response.code() == 409) {
                    return Result.failure(Exception("El correo electrónico ya está registrado."))
                }
                // Para otros errores del servidor (ej. 400 Bad Request, 500 Internal Server Error)
                return Result.failure(Exception("Error en el registro (código: ${response.code()})."))
            }
        } catch (e: Exception) {
            // Error de red (sin conexión, servidor caído, etc.)
            return Result.failure(Exception("Error de red: ${e.message}"))
        }
    }


    suspend fun getUserByEmail(email: String): Result<UserEntity> {
        try {
            // Hacemos la llamada a la API que ya teníamos definida en ApiService.kt
            val response = apiService.getUserByEmail(email)

            if (response.isSuccessful) {
                val userDto = response.body()
                if (userDto != null) {
                    // Convertimos el DTO que viene del microservicio a la entidad local
                    // que la app entiende.
                    val userEntity = UserEntity(
                        id = userDto.id_usuario.toLong(),
                        name = userDto.nombre,
                        email = userDto.correo,
                        phone = userDto.telefono ?: "",
                        password = "", // Nunca guardamos la contraseña
                        isAdmin = userDto.rol.nombre.equals("ADMIN", ignoreCase = true)
                    )
                    return Result.success(userEntity)
                } else {
                    // El servidor respondió OK pero sin datos
                    return Result.failure(Exception("Respuesta de usuario vacía."))
                }
            } else {
                // El servidor devolvió un error (ej. 404 Not Found)
                return Result.failure(Exception("Usuario no encontrado en el servidor (código: ${response.code()})."))
            }
        } catch (e: Exception) {
            // Error de red (sin conexión, servidor caído, etc.)
            return Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    // 1. Nueva función para cambiar la contraseña de un usuario
    suspend fun resetPassword(email: String, newPassword: String): Result<Unit> {
        return try {
            val request = ResetPasswordRequest(correo = email, newPassword = newPassword)
            val response = apiService.resetPassword(request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                // Si el servidor devuelve 404, significa que el usuario no fue encontrado
                Result.failure(Exception("Usuario no encontrado en el servidor."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red al cambiar la contraseña: ${e.message}"))
        }
    }
}
