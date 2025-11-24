package com.example.resetandreplay.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.resetandreplay.data.local.storage.UserPreferences
import com.example.resetandreplay.data.remote.dto.CompraDto // <-- Importante
import com.example.resetandreplay.ui.util.formatPrice
import com.example.resetandreplay.ui.viewmodel.AuthViewModel
import com.example.resetandreplay.ui.viewmodel.PurchaseViewModel

@Composable
fun PurchaseHistoryScreen(
    purchaseViewModel: PurchaseViewModel,
    authViewModel: AuthViewModel
) {
    val uiState by purchaseViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val userEmail by userPrefs.userEmail.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(userEmail) {
        userEmail?.let { email ->
            val userResult = authViewModel.getUserDetails(email)
            if (userResult.isSuccess) {
                val userId = userResult.getOrNull()?.id
                userId?.let {
                    purchaseViewModel.getPurchaseHistory(it)
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mi Historial de Compras", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${uiState.error}")
                }
            }
            uiState.purchaseHistory.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no has realizado ninguna compra.")
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(uiState.purchaseHistory) { purchase ->
                        PurchaseCard(purchase = purchase)
                    }
                }
            }
        }
    }
}

@Composable
private fun PurchaseCard(purchase: CompraDto) { // <-- Ahora recibe CompraDto
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Formateamos la fecha que viene como String del microservicio
            Text(text = "Fecha: ${purchase.fecha.take(16).replace("T", " ")}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))

            // Mostramos los detalles de la compra
            purchase.detalles.forEach { detalle ->
                Text(text = "• ${detalle.cantidad} x Producto ID: ${detalle.id_producto} (${formatPrice(detalle.precio)})", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total: ${formatPrice(purchase.total)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}