package com.example.resetandreplay.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.resetandreplay.data.local.storage.UserPreferences
import com.example.resetandreplay.ui.viewmodel.AuthViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images").apply { if (!exists()) mkdirs() }
    return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir)
}

private fun getImageUriFromFile(context: Context, file: File): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel, 
    onLoggedOut: () -> Unit,
    onGoToPurchaseHistory: () -> Unit // 1. Nueva acción para navegar al historial
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    val userEmail by userPrefs.userEmail.collectAsStateWithLifecycle(initialValue = null)
    val photoUriString by userPrefs.userPhotoUri.collectAsStateWithLifecycle(initialValue = null)

    var userName by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }

    LaunchedEffect(userEmail) {
        userEmail?.let {
            val result = authViewModel.getUserDetails(it)
            result.onSuccess {
                user ->
                userName = user.name
                userPhone = user.phone
            }
        }
    }

    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCaptureUri?.let { uri ->
                authViewModel.savePhotoUri(userPrefs, uri.toString())
                Toast.makeText(context, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show()
            }
        } else {
            pendingCaptureUri = null
            Toast.makeText(context, "No se tomó ninguna foto", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        if (photoUriString != null) {
            AsyncImage(model = Uri.parse(photoUriString), contentDescription = "Foto de perfil", modifier = Modifier.size(150.dp).clip(CircleShape), contentScale = ContentScale.Crop)
        } else {
            Icon(imageVector = Icons.Outlined.AccountCircle, contentDescription = "Foto de perfil de placeholder", modifier = Modifier.size(150.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val file = createTempImageFile(context)
            val uri = getImageUriFromFile(context, file)
            pendingCaptureUri = uri
            takePictureLauncher.launch(uri)
        }) {
            Text(if (photoUriString == null) "Añadir foto" else "Cambiar foto")
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(value = userName, onValueChange = {}, readOnly = true, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = userEmail ?: "", onValueChange = {}, readOnly = true, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = userPhone, onValueChange = {}, readOnly = true, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Botón para ver el historial de compras
        OutlinedButton(onClick = onGoToPurchaseHistory, modifier = Modifier.fillMaxWidth()) {
            Text("Mi Historial de Compras")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                authViewModel.logout(userPrefs)
                onLoggedOut()
                Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar Sesión")
        }
    }
}
