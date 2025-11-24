package com.example.resetandreplay.data.remote

import com.example.resetandreplay.data.remote.dto.CategoriaDto
import com.example.resetandreplay.data.remote.dto.PlataformaDto
import com.example.resetandreplay.data.remote.dto.ProductDto
import com.example.resetandreplay.data.remote.dto.EstadoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InventoryApiService {

    @GET("productos")
    suspend fun getAllProducts(): Response<List<ProductDto>>

    @GET("productos/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<ProductDto>

    @GET("categorias")
    suspend fun getAllCategorias(): Response<List<CategoriaDto>>

    @GET("plataformas")
    suspend fun getAllPlataformas(): Response<List<PlataformaDto>>

    @GET("estados")
    suspend fun getAllEstados(): Response<List<EstadoDto>>

    @POST("productos")
    suspend fun createProduct(@Body product: ProductDto): Response<ProductDto>

    @DELETE("productos/{id}")
    suspend fun deleteProductById(@Path("id") id: Int): Response<Void> // Response<Void> porque no devuelve cuerpo
}