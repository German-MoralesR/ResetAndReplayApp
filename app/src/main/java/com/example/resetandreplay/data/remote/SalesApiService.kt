package com.example.resetandreplay.data.remote

import com.example.resetandreplay.data.remote.dto.CompraDto
import com.example.resetandreplay.data.remote.dto.CompraRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SalesApiService {
    @POST("compras")
    suspend fun createCompra(@Body compraRequest: CompraRequest): Response<Map<String, String>>

    @GET("compras/usuario/{id}")
    suspend fun getComprasByUsuario(@Path("id") idUsuario: Int): Response<List<CompraDto>>
}