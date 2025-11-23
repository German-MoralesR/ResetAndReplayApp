package com.example.resetandreplay.navigation

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Login : Route("login")
    data object Register : Route("register")
    data object ProductList : Route("products")
    data object Cart : Route("cart")
    data object Profile : Route("profile")
    data object ForgotPassword : Route("forgot-password")

    // 1. Nueva ruta para el historial de compras
    data object PurchaseHistory : Route("purchase-history")

    data object ResetPassword : Route("reset-password/{email}") {
        fun createRoute(email: String) = "reset-password/$email"
    }

    data object ProductForm : Route("product-form/{productId}") {
        fun createRoute(productId: Long?) = "product-form/${productId ?: -1L}"
    }

    data object ProductDetail : Route("products/{productId}") {
        fun createRoute(productId: Long) = "products/$productId"
    }
}
