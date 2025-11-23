package com.example.resetandreplay.data.repository

import com.example.resetandreplay.data.local.product.ProductDao
import com.example.resetandreplay.data.local.product.ProductEntity
import kotlinx.coroutines.flow.Flow

// Repositorio para gestionar los datos de productos
class ProductRepository(private val productDao: ProductDao) {

    // Obtiene todos los productos como un Flow
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAll()

    // Obtiene un producto por su ID
    fun getProductById(id: Long): Flow<ProductEntity?> = productDao.getById(id)

    // Inserta un nuevo producto
    suspend fun insertProduct(product: ProductEntity) {
        productDao.insert(product)
    }

    // Elimina un producto por su ID
    suspend fun deleteProductById(id: Long) {
        productDao.deleteById(id)
    }
}
