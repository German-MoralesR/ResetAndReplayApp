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
import com.example.resetandreplay.data.local.purchase.PurchaseEntity
import com.example.resetandreplay.data.local.storage.UserPreferences
import com.example.resetandreplay.ui.util.formatPrice
import com.example.resetandreplay.ui.viewmodel.AuthViewModel
import com.example.resetandreplay.ui.viewmodel.PurchaseViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PurchaseHistoryScreen(
    purchaseViewModel: PurchaseViewModel,
    authViewModel: AuthViewModel
) {
    val uiState by purchaseViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val userEmail by userPrefs.userEmail.collectAsStateWithLifecycle(initialValue = null)

    // 1. Corregido: Se usa el email de DataStore para obtener el usuario y luego su historial.
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
            uiState.purchases.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no has realizado ninguna compra.")
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(uiState.purchases) { purchase ->
                        PurchaseCard(purchase = purchase)
                    }
                }
            }
        }
    }
}

@Composable
private fun PurchaseCard(purchase: PurchaseEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(purchase.date))
            Text(text = "Fecha: $date", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = purchase.itemsDescription, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total: ${formatPrice(purchase.totalPrice)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
