package com.example.resetandreplay.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.resetandreplay.ui.viewmodel.ProductViewModel

@Composable
fun ProductFormScreen(
    productViewModel: ProductViewModel,
    productId: Long?, // 1. Acepta un ID de producto opcional
    onProductSaved: () -> Unit
) {
    val products by productViewModel.uiState.collectAsState()
    
    // 2. Carga los datos del producto si estamos en modo edición
    val product = productId?.let { products.products.find { p -> p.id == it } }

    var name by remember(product) { mutableStateOf(product?.name ?: "") }
    var description by remember(product) { mutableStateOf(product?.description ?: "") }
    var price by remember(product) { mutableStateOf(product?.price?.toString() ?: "") }
    var stock by remember(product) { mutableStateOf(product?.stock?.toString() ?: "") }
    var sku by remember(product) { mutableStateOf(product?.sku ?: "") }
    var category by remember(product) { mutableStateOf(product?.category ?: "") }

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
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (name.isNotBlank() && price.toDoubleOrNull() != null && stock.toIntOrNull() != null) {
                    // 4. Pasamos el ID al guardar
                    productViewModel.saveProduct(
                        id = productId,
                        name = name,
                        description = description,
                        price = price.toDouble(),
                        stock = stock.toInt(),
                        sku = sku,
                        category = category
                    )
                    onProductSaved()
                } else {
                    Toast.makeText(context, "Por favor, completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Producto")
        }
    }
}
