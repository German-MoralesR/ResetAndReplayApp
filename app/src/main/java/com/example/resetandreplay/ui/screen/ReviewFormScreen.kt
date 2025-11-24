package com.example.resetandreplay.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.resetandreplay.data.remote.dto.ResenaRequest
import com.example.resetandreplay.ui.viewmodel.AuthViewModel
import com.example.resetandreplay.ui.viewmodel.ReviewViewModel

@Composable
fun ReviewFormScreen(
    productId: Int,
    userId: Int, // Necesitamos el ID del usuario que escribe la reseña
    reviewViewModel: ReviewViewModel,
    onReviewSubmitted: () -> Unit
) {
    var calificacion by remember { mutableStateOf(0) }
    var texto by remember { mutableStateOf("") }
    val context = LocalContext.current
    val reviewUiState by reviewViewModel.uiState.collectAsState()

    // Observamos el flag de éxito para navegar hacia atrás
    LaunchedEffect(reviewUiState.reviewSubmitted) {
        if (reviewUiState.reviewSubmitted) {
            Toast.makeText(context, "Reseña publicada con éxito", Toast.LENGTH_SHORT).show()
            reviewViewModel.clearSubmissionStatus() // Limpiamos el estado
            onReviewSubmitted() // Navegamos hacia atrás
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Escribe tu reseña", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Selector de estrellas
        Text("Calificación:")
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            (1..5).forEach { star ->
                Icon(
                    imageVector = if (star <= calificacion) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = "Estrella $star",
                    tint = if (star <= calificacion) Color(0xFFFFC107) else Color.Gray,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { calificacion = star }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para la reseña
        OutlinedTextField(
            value = texto,
            onValueChange = { texto = it },
            label = { Text("Tu opinión sobre el producto") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de enviar
        Button(
            onClick = {
                if (calificacion > 0 && texto.isNotBlank()) {
                    val resenaRequest = ResenaRequest(
                        idProducto = productId,
                        idUsuario = userId,
                        texto = texto,
                        calificacion = calificacion
                    )
                    reviewViewModel.submitReview(resenaRequest)
                } else {
                    Toast.makeText(context, "Por favor, selecciona una calificación y escribe una reseña", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publicar Reseña")
        }

        // Muestra un Toast si hay un error al enviar
        if(reviewUiState.error != null) {
            Toast.makeText(context, "Error: ${reviewUiState.error}", Toast.LENGTH_LONG).show()
            reviewViewModel.clearSubmissionStatus()
        }
    }
}