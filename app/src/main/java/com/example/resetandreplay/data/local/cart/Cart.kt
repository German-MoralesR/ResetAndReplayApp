package com.example.resetandreplay.data.local.cart

import com.example.resetandreplay.data.local.product.ProductEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Representa un ítem en el carrito de compras con un producto y su cantidad
data class CartItem(val product: ProductEntity, val quantity: Int)

// Objeto singleton que gestiona el estado del carrito de compras.
object Cart {
    // _items es un flujo mutable privado que contiene la lista de ítems del carrito
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    // items expone el estado del carrito como un flujo inmutable de solo lectura
    val items: StateFlow<List<CartItem>> = _items

    // Añade un producto al carrito o incrementa su cantidad si ya existe
    fun addItem(product: ProductEntity) {
        val currentItems = _items.value.toMutableList()
        val existingItem = currentItems.find { it.product.id == product.id }

        if (existingItem != null) {
            // Si el producto ya está en el carrito, incrementa la cantidad
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
            val index = currentItems.indexOf(existingItem)
            currentItems[index] = updatedItem
        } else {
            // Si es un producto nuevo, lo añade al carrito con cantidad 1
            currentItems.add(CartItem(product = product, quantity = 1))
        }
        _items.value = currentItems
    }

    // Elimina un producto del carrito
    fun removeItem(productId: Long) {
        val currentItems = _items.value.toMutableList()
        currentItems.removeAll { it.product.id == productId }
        _items.value = currentItems
    }

    // Vacía todos los productos del carrito
    fun clearCart() {
        _items.value = emptyList()
    }
}
