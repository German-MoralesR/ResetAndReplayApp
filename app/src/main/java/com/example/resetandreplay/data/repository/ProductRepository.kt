package com.example.resetandreplay.data.repository

import android.content.Context
import android.net.Uri
import com.example.resetandreplay.R // Para la imagen por defecto
import com.example.resetandreplay.data.local.product.ProductEntity // Seguiremos usando la entidad local por ahora
import com.example.resetandreplay.data.remote.InventoryApiService
import com.example.resetandreplay.data.remote.InventoryRetrofitClient
import com.example.resetandreplay.data.remote.dto.CategoriaDto
import com.example.resetandreplay.data.remote.dto.EstadoDto
import com.example.resetandreplay.data.remote.dto.PlataformaDto
import com.example.resetandreplay.data.remote.dto.ProductDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

// ¡El repositorio ya no depende de ProductDao!
class ProductRepository(
    // Parámetro opcional: usa la instancia real en la app, permite inyectar un mock en los tests.
    private val context: Context,
    private val apiService: InventoryApiService = InventoryRetrofitClient.create(InventoryApiService::class.java)
) {
    private val inventoryBaseUrl = "http://10.0.2.2:8082"

    // Función para obtener todos los productos desde la API
    fun getAllProducts(): Flow<List<ProductEntity>> = flow {
        try {
            val response = apiService.getAllProducts()
            if (response.isSuccessful) {
                val productDtos = response.body() ?: emptyList()
                val productEntities = productDtos.map { dto ->
                    ProductEntity(
                        id = dto.id_producto.toLong(),
                        name = dto.nombre,
                        description = dto.descripcion,
                        price = dto.precio,
                        // --- ¡CORRECCIÓN AQUÍ! ---
                        // Construimos la URL completa si photoUrl no es nulo
                        imageUrl = dto.photoUrl?.let { inventoryBaseUrl + it } ?: "",
                        stock = dto.stock,
                        sku = dto.sku,
                        category = dto.categoria.nombre
                    )
                }
                emit(productEntities)
            } else {
                throw IOException("Error en la respuesta del servidor: ${response.code()}")
            }
        } catch (e: Exception) {
            if (e is java.util.concurrent.CancellationException) {
                throw e
            }
            throw IOException("No se pudieron cargar los productos: ${e.message}", e)
        }
    }

    // Función para obtener un producto por su ID
    fun getProductById(id: Long): Flow<ProductEntity?> = flow {
        try {
            val response = apiService.getProductById(id.toInt())
            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) {
                    val entity = ProductEntity(
                        id = dto.id_producto.toLong(),
                        name = dto.nombre,
                        description = dto.descripcion,
                        price = dto.precio,
                        // --- ¡CORRECCIÓN AQUÍ! ---
                        imageUrl = dto.photoUrl?.let { inventoryBaseUrl + it } ?: "",
                        stock = dto.stock,
                        sku = dto.sku,
                        category = dto.categoria.nombre
                    )
                    emit(entity)
                } else {
                    emit(null)
                }
            } else {
                throw IOException("Error al buscar el producto: ${response.code()}")
            }
        } catch (e: Exception) {
            throw IOException("No se pudo cargar el producto: ${e.message}", e)
        }
    }

    // --- FUNCIÓN DE INSERTAR/ACTUALIZAR ---
    suspend fun saveProduct(
        productDto: ProductDto, // Recibimos el DTO completo
        imageUri: Uri?          // Y la URI de la imagen (opcional)
    ): Result<ProductDto> {
        return try {
            // 1. Convertir el DTO del producto a un RequestBody de tipo JSON
            val productJson = Gson().toJson(productDto)
            val productRequestBody = productJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            // 2. Convertir la URI de la imagen a un MultipartBody.Part (si existe)
            val imagePart: MultipartBody.Part? = imageUri?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val fileBytes = inputStream.readBytes()
                    val requestFile = fileBytes.toRequestBody(
                        context.contentResolver.getType(uri)?.toMediaTypeOrNull() // "image/jpeg", "image/png", etc.
                    )
                    // El nombre "file" debe coincidir con el @RequestPart del backend
                    MultipartBody.Part.createFormData("file", "image.jpg", requestFile)
                }
            }

            // 3. Decidir si llamar a 'create' o 'update' basado en el ID
            val response = if (productDto.id_producto == 0) {
                // ID es 0 o no existe -> Crear nuevo producto
                apiService.createProduct(productRequestBody, imagePart)
            } else {
                // ID existe -> Actualizar producto existente
                apiService.updateProduct(productDto.id_producto, productRequestBody, imagePart)
            }

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(IOException("Error al guardar el producto: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función para borrar un producto por su ID
    suspend fun deleteProductById(id: Long) {
        try {
            val response = apiService.deleteProductById(id.toInt())
            if (!response.isSuccessful) {
                throw IOException("Error al eliminar el producto: ${response.code()}")
            }
        } catch (e: Exception) {
            throw IOException("No se pudo eliminar el producto: ${e.message}", e)
        }
    }

    fun getAllCategorias(): Flow<List<CategoriaDto>> = flow {
        val response = apiService.getAllCategorias()
        if (response.isSuccessful) {
            emit(response.body() ?: emptyList())
        } else {
            throw IOException("Error al cargar categorías: ${response.code()}")
        }
    }

    fun getAllPlataformas(): Flow<List<PlataformaDto>> = flow {
        val response = apiService.getAllPlataformas()
        if (response.isSuccessful) {
            emit(response.body() ?: emptyList())
        } else {
            throw IOException("Error al cargar plataformas: ${response.code()}")
        }
    }

    fun getAllEstados(): Flow<List<EstadoDto>> = flow {
        val response = apiService.getAllEstados()
        if (response.isSuccessful) {
            emit(response.body() ?: emptyList())
        } else {
            throw IOException("Error al cargar estados: ${response.code()}")
        }
    }
}
