package com.example.resetandreplay.data.repository

import com.example.resetandreplay.data.remote.ReviewsApiService
import com.example.resetandreplay.data.remote.ReviewsRetrofitClient
import com.example.resetandreplay.data.remote.dto.ResenaDto
import com.example.resetandreplay.data.remote.dto.ResenaRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class ReviewRepository {

    private val apiService: ReviewsApiService = ReviewsRetrofitClient.create(ReviewsApiService::class.java)

    // Obtiene las reseñas para un producto específico
    fun getReviewsForProduct(productId: Int): Flow<List<ResenaDto>> = flow {
        try {
            val response = apiService.getResenasByProducto(productId)
            if (response.isSuccessful) {
                emit(response.body() ?: emptyList())
            } else {
                throw IOException("Error al cargar las reseñas: ${response.code()}")
            }
        } catch (e: Exception) {
            throw IOException("No se pudieron cargar las reseñas: ${e.message}", e)
        }
    }

    // Crea una nueva reseña
    suspend fun createReview(resenaRequest: ResenaRequest): Result<Unit> {
        return try {
            val response = apiService.createResena(resenaRequest)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(IOException("Error al publicar la reseña: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("No se pudo publicar la reseña: ${e.message}", e))
        }
    }
}