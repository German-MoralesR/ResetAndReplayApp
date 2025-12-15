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
import okhttp3.MultipartBody // <-- ¡AÑADIR IMPORT!
import okhttp3.RequestBody // <-- ¡AÑADIR IMPORT!
import retrofit2.http.Multipart // <-- ¡AÑADIR IMPORT!
import retrofit2.http.PUT // <-- ¡AÑADIR IMPORT!
import retrofit2.http.Part

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

    @Multipart // Indicamos que es una petición multipart
    @POST("productos")
    suspend fun createProduct(
        @Part("producto") product: RequestBody, // El DTO irá como JSON en esta parte
        @Part file: MultipartBody.Part?       // El archivo de imagen irá en esta parte (opcional)
    ): Response<ProductDto>

    // --- MÉTODO DE ACTUALIZACIÓN CORREGIDO ---
    @Multipart
    @PUT("productos/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Part("producto") product: RequestBody,
        @Part file: MultipartBody.Part?
    ): Response<ProductDto>

    @DELETE("productos/{id}")
    suspend fun deleteProductById(@Path("id") id: Int): Response<Void>
}