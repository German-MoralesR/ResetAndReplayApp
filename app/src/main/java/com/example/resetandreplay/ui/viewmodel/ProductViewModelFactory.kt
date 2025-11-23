package com.example.resetandreplay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.resetandreplay.data.repository.ProductRepository

// Factory para crear instancias de ProductViewModel
class ProductViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {

    // Crea una nueva instancia del ViewModel solicitado.
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel class desconocido: ${modelClass.name}")
    }
}
