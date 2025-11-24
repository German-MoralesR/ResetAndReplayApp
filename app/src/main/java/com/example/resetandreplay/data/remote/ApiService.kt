// com/example/resetandreplay/data/remote/ApiService.kt

package com.example.resetandreplay.data.remote

import com.example.resetandreplay.data.remote.dto.LoginRequest
import com.example.resetandreplay.data.remote.dto.RegisterRequest
import com.example.resetandreplay.data.remote.dto.ResetPasswordRequest
import retrofit2.http.PUT
import com.example.resetandreplay.data.remote.dto.UserDto // Crearemos esto pronto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // Define un endpoint para obtener un usuario por su email
    // La petición será: GET http://<BASE_URL>/usuarios/email/user@example.com
    @GET("usuarios/email/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): Response<UserDto>

    @POST("usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<UserDto>

    @POST("usuarios") // Llama al endpoint POST /usuarios
    suspend fun registerUser(@Body request: RegisterRequest): Response<UserDto> // Recibe un RegisterRequest y espera un UserDto como respuesta

    @PUT("usuarios/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Void>
}
