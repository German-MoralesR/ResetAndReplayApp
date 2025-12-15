package com.example.resetandreplay.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.resetandreplay.data.remote.dto.CategoriaDto
import com.example.resetandreplay.data.remote.dto.EstadoDto
import com.example.resetandreplay.data.remote.dto.PlataformaDto
import com.example.resetandreplay.ui.viewmodel.ProductViewModel
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productViewModel: ProductViewModel,
    productId: Long?, // 1. Acepta un ID de producto opcional
    onProductSaved: () -> Unit
) {
    val uiState by productViewModel.uiState.collectAsState()

    // 2. Carga los datos del producto si estamos en modo edición
    val product = productId?.let { uiState.products.find { p -> p.id == it } }

    var name by remember(product) { mutableStateOf(product?.name ?: "") }
    var description by remember(product) { mutableStateOf(product?.description ?: "") }
    var price by remember(product) { mutableStateOf(product?.price?.toString() ?: "") }
    var stock by remember(product) { mutableStateOf(product?.stock?.toString() ?: "") }
    var sku by remember(product) { mutableStateOf(product?.sku ?: "") }
    var category by remember(product) { mutableStateOf(product?.category ?: "") }

    // Estados para el menú desplegable de CATEGORÍAS
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<CategoriaDto?>(null) }

    // Estados para el menú desplegable de PLATAFORMAS
    var platformMenuExpanded by remember { mutableStateOf(false) }
    var selectedPlatform by remember { mutableStateOf<PlataformaDto?>(null) }

    // Estados para el menú desplegable de ESTADOS
    var stateMenuExpanded by remember { mutableStateOf(false) }
    var selectedState by remember { mutableStateOf<EstadoDto?>(null) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para seleccionar una imagen de la galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val context = LocalContext.current

    // 3. El título cambia si estamos editando o creando
    val title = if (productId == null) "Añadir Nuevo Producto" else "Editar Producto"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del producto") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock disponible") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = sku, onValueChange = { sku = it }, label = { Text("SKU") }, modifier = Modifier.fillMaxWidth())

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { imagePickerLauncher.launch("image/*") }, // Lanza el selector de imágenes
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Haz clic para seleccionar una imagen", style = MaterialTheme.typography.bodyLarge)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = categoryMenuExpanded, // Usa el estado que creamos para saber si está abierto o cerrado
            onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded } // Al hacer clic, cambia el estado
        ) {
            // ESTE ES EL CAMPO DE TEXTO (la parte visible del ComboBox)
            OutlinedTextField(
                value = selectedCategory?.nombre ?: "Selecciona una categoría", // Muestra el nombre de la categoría seleccionada, o un texto por defecto
                onValueChange = {}, // No hace nada, porque es de solo lectura
                readOnly = true,    // ¡Importante! El usuario no puede escribir aquí
                label = { Text("Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded) }, // La flechita de desplegar
                modifier = Modifier
                    .menuAnchor() // Le dice al menú dónde anclarse
                    .fillMaxWidth()
            )

            // ESTE ES EL MENÚ DESPLEGABLE (la parte que aparece y desaparece)
            ExposedDropdownMenu(
                expanded = categoryMenuExpanded, // También usa el estado para mostrarse u ocultarse
                onDismissRequest = { categoryMenuExpanded = false } // Si el usuario hace clic afuera, se cierra
            ) {
                // Recorremos la lista de categorías que viene del ViewModel (uiState.categories)
                uiState.categories.forEach { category ->
                    // Por cada categoría en la lista, creamos un ítem en el menú
                    DropdownMenuItem(
                        text = { Text(category.nombre) }, // El texto que ve el usuario
                        onClick = {
                            selectedCategory = category // 1. Guardamos el objeto completo de la categoría seleccionada
                            categoryMenuExpanded = false // 2. Cerramos el menú
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = platformMenuExpanded,
            onExpandedChange = { platformMenuExpanded = !platformMenuExpanded }
        ) {
            OutlinedTextField(
                value = selectedPlatform?.nombre ?: "Selecciona una plataforma",
                onValueChange = {},
                readOnly = true,
                label = { Text("Plataforma") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = platformMenuExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = platformMenuExpanded,
                onDismissRequest = { platformMenuExpanded = false }
            ) {
                // Recorremos la lista de plataformas del ViewModel (uiState.platforms)
                uiState.platforms.forEach { platform ->
                    DropdownMenuItem(
                        text = { Text(platform.nombre) },
                        onClick = {
                            selectedPlatform = platform // Guardamos la plataforma seleccionada
                            platformMenuExpanded = false // Cerramos el menú
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = stateMenuExpanded,
            onExpandedChange = { stateMenuExpanded = !stateMenuExpanded }
        ) {
            OutlinedTextField(
                value = selectedState?.nombre ?: "Selecciona un estado",
                onValueChange = {},
                readOnly = true,
                label = { Text("Estado") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateMenuExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = stateMenuExpanded,
                onDismissRequest = { stateMenuExpanded = false }
            ) {
                // Recorremos la lista de estados del ViewModel
                uiState.states.forEach { state ->
                    DropdownMenuItem(
                        text = { Text(state.nombre) },
                        onClick = {
                            selectedState = state // Guardamos el estado seleccionado
                            stateMenuExpanded = false // Cerramos el menú
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))



        Button(
            onClick = {
                if (name.isNotBlank() && /* ... otras validaciones ... */ selectedState != null) {
                    productViewModel.saveProduct(
                        id = productId,
                        name = name,
                        description = description,
                        price = price.toDoubleOrNull() ?: 0.0,
                        stock = stock.toIntOrNull() ?: 0,
                        sku = sku,
                        categoryId = selectedCategory!!.id_cat,
                        platformId = selectedPlatform!!.id_plat,
                        estadoId = selectedState!!.id_estado,
                        imageUri = imageUri // <-- ¡PASAMOS LA URI DE LA IMAGEN!
                    )
                    onProductSaved()
                } else {
                    Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Producto")
        }
    }
}
