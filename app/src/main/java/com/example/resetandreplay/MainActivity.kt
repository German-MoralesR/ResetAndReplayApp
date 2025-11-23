package com.example.resetandreplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.resetandreplay.data.local.database.AppDatabase
import com.example.resetandreplay.data.repository.ProductRepository
import com.example.resetandreplay.data.repository.PurchaseRepository
import com.example.resetandreplay.data.repository.UserRepository
import com.example.resetandreplay.navigation.AppNavGraph
import com.example.resetandreplay.ui.util.NotificationHelper
import com.example.resetandreplay.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()

        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current.applicationContext
    val db = AppDatabase.getInstance(context)

    // Inyección de dependencias
    val userRepository = UserRepository(db.userDao())
    val productRepository = ProductRepository(db.productDao())
    val purchaseRepository = PurchaseRepository(db.purchaseDao()) // 1. Creamos el nuevo repo

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(userRepository))
    val productViewModel: ProductViewModel = viewModel(factory = ProductViewModelFactory(productRepository))
    val purchaseViewModel: PurchaseViewModel = viewModel(factory = PurchaseViewModelFactory(purchaseRepository)) // 2. Creamos el nuevo ViewModel

    val navController = rememberNavController()
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            // 3. Pasamos el nuevo ViewModel al NavGraph
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                productViewModel = productViewModel,
                purchaseViewModel = purchaseViewModel
            )
        }
    }
}
