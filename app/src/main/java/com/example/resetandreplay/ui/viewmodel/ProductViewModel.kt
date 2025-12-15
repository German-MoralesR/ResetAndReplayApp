package com.example.resetandreplay.ui.viewmodel

import androidx.compose.animation.core.copy
import androidx.datastore.core.IOException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resetandreplay.R
import com.example.resetandreplay.data.local.product.ProductEntity
import com.example.resetandreplay.data.remote.dto.CategoriaDto
import com.example.resetandreplay.data.remote.dto.EstadoDto
import com.example.resetandreplay.data.remote.dto.PlataformaDto
import com.example.resetandreplay.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.net.Uri
import com.example.resetandreplay.data.remote.dto.ProductDto

data class ProductUiState(
    val products: List<ProductEntity> = emptyList(),
    val categories: List<CategoriaDto> = emptyList(),
    val platforms: List<PlataformaDto> = emptyList(),
    val states: List<EstadoDto> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState

    init {
        loadAllInitialData()
    }

    private fun loadAllInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Carga de productos
                repository.getAllProducts().collect { products ->
                    _uiState.update { it.copy(products = products) }
                }
                // Carga de categorías
                repository.getAllCategorias().collect { categories ->
                    _uiState.update { it.copy(categories = categories) }
                }
                // Carga de plataformas
                repository.getAllPlataformas().collect { platforms ->
                    _uiState.update { it.copy(platforms = platforms) }
                }
                // Carga de estados
                repository.getAllEstados().collect { states ->
                    _uiState.update { it.copy(states = states) }
                }
            } catch (e: IOException) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadProductById(productId: Long) {
        viewModelScope.launch {
            repository.getProductById(productId)
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
                .collect { product ->
                    if (product != null) {
                        val updatedProducts = _uiState.value.products.toMutableList()
                        val index = updatedProducts.indexOfFirst { it.id == productId }
                        if (index != -1) {
                            updatedProducts[index] = product
                        } else {
                            updatedProducts.add(product)
                        }
                        _uiState.value = _uiState.value.copy(isLoading = false, products = updatedProducts)
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = "Producto no encontrado")
                    }
                }
        }
    }

    // 1. Modificamos la función para que acepte un ID opcional
    fun saveProduct(
        id: Long?,
        name: String,
        description: String,
        price: Double,
        stock: Int,
        sku: String,
        categoryId: Int,
        platformId: Int,
        estadoId: Int,
        imageUri: Uri? // <-- AÑADIR URI DE IMAGEN
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Creamos el DTO que espera el repositorio
            val productDto = ProductDto(
                id_producto = id?.toInt() ?: 0,
                nombre = name,
                descripcion = description,
                precio = price,
                stock = stock,
                sku = sku,
                categoria = CategoriaDto(id_cat = categoryId, nombre = ""),
                plataforma = PlataformaDto(id_plat = platformId, nombre = ""),
                estado = EstadoDto(id_estado = estadoId, nombre = ""),
                fotos = emptyList(), // El backend maneja las fotos por separado
                photoUrl = null
            )

            val result = repository.saveProduct(productDto, imageUri)

            if (result.isSuccess) {
                // Éxito, recargamos todo para ver los cambios
                loadAllInitialData()
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Error desconocido") }
            }
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            repository.deleteProductById(productId)
        }
    }
}
