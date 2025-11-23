package com.example.resetandreplay.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(onGoToProducts: () -> Unit) {
    val bg = MaterialTheme.colorScheme.surface

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .verticalScroll(rememberScrollState()) // Hace la pantalla scrollable
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tarjeta de bienvenida
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Bienvenido a Reset&Replay",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Tu tienda de confianza para consolas, videojuegos y merchandising retro. ¡Revive la nostalgia!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Botón para ver productos
        Button(onClick = onGoToProducts, modifier = Modifier.fillMaxWidth()) {
            Text("Ver Productos", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(32.dp))

        // Sección Sobre Nosotros
        Text("Sobre Nosotros", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(
            "Somos un equipo de apasionados por los videojuegos clásicos, dedicados a rescatar y ofrecer los tesoros de una época dorada. Cada artículo en nuestra tienda ha sido seleccionado y probado con cariño para asegurar la mejor calidad.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )

        Spacer(Modifier.height(24.dp))

        // Sección Contacto
        Text("Contacto", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(
            "¿Tienes alguna duda o buscas algún producto en especial? Escríbenos a contacto@resetandreplay.com o síguenos en nuestras redes sociales.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )
    }
}
