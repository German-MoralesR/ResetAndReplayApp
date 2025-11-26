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
import com.example.resetandreplay.data.remote.RetrofitClient
import com.example.resetandreplay.data.repository.ProductRepository
import com.example.resetandreplay.data.repository.PurchaseRepository
import com.example.resetandreplay.data.repository.ReviewRepository
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
    val userRepository = UserRepository(
        db.userDao(),
        apiService = RetrofitClient.instance
    )
    // El nuevo ProductRepository ya no necesita el Dao
    val productRepository = ProductRepository()
    val purchaseRepository = PurchaseRepository()
    val reviewRepository = ReviewRepository()

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(userRepository))
    // La factory funciona igual, solo que el repo que le pasamos es diferente
    val productViewModel: ProductViewModel = viewModel(factory = ProductViewModelFactory(productRepository))
    val purchaseViewModel: PurchaseViewModel = viewModel(factory = PurchaseViewModelFactory(purchaseRepository))
    val reviewViewModel: ReviewViewModel = viewModel(factory = ReviewViewModelFactory(reviewRepository))

    val navController = rememberNavController()
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            // 3. Pasamos el nuevo ViewModel al NavGraph
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                productViewModel = productViewModel,
                purchaseViewModel = purchaseViewModel,
                reviewViewModel = reviewViewModel
            )
        }
    }
}
