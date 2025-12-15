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
fun ForgotPasswordScreen(
    authViewModel: AuthViewModel,
    // La navegación ahora la gestionará el ViewModel tras una verificación exitosa
    onUserFound: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // Estado de carga
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Recuperar Contraseña", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Ingresa tu correo electrónico para iniciar la recuperación.", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading // Deshabilitar mientras carga
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotBlank()) {
                    isLoading = true // Inicia la carga
                    // Llamamos a la función que busca la pregunta
                    authViewModel.getSecurityQuestion(email) { result ->
                        isLoading = false // Termina la carga
                        result.onSuccess {
                            // ¡Éxito! La pregunta existe, ahora podemos navegar.
                            onUserFound(email)
                        }
                        result.onFailure { exception ->
                            // Falló: mostramos el error que viene del repositorio
                            Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Por favor, ingresa un email", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading // Deshabilitar mientras carga
        ) {
            if (isLoading) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Buscando...")
            } else {
                Text("Buscar cuenta")
            }
        }
    }
}
