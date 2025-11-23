package com.example.resetandreplay.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onOpenDrawer: () -> Unit,
    onGoToCart: () -> Unit,
    onGoToProfile: () -> Unit,
    onGoToHome: () -> Unit,
    isUserLoggedIn: Boolean
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Text(
                text = "Reset&Replay",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { onGoToHome() } // Hace que el título sea clickeable
            )
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menú")
            }
        },
        actions = {
            IconButton(onClick = onGoToCart) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito de compras")
            }
            IconButton(onClick = onGoToProfile) {
                Icon(
                    imageVector = if (isUserLoggedIn) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Perfil de usuario"
                )
            }
        }
    )
}
