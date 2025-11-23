package com.example.resetandreplay.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.resetandreplay.data.local.cart.Cart
import com.example.resetandreplay.data.local.cart.CartItem
import com.example.resetandreplay.data.local.purchase.PurchaseEntity
import com.example.resetandreplay.ui.util.NotificationHelper
import com.example.resetandreplay.ui.util.formatPrice
import com.example.resetandreplay.ui.viewmodel.AuthViewModel
import com.example.resetandreplay.ui.viewmodel.PurchaseViewModel
import kotlinx.coroutines.launch

@Composable
fun CartScreen(
    purchaseViewModel: PurchaseViewModel,
    authViewModel: AuthViewModel,
    onGoToLogin: () -> Unit,
    isUserLoggedIn: Boolean,
    userEmail: String?
) {
    val cartItems by Cart.items.collectAsState()
    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper(context) }
    val scope = rememberCoroutineScope()

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            notificationHelper.sendPurchaseConfirmationNotification()
        } else {
            Toast.makeText(context, "Permiso de notificación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tu carrito está vacío", style = MaterialTheme.typography.titleLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(cartItems) { item ->
                    CartItemRow(item = item, onRemove = { Cart.removeItem(item.product.id) })
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                val totalPrice = cartItems.sumOf { it.product.price * it.quantity }
                Text(text = "Total: ${formatPrice(totalPrice)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (!isUserLoggedIn) {
                            Toast.makeText(context, "Debes iniciar sesión para finalizar la compra", Toast.LENGTH_SHORT).show()
                            onGoToLogin()
                            return@Button
                        }

                        scope.launch {
                            userEmail?.let { email ->
                                val userResult = authViewModel.getUserDetails(email)
                                if (userResult.isSuccess) {
                                    val user = userResult.getOrNull()!!
                                    val itemsDescription = cartItems.joinToString(separator = ", ") { "${it.quantity} x ${it.product.name}" }
                                    
                                    val purchase = PurchaseEntity(
                                        userId = user.id,
                                        itemsDescription = itemsDescription,
                                        totalPrice = totalPrice,
                                        date = System.currentTimeMillis()
                                    )
                                    purchaseViewModel.savePurchase(purchase)

                                    Cart.clearCart()
                                    Toast.makeText(context, "¡Gracias por tu compra!", Toast.LENGTH_SHORT).show()

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        when {
                                            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                                                notificationHelper.sendPurchaseConfirmationNotification()
                                            }
                                            else -> {
                                                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                            }
                                        }
                                    } else {
                                        notificationHelper.sendPurchaseConfirmationNotification()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finalizar Compra")
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(item: CartItem, onRemove: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = item.product.imageUrl), contentDescription = item.product.name, modifier = Modifier.size(60.dp))
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Cantidad: ${item.quantity}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onRemove) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar producto")
            }
        }
    }
}
