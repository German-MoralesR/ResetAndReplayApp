package com.example.resetandreplay.data.repository

import com.example.resetandreplay.data.local.user.UserDao
import com.example.resetandreplay.data.local.user.UserEntity
import com.example.resetandreplay.data.remote.ApiService
import com.example.resetandreplay.data.remote.RetrofitClient
import com.example.resetandreplay.data.remote.dto.LoginRequest
import com.example.resetandreplay.data.remote.dto.RegisterRequest
import com.example.resetandreplay.data.remote.dto.ResetPasswordRequest
import com.example.resetandreplay.data.remote.dto.VerifyAnswerDto

class UserRepository(
    private val userDao: UserDao,
    private val apiService: ApiService = RetrofitClient.instance // Parámetro opcional
) {

    suspend fun login(email: String, password: String): Result<UserEntity> {
        try {
            val loginRequest = LoginRequest(correo = email, password = password)
            val response = apiService.login(loginRequest)

            if (response.isSuccessful) {
                // ---- ESTA ES LA PARTE MODIFICADA ----
                val loginResponse = response.body()
                if (loginResponse != null) {
                    // 1. Obtenemos el objeto 'usuario' de la respuesta
                    val userDto = loginResponse.usuario

                    // 2. Convertimos el DTO a la Entidad local
                    val userEntity = UserEntity(
                        id = userDto.id_usuario.toLong(),
                        name = userDto.nombre,
                        email = userDto.correo,
                        phone = userDto.telefono ?: "",
                        password = "", // La contraseña no viaja, esto es correcto
                        // Hacemos una comprobación segura por si el rol es nulo
                        isAdmin = userDto.rol?.nombre.equals("ADMIN", ignoreCase = true)
                    )
                    return Result.success(userEntity)
                } else {
                    return Result.failure(Exception("Respuesta vacía del servidor."))
                }
            } else {
                return Result.failure(Exception("Email o contraseña incorrectos."))
            }
        } catch (e: Exception) {
            // Este es el error que veías. Ahora tendrá más sentido si ocurre.
            return Result.failure(Exception("Error de red o de procesamiento: ${e.message}"))
        }
    }

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        securityQuestion: String,
        securityAnswer: String
    ): Result<Long> {
        try {
            // 1. Creamos el objeto DTO con los datos del formulario de registro
            // El campo 'phone' no está en nuestro DTO, el microservicio no lo pide por ahora.
            val registerRequest = RegisterRequest(
                nombre = name,
                correo = email,
                telefono = phone,
                password = password,
                securityQuestion = securityQuestion,
                securityAnswer = securityAnswer
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

    suspend fun getSecurityQuestion(email: String): Result<String> {
        return try {
            val response = apiService.getSecurityQuestion(email)

            if (response.isSuccessful) {
                val body = response.body()
                val question = body?.get("question")
                if (question != null) {
                    Result.success(question)
                } else {
                    // Esto ocurre si la API da un 200 OK pero el cuerpo es inesperado
                    Result.failure(Exception("Respuesta del servidor inválida."))
                }
            } else {
                // ----> ESTA ES LA LÓGICA DE ERROR MEJORADA <----
                var errorMessage = "Error desconocido del servidor." // Mensaje por defecto
                val errorBody = response.errorBody()?.string() // Leemos el cuerpo del error UNA SOLA VEZ

                if (!errorBody.isNullOrBlank()) {
                    try {
                        // Intentamos parsear el JSON de error que envía el microservicio
                        val jsonObject = org.json.JSONObject(errorBody)
                        // Buscamos la clave "error" que definimos en el backend
                        if (jsonObject.has("error")) {
                            errorMessage = jsonObject.getString("error")
                        } else {
                            errorMessage = "Error ${response.code()}: No se encontró el detalle del error."
                        }
                    } catch (e: org.json.JSONException) {
                        // Si el errorBody no es un JSON válido, usamos el texto plano
                        errorMessage = errorBody
                    }
                } else {
                    // Si no hay cuerpo de error, usamos un mensaje genérico basado en el código HTTP
                    errorMessage = when (response.code()) {
                        404 -> "El usuario no fue encontrado."
                        else -> "Error del servidor (código: ${response.code()})."
                    }
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("Error de red: No se pudo conectar al servidor."))
        } catch (e: Exception) {
            Result.failure(Exception("Error inesperado: ${e.message}"))
        }
    }

    suspend fun verifySecurityAnswer(email: String, answer: String): Result<Boolean> {
        return try {
            val request = VerifyAnswerDto(correo = email, answer = answer)
            val response = apiService.verifySecurityAnswer(request)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("La respuesta es incorrecta."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
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
