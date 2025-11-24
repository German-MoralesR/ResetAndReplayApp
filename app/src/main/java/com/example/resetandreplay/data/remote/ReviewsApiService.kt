package com.example.resetandreplay.data.remote

import com.example.resetandreplay.data.remote.dto.ResenaDto
import com.example.resetandreplay.data.remote.dto.ResenaRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewsApiService {

    @GET("resenas/producto/{idProducto}")
    suspend fun getResenasByProducto(@Path("idProducto") idProducto: Int): Response<List<ResenaDto>>

    @POST("resenas")
    suspend fun createResena(@Body resenaRequest: ResenaRequest): Response<ResenaDto>
}