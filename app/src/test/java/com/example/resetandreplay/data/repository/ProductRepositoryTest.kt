package com.example.resetandreplay.data.repository

import com.example.resetandreplay.data.remote.InventoryApiService
import com.example.resetandreplay.data.remote.dto.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ProductRepositoryTest {

    // 1. Declaramos el mock de la API y el repositorio a probar
    private lateinit var mockInventoryApi: InventoryApiService
    private lateinit var productRepository: ProductRepository

    // 2. Preparamos el entorno antes de cada test
    @Before
    fun setUp() {
        // Creamos un "mock" o doble de nuestro InventoryApiService
        mockInventoryApi = mockk()

        // Creamos la instancia del ProductRepository, inyectando nuestro mock.
        // Esto requiere una pequeña modificación en ProductRepository.
        productRepository = ProductRepository(apiService = mockInventoryApi)
    }

    @Test
    fun `getAllProducts cuando la API devuelve datos, retorna un Flow con la lista de ProductEntity`() = runBlocking {
        // 1. Arrange (Preparación)
        // Creamos una lista falsa de DTOs, que es lo que simulará devolver la API.
        val fakeProductDtoList = listOf(
            ProductDto(
                id_producto = 1,
                nombre = "Consola Retro",
                descripcion = "Consola con 500 juegos",
                precio = 99990.0,
                stock = 10,
                sku = "CON-RET-001",
                categoria = CategoriaDto(1, "Consolas"),
                plataforma = PlataformaDto(1, "NES"),
                estado = EstadoDto(1, "Nuevo"),
                fotos = emptyList()
            )
        )

        // "Cuando se llame a mockInventoryApi.getAllProducts(),
        // devuelve una respuesta exitosa (200 OK) con nuestra lista falsa"
        coEvery { mockInventoryApi.getAllProducts() } returns Response.success(fakeProductDtoList)

        // 2. Act (Actuación)
        // Llamamos al método que queremos probar y recolectamos el primer valor del Flow.
        val resultFlow = productRepository.getAllProducts()
        val productEntities = resultFlow.first() // Obtenemos el resultado del Flow

        // 3. Assert (Verificación)
        // Verificamos que la lista no esté vacía y que los datos se hayan mapeado correctamente.
        assertNotNull("La lista de entidades no debería ser nula", productEntities)
        assertEquals("La lista debería contener 1 producto", 1, productEntities.size)
        assertEquals("El nombre del primer producto no coincide", "Consola Retro", productEntities[0].name)
        assertEquals("La categoría del primer producto no coincide", "Consolas", productEntities[0].category)
    }
}