package com.example.resetandreplay.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // URL base del microservicio.
    // Si usas un emulador de Android, 'localhost' se refiere al propio emulador.
    // Debes usar '10.0.2.2' para conectarte al localhost de tu PC.
    private const val BASE_URL = "http://10.0.2.2:8081/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}