package com.example.resetandreplay.data.repository

import com.example.resetandreplay.data.remote.SalesApiService
import com.example.resetandreplay.data.remote.SalesRetrofitClient
import com.example.resetandreplay.data.remote.dto.CompraDto
import com.example.resetandreplay.data.remote.dto.CompraRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class PurchaseRepository {
    // Creamos la instancia del nuevo servicio de API
    private val apiService: SalesApiService = SalesRetrofitClient.create(SalesApiService::class.java)

    // Función para obtener el historial de compras de un usuario
    fun getPurchasesForUser(userId: Long): Flow<List<CompraDto>> = flow {
        try {
            val response = apiService.getComprasByUsuario(userId.toInt())
            if (response.isSuccessful) {
                emit(response.body() ?: emptyList())
            } else {
                throw IOException("Error al cargar el historial: ${response.code()}")
            }
        } catch (e: Exception) {
            throw IOException("No se pudo cargar el historial: ${e.message}", e)
        }
    }

    // Función para crear una nueva compra
    suspend fun createPurchase(compraRequest: CompraRequest): Result<Unit> {
        return try {
            val response = apiService.createCompra(compraRequest)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                // ---- ESTA ES LA PARTE CORREGIDA ----
                val errorJson = response.errorBody()?.string()
                var errorMessage = "Error desconocido al procesar la compra." // Mensaje por defecto

                if (errorJson != null) {
                    try {
                        // Usamos la librería de JSON para extraer el mensaje del campo "error"
                        val jsonObject = org.json.JSONObject(errorJson)
                        errorMessage = jsonObject.getString("error")
                    } catch (e: org.json.JSONException) {
                        // Si el error no es el JSON que esperamos, mostramos el cuerpo tal cual.
                        errorMessage = errorJson
                    }
                }
                Result.failure(IOException(errorMessage))
                // -------------------------------------
            }
        } catch (e: Exception) {
            Result.failure(IOException("No se pudo conectar con el servicio: ${e.message}", e))
        }
    }
}
