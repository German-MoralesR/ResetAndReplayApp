package com.example.resetandreplay.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object InventoryRetrofitClient {

    // URL base del microservicio de inventario. ¡OJO AL PUERTO 8082!
    private const val BASE_URL = "http://10.0.2.2:8082/"

    // Creamos una instancia de Retrofit específica para el servicio de inventario
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Función para crear el servicio de la API
    fun <T> create(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}