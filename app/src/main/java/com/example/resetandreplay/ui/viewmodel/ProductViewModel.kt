package com.example.resetandreplay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resetandreplay.R
import com.example.resetandreplay.data.local.product.ProductEntity
import com.example.resetandreplay.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class ProductUiState(
    val products: List<ProductEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getAllProducts()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
                .collect { products ->
                    _uiState.value = _uiState.value.copy(isLoading = false, products = products)
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
        id: Long? = null,
        name: String,
        description: String,
        price: Double,
        stock: Int,
        sku: String,
        category: String
    ) {
        viewModelScope.launch {
            // 2. Si el ID no es nulo, estamos editando un producto existente.
            val productToSave = if (id != null) {
                // Creamos una copia del producto existente con los nuevos datos
                uiState.value.products.find { it.id == id }?.copy(
                    name = name,
                    description = description,
                    price = price,
                    stock = stock,
                    sku = sku,
                    category = category
                ) ?: return@launch // Si no se encuentra el producto, no hacemos nada
            } else {
                // Si el ID es nulo, creamos un producto nuevo
                ProductEntity(
                    name = name,
                    description = description,
                    price = price,
                    stock = stock,
                    sku = sku,
                    category = category,
                    imageUrl = R.drawable.logo // Imagen por defecto para nuevos productos
                )
            }
            repository.insertProduct(productToSave)
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            repository.deleteProductById(productId)
        }
    }
}
