package com.example.resetandreplay.ui.screen

import androidx.compose.foundation.background                 // Fondo
import androidx.compose.foundation.layout.*                   // Box/Column/Row/Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons                  // Íconos Material
import androidx.compose.material.icons.filled.Visibility      // Ícono mostrar
import androidx.compose.material.icons.filled.VisibilityOff   // Ícono ocultar
import androidx.compose.material3.*                           // Material 3
import androidx.compose.runtime.*                             // remember, Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment                          // Alineaciones
import androidx.compose.ui.Modifier                           // Modificador
import androidx.compose.ui.text.input.*                       // KeyboardOptions/Types/Transformations
import androidx.compose.ui.unit.dp                            // DPs
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Observa StateFlow
import com.example.resetandreplay.ui.viewmodel.AuthViewModel         // ViewModel

@Composable
fun RegisterScreenVm(
    vm: AuthViewModel, // recibimos el VM desde NavGraph
    onRegisteredNavigateLogin: () -> Unit, // Navega a Login si success=true
    onGoLogin: () -> Unit
) {

    val state by vm.register.collectAsStateWithLifecycle() // Observa estado en tiempo real

    if (state.success) { // Si registro fue exitoso
        vm.clearRegisterResult() // Limpia banderas
        onRegisteredNavigateLogin() // Navega a Login
    }

    RegisterScreen(
        name = state.name, // 1) Nombre
        email = state.email, // 2) Email
        phone = state.phone, // 3) Teléfono
        pass = state.pass, // 4) Password
        confirm = state.confirm, // 5) Confirmación
        securityQuestion = state.securityQuestion,
        securityAnswer = state.securityAnswer,
        securityQuestionError = state.securityQuestionError,
        securityAnswerError = state.securityAnswerError,

        nameError = state.nameError, // Errores por campo
        emailError = state.emailError,
        phoneError = state.phoneError,
        passError = state.passError,
        confirmError = state.confirmError,

        canSubmit = state.canSubmit, // Habilitar "Registrar"
        isSubmitting = state.isSubmitting, // Flag de carga
        errorMsg = state.errorMsg, // Error global

        onNameChange = vm::onNameChange, // Handlers
        onEmailChange = vm::onRegisterEmailChange,
        onPhoneChange = vm::onPhoneChange,
        onPassChange = vm::onRegisterPassChange,
        onConfirmChange = vm::onConfirmChange,
        onSecurityQuestionChange = vm::onSecurityQuestionChange,
        onSecurityAnswerChange = vm::onSecurityAnswerChange,

        onSubmit = vm::submitRegister, // Acción Registrar
        onGoLogin = onGoLogin // Ir a Login
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterScreen(
    name: String, // 1) Nombre (solo letras/espacios)
    email: String, // 2) Email
    phone: String, // 3) Teléfono (solo números)
    pass: String, // 4) Password (segura)
    confirm: String, // 5) Confirmación
    nameError: String?, // Errores
    emailError: String?,
    phoneError: String?,
    passError: String?,
    confirmError: String?,
    securityQuestion: String,
    securityAnswer: String,
    securityQuestionError: String?,
    securityAnswerError: String?,
    canSubmit: Boolean, // Habilitar botón
    isSubmitting: Boolean, // Flag de carga
    errorMsg: String?, // Error global (duplicado)
    onNameChange: (String) -> Unit, // Handler nombre
    onEmailChange: (String) -> Unit, // Handler email
    onPhoneChange: (String) -> Unit, // Handler teléfono
    onPassChange: (String) -> Unit, // Handler password
    onConfirmChange: (String) -> Unit, // Handler confirmación
    onSecurityQuestionChange: (String) -> Unit,
    onSecurityAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit, // Acción Registrar
    onGoLogin: () -> Unit // Ir a Login
) {
    //val bg = MaterialTheme.colorScheme.tertiaryContainer // Fondo único
    // variables para mostrar y ocultar el password
    var showPass by remember { mutableStateOf(false) } // Mostrar/ocultar password
    var showConfirm by remember { mutableStateOf(false) } // Mostrar/ocultar confirm

    // LISTA DE PREGUNTAS DE SEGURIDAD
    val securityQuestions = listOf(
        "¿Cuál es el nombre de tu primera mascota?",
        "¿Cuál es tu comida favorita?",
        "¿En qué ciudad naciste?",
        "¿Cuál era el nombre de tu escuela primaria?"
    )
    var questionMenuExpanded by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo
            .padding(16.dp), // Margen
        contentAlignment = Alignment.Center // Centro
    ) {
        // 5 modificamos el parametro de la columna
        Column(modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())) { // Estructura vertical
            Text(
                text = "Registro",
                style = MaterialTheme.typography.headlineSmall // Título
            )
            Spacer(Modifier.height(12.dp)) // Separación

            // Nombre (solo letras/espacios)
            OutlinedTextField(
                value = name, // Valor actual
                onValueChange = onNameChange, // Notifica VM (filtra y valida)
                label = { Text("Nombre") }, // Etiqueta
                singleLine = true, // Una línea
                isError = nameError != null, // Marca error
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text // Teclado de texto
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (nameError != null) { // Muestra error
                Text(nameError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp)) // Espacio

            // ---------- EMAIL ----------
            OutlinedTextField(
                value = email, // Valor actual
                onValueChange = onEmailChange, // Notifica VM (valida)
                label = { Text("Email") }, // Etiqueta
                singleLine = true, // Una línea
                isError = emailError != null, // Marca error
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email // Teclado de email
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError != null) { // Muestra error
                Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp)) // Espacio

            // ---------- TELÉFONO (solo números). El VM ya filtra a dígitos ----------
            OutlinedTextField(
                value = phone, // Valor actual (solo dígitos)
                onValueChange = onPhoneChange, // Notifica VM (filtra y valida)
                label = { Text("Teléfono") }, // Etiqueta
                singleLine = true, // Una línea
                isError = phoneError != null, // Marca error
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number // Teclado numérico
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (phoneError != null) { // Muestra error
                Text(phoneError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp)) // Espacio

            // Contraseña
            OutlinedTextField(
                value = pass, // Valor actual
                onValueChange = onPassChange, // Notifica VM (valida fuerza)
                label = { Text("Contraseña") }, // Etiqueta
                singleLine = true, // Una línea
                isError = passError != null, // Marca error
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(), // Oculta/mostrar
                trailingIcon = { // Icono para alternar visibilidad
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (passError != null) { // Muestra error
                Text(passError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp)) // Espacio

            // Confirmar contraseña
            OutlinedTextField(
                value = confirm, // Valor actual
                onValueChange = onConfirmChange, // Notifica VM (valida igualdad)
                label = { Text("Confirmar contraseña") },
                singleLine = true, // Una línea
                isError = confirmError != null, // Marca error
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(), // Oculta/mostrar
                trailingIcon = { // Icono para alternar visibilidad
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(
                            imageVector = if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showConfirm) "Ocultar confirmación" else "Mostrar confirmación"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (confirmError != null) { // Muestra error
                Text(confirmError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))

            // Dropdown para Pregunta de Seguridad
            ExposedDropdownMenuBox(
                expanded = questionMenuExpanded,
                onExpandedChange = { questionMenuExpanded = !questionMenuExpanded }
            ) {
                OutlinedTextField(
                    value = securityQuestion,
                    onValueChange = {}, // No se cambia directamente
                    readOnly = true,
                    label = { Text("Pregunta de seguridad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = questionMenuExpanded) },
                    isError = securityQuestionError != null,
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = questionMenuExpanded,
                    onDismissRequest = { questionMenuExpanded = false }
                ) {
                    securityQuestions.forEach { question ->
                        DropdownMenuItem(
                            text = { Text(question) },
                            onClick = {
                                onSecurityQuestionChange(question) // Llama al ViewModel
                                questionMenuExpanded = false
                            }
                        )
                    }
                }
            }
            if (securityQuestionError != null) {
                Text(securityQuestionError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))

            // Campo para Respuesta de Seguridad
            OutlinedTextField(
                value = securityAnswer,
                onValueChange = onSecurityAnswerChange,
                label = { Text("Tu respuesta secreta") },
                singleLine = true,
                isError = securityAnswerError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (securityAnswerError != null) {
                Text(securityAnswerError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(16.dp)) // Espacio

            // ---------- BOTÓN REGISTRAR ----------
            Button(
                onClick = onSubmit, // Intenta registrar (inserta en la colección)
                enabled = canSubmit && !isSubmitting, // Solo si todo es válido y no cargando
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSubmitting) { // Muestra loading mientras “procesa”
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Creando cuenta...")
                } else {
                    Text("Registrar")
                }
            }

            if (errorMsg != null) { // Error global (ej: usuario duplicado)
                Spacer(Modifier.height(8.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp)) // Espacio

            // ---------- BOTÓN IR A LOGIN ----------
            OutlinedButton(onClick = onGoLogin, modifier = Modifier.fillMaxWidth()) {
                Text("Ir a Iniciar sesión")
            }
        }
    }
}