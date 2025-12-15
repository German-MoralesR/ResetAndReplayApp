package com.example.resetandreplay.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.resetandreplay.ui.viewmodel.AuthViewModel

@Composable
fun SecurityQuestionScreen(
    authViewModel: AuthViewModel,
    email: String,
    onAnswerCorrect: (String) -> Unit // Navega a ResetPasswordScreen
) {
    var question by remember { mutableStateOf<String?>(null) }
    var answer by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Carga la pregunta de seguridad cuando la pantalla aparece
    LaunchedEffect(email) {
        authViewModel.getSecurityQuestion(email) { result ->
            isLoading = false
            result.onSuccess { q -> question = q }
            result.onFailure { e -> error = e.message }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Verificación de Seguridad", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        when {
            isLoading -> CircularProgressIndicator()
            error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
            question != null -> {
                Text(question!!, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Tu respuesta") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        authViewModel.verifySecurityAnswer(email, answer) { result ->
                            if (result.isSuccess) {
                                onAnswerCorrect(email) // ¡Éxito! Navegamos a resetear contraseña
                            } else {
                                Toast.makeText(context, "Respuesta incorrecta. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Verificar")
                }
            }
        }
    }
}
