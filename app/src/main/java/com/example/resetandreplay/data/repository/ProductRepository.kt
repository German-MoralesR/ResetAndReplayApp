package com.example.resetandreplay.data.repository

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

// ¡El repositorio ya no depende de ProductDao!
class ProductRepository(
    // Parámetro opcional: usa la instancia real en la app, permite inyectar un mock en los tests.
    private val apiService: InventoryApiService = InventoryRetrofitClient.create(InventoryApiService::class.java)
) {

    // Función para obtener todos los productos desde la API
    fun getAllProducts(): Flow<List<ProductEntity>> = flow {
        try {
            val response = apiService.getAllProducts()
            if (response.isSuccessful) {
                val productDtos = response.body() ?: emptyList()
                // Convertimos la lista de DTOs a una lista de Entidades locales
                val productEntities = productDtos.map { dto ->
                    ProductEntity(
                        id = dto.id_producto.toLong(),
                        name = dto.nombre,
                        description = dto.descripcion,
                        price = dto.precio,
                        // OJO: La imagen ya no viene de R.drawable. Usaremos un placeholder.
                        // La primera foto de la lista podría ser la principal.
                        imageUrl = R.drawable.logo, // Placeholder
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
                        imageUrl = R.drawable.logo, // Placeholder
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

    // Función para insertar/crear un nuevo producto
    suspend fun insertProduct(product: ProductEntity, categoryId: Int, platformId: Int) {
        try {
            // 1. Convertimos la ProductEntity de la UI a un ProductDto para la API
            val productDto = ProductDto(
                id_producto = product.id.toInt(), // En la creación, la API lo ignora y genera uno nuevo
                nombre = product.name,
                descripcion = product.description,
                precio = product.price,
                stock = product.stock,
                sku = product.sku,
                // Creamos DTOs "vacíos" para las relaciones, solo con el ID que es lo que JPA necesita para enlazar.
                // Aquí asumimos IDs fijos. En un futuro, el formulario de la app debería dejar seleccionar la categoría/plataforma/estado.
                categoria = CategoriaDto(id_cat = categoryId, nombre = ""), // El nombre no importa aquí
                plataforma = PlataformaDto(id_plat = platformId, nombre = ""),
                estado = EstadoDto(id_estado = 1, nombre = "Nuevo"), // Placeholder
                fotos = emptyList() // La API podría manejar la subida de fotos por separado
            )

            // 2. Llamamos al endpoint de la API
            val response = apiService.createProduct(productDto)
            if (!response.isSuccessful) {
                throw IOException("Error al crear el producto: ${response.code()}")
            }
        } catch (e: Exception) {
            throw IOException("No se pudo crear el producto: ${e.message}", e)
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
