package com.example.resetandreplay.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.resetandreplay.data.local.cart.Cart
import com.example.resetandreplay.data.local.product.ProductEntity
import com.example.resetandreplay.data.remote.dto.ResenaDto
import com.example.resetandreplay.ui.util.formatPrice
import com.example.resetandreplay.ui.viewmodel.ProductViewModel
import com.example.resetandreplay.ui.viewmodel.ReviewViewModel

@Composable
fun ProductDetailScreen(
    productId: Long,
    productViewModel: ProductViewModel,
    reviewViewModel: ReviewViewModel, // <-- 1. Recibimos el nuevo ViewModel
    onAddReview: (Long) -> Unit
) {
    val uiState by productViewModel.uiState.collectAsState()
    val reviewUiState by reviewViewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        productViewModel.loadProductById(productId)
        reviewViewModel.loadReviews(productId.toInt())
    }

    val product = uiState.products.find { it.id == productId }

    when {
        uiState.isLoading -> {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
                Text(text = "Cargando detalle del producto...")
            }
        }
        uiState.error != null -> {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(text = "Error: ${uiState.error}")
            }
        }
        product != null -> {
            // Usamos LazyColumn para poder hacer scroll si hay muchas reseñas
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Parte 1: Detalles del producto
                item {
                    ProductDetail(product = product)
                }

                // Separador
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Reseñas", style = MaterialTheme.typography.headlineSmall)
                }

                // Parte 2: Lista de Reseñas
                if (reviewUiState.isLoading) {
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                } else if (reviewUiState.reviews.isEmpty()) {
                    item {
                        Text("Este producto aún no tiene reseñas. ¡Sé el primero!", modifier = Modifier.padding(16.dp))
                    }
                } else {
                    items(reviewUiState.reviews) { review ->
                        ReviewCard(review = review)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Parte 3: Botón para añadir reseña
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onAddReview(productId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Escribir una reseña")
                    }
                }
            }
        }
        else -> {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(text = "Producto no encontrado")
            }
        }
    }
}

@Composable
private fun ProductDetail(product: ProductEntity) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = product.imageUrl),
            contentDescription = "Imagen de ${product.name}",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = product.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = product.description, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Precio:", style = MaterialTheme.typography.titleMedium)
            // Usamos la función formatPrice
            Text(text = formatPrice(product.price), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Stock:", style = MaterialTheme.typography.titleMedium)
            Text(text = "${product.stock} unidades", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "SKU:", style = MaterialTheme.typography.titleMedium)
            Text(text = product.sku, style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Categoría:", style = MaterialTheme.typography.titleMedium)
            Text(text = product.category, style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                Cart.addItem(product)
                Toast.makeText(context, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir al carrito")
        }
    }
}

@Composable
private fun ReviewCard(review: ResenaDto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Mostramos la calificación con estrellas
                Row {
                    repeat(review.calificacion) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107), // Color dorado
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Mostramos la fecha
                Text(
                    text = review.fecha.take(10), // Tomamos solo la fecha (YYYY-MM-DD)
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Mostramos el texto de la reseña
            Text(text = review.texto, style = MaterialTheme.typography.bodyMedium)
        }
    }
}