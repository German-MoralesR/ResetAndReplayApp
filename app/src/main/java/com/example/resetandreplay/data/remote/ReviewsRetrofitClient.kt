package com.example.resetandreplay.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ReviewsRetrofitClient {
    // URL base del microservicio de reseñas.
    private const val BASE_URL = "http://10.0.2.2:8084/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}