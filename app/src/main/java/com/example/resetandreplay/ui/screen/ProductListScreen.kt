package com.example.resetandreplay.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.resetandreplay.data.local.product.ProductEntity
import com.example.resetandreplay.ui.util.formatPrice
import com.example.resetandreplay.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    productViewModel: ProductViewModel,
    isAdmin: Boolean,
    onProductClick: (Long) -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (Long) -> Unit // 1. Nueva acción para editar
) {
    val uiState by productViewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val sortOptions = listOf("Defecto", "Menor a mayor", "Mayor a menor")
    var selectedSortOption by remember { mutableStateOf(sortOptions[0]) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    val categories = listOf("Todas") + uiState.products.map { it.category }.distinct()
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    // 2. Estado para el diálogo de confirmación de borrado
    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    if (showDeleteDialog && productToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar el producto '${productToDelete!!.name}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        productViewModel.deleteProduct(productToDelete!!.id)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = onAddProduct) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir producto")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // ... (filtros y búsqueda se mantienen igual)
            OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it }, label = { Text("Buscar producto") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(expanded = sortMenuExpanded, onExpandedChange = { sortMenuExpanded = !sortMenuExpanded }, modifier = Modifier.weight(1f)) {
                    OutlinedTextField(value = selectedSortOption, onValueChange = {}, readOnly = true, label = { Text("Ordenar por") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortMenuExpanded) }, modifier = Modifier.menuAnchor())
                    ExposedDropdownMenu(expanded = sortMenuExpanded, onDismissRequest = { sortMenuExpanded = false }) { sortOptions.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { selectedSortOption = it; sortMenuExpanded = false }) } }
                }
                ExposedDropdownMenuBox(expanded = categoryMenuExpanded, onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded }, modifier = Modifier.weight(1f)) {
                    OutlinedTextField(value = selectedCategory, onValueChange = {}, readOnly = true, label = { Text("Categoría") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded) }, modifier = Modifier.menuAnchor())
                    ExposedDropdownMenu(expanded = categoryMenuExpanded, onDismissRequest = { categoryMenuExpanded = false }) { categories.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { selectedCategory = it; categoryMenuExpanded = false }) } }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
                uiState.error != null -> { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = "Error: ${uiState.error}") } }
                else -> {
                    val processedProducts = uiState.products
                        .filter { it.name.contains(searchQuery, ignoreCase = true) }
                        .filter { if (selectedCategory == "Todas") true else it.category == selectedCategory }
                        .let {
                            when (selectedSortOption) {
                                "Menor a mayor" -> it.sortedBy { p -> p.price }
                                "Mayor a menor" -> it.sortedByDescending { p -> p.price }
                                else -> it
                            }
                        }
                    ProductList(
                        products = processedProducts,
                        isAdmin = isAdmin,
                        onProductClick = onProductClick,
                        onEdit = onEditProduct,
                        onDelete = {
                            productToDelete = it
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductList(
    products: List<ProductEntity>,
    isAdmin: Boolean,
    onProductClick: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (ProductEntity) -> Unit
) {
    if (products.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No se encontraron productos") }
    } else {
        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(products, key = { it.id }) { product ->
                AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                    ProductCard(
                        product = product,
                        isAdmin = isAdmin,
                        onClick = { onProductClick(product.id) },
                        onEdit = { onEdit(product.id) },
                        onDelete = { onDelete(product) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductCard(
    product: ProductEntity,
    isAdmin: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = product.imageUrl), contentDescription = "Imagen de ${product.name}", modifier = Modifier.size(80.dp).clip(MaterialTheme.shapes.small), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = formatPrice(product.price), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            // 3. Mostramos los botones de admin si corresponde
            if (isAdmin) {
                Column {
                    IconButton(onClick = onEdit) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar producto")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar producto")
                    }
                }
            }
        }
    }
}
