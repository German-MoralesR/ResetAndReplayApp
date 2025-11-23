package com.example.resetandreplay.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.resetandreplay.data.local.storage.UserPreferences
import com.example.resetandreplay.ui.components.AppDrawer
import com.example.resetandreplay.ui.components.AppTopBar
import com.example.resetandreplay.ui.components.defaultDrawerItems
import com.example.resetandreplay.ui.screen.*
import com.example.resetandreplay.ui.viewmodel.AuthViewModel
import com.example.resetandreplay.ui.viewmodel.ProductViewModel
import com.example.resetandreplay.ui.viewmodel.PurchaseViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    purchaseViewModel: PurchaseViewModel
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val isLoggedIn by userPrefs.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
    val isAdmin by userPrefs.isAdmin.collectAsStateWithLifecycle(initialValue = false)
    val userEmail by userPrefs.userEmail.collectAsStateWithLifecycle(initialValue = null)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val goHome: () -> Unit = { navController.navigate(Route.Home.path) { popUpTo(Route.Home.path) { inclusive = true } } }
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) }
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) }
    val goProducts: () -> Unit = { navController.navigate(Route.ProductList.path) }
    val goToAddProduct: () -> Unit = { navController.navigate(Route.ProductForm.createRoute(null)) }
    val goToEditProduct: (Long) -> Unit = { navController.navigate(Route.ProductForm.createRoute(it)) }
    val goToCart: () -> Unit = { navController.navigate(Route.Cart.path) }
    val goToProfile: () -> Unit = { if (isLoggedIn) navController.navigate(Route.Profile.path) else goLogin() }
    val onLoggedOut: () -> Unit = { goHome() }
    val goToForgotPassword: () -> Unit = { navController.navigate(Route.ForgotPassword.path) }
    val goToResetPassword: (String) -> Unit = { navController.navigate(Route.ResetPassword.createRoute(it)) }
    val goToPurchaseHistory: () -> Unit = { navController.navigate(Route.PurchaseHistory.path) } // 1. Acción para el historial

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = null,
                items = defaultDrawerItems(
                    onHome = { scope.launch { drawerState.close() }; goHome() },
                    onProducts = { scope.launch { drawerState.close() }; goProducts() },
                    onGoToCart = { scope.launch { drawerState.close() }; goToCart() }
                )
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onGoToCart = goToCart,
                    onGoToProfile = goToProfile,
                    onGoToHome = goHome,
                    isUserLoggedIn = isLoggedIn
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ) {

                composable(Route.Home.path) { HomeScreen(onGoToProducts = goProducts) }
                composable(Route.Login.path) { LoginScreenVm(vm = authViewModel, onLoginOkNavigateHome = goHome, onGoRegister = goRegister, onGoToForgotPassword = goToForgotPassword) }
                composable(Route.Register.path) { RegisterScreenVm(vm = authViewModel, onRegisteredNavigateLogin = goLogin, onGoLogin = goLogin) }
                composable(Route.ProductList.path) { ProductListScreen(productViewModel = productViewModel, isAdmin = isAdmin, onProductClick = { navController.navigate(Route.ProductDetail.createRoute(it)) }, onAddProduct = goToAddProduct, onEditProduct = goToEditProduct) }
                
                // 2. Pasamos todos los parámetros necesarios a CartScreen
                composable(Route.Cart.path) { 
                    CartScreen(
                        purchaseViewModel = purchaseViewModel,
                        onGoToLogin = goLogin,
                        isUserLoggedIn = isLoggedIn,
                        authViewModel = authViewModel,
                        userEmail = userEmail
                    ) 
                }

                composable(Route.ProductDetail.path, arguments = listOf(navArgument("productId") { type = NavType.LongType })) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
                    ProductDetailScreen(productId = productId, productViewModel = productViewModel)
                }
                // 3. Pasamos la nueva acción a ProfileScreen
                composable(Route.Profile.path) { ProfileScreen(authViewModel = authViewModel, onLoggedOut = onLoggedOut, onGoToPurchaseHistory = goToPurchaseHistory) }
                composable(Route.ProductForm.path, arguments = listOf(navArgument("productId") { type = NavType.LongType; defaultValue = -1L })) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getLong("productId")
                    ProductFormScreen(
                        productViewModel = productViewModel,
                        productId = if (productId == -1L) null else productId,
                        onProductSaved = { navController.popBackStack() }
                    )
                }
                composable(Route.ForgotPassword.path) { ForgotPasswordScreen(authViewModel = authViewModel, onUserFound = { email -> goToResetPassword(email) }) }
                composable(Route.ResetPassword.path, arguments = listOf(navArgument("email") { type = NavType.StringType })) { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    ResetPasswordScreen(
                        authViewModel = authViewModel,
                        email = email,
                        onPasswordReset = { navController.popBackStack(Route.Login.path, false) }
                    )
                }

                // 4. Añadimos el nuevo destino para el historial
                composable(Route.PurchaseHistory.path) {
                    PurchaseHistoryScreen(purchaseViewModel = purchaseViewModel, authViewModel = authViewModel)
                }
            }
        }
    }
}
