package com.example.resetandreplay.data.local.product

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// operaciones para la tabla de productos
@Dao
interface ProductDao {

    // Inserta un producto. Si ya existe, lo reemplaza
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    // Obtiene todos los productos ordenados por nombre
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAll(): Flow<List<ProductEntity>>

    // Obtiene un producto por su ID
    @Query("SELECT * FROM products WHERE id = :productId")
    fun getById(productId: Long): Flow<ProductEntity?>

    // Elimina un producto por su ID
    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteById(productId: Long)

    // Cuenta el total de productos en la tabla
    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int
}
