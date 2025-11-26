package com.example.resetandreplay.data.repository

import com.example.resetandreplay.data.remote.ApiService
import com.example.resetandreplay.data.remote.dto.RolDto
import com.example.resetandreplay.data.remote.dto.UserDto
import com.example.resetandreplay.data.repository.fakes.FakeUserDao
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class UserRepositoryTest {

    private lateinit var mockApiService: ApiService
    private lateinit var fakeUserDao: FakeUserDao
    private lateinit var userRepository: UserRepository

    // @Before se ejecuta antes de CADA test, asegurando un estado limpio
    @Before
    fun setUp() {
        // 1. Creamos un "mock" de nuestra ApiService (usando MockK)
        mockApiService = mockk()
        // 2. Creamos una instancia de nuestro FakeUserDao
        fakeUserDao = FakeUserDao()
        // 3. Creamos el UserRepository inyectando las dependencias falsas/mockeadas
        userRepository = UserRepository(
            userDao = fakeUserDao,
            apiService = mockApiService
        )
    }

    @Test
    fun `login con credenciales correctas devuelve Success y el UserEntity`() = runBlocking {
        // 1. Arrange (Preparación)
        // Datos falsos que esperamos que devuelva la API
        val fakeUserDto = UserDto(
            id_usuario = 1,
            nombre = "Emilio",
            correo = "emiil@example.com",
            telefono = "123456789",
            foto_perfil = "",
            rol = RolDto(id_rol = 1, nombre = "ADMIN")
        )

        // "Cuando se llame a mockApiService.login() con cualquier argumento,
        // devuelve una respuesta HTTP 200 OK con nuestros datos falsos"
        coEvery { mockApiService.login(any()) } returns Response.success(fakeUserDto)

        // 2. Act (Actuación)
        // Ejecutamos la función que queremos probar
        val result = userRepository.login("emiil@example.com", "password123")

        // 3. Assert (Verificación)
        Assert.assertTrue("El resultado debería ser exitoso", result.isSuccess)
        val userEntity = result.getOrNull()
        Assert.assertNotNull("El UserEntity no debería ser nulo", userEntity)
        Assert.assertEquals("El email no coincide", "emiil@example.com", userEntity?.email)
        Assert.assertTrue("El usuario debería ser admin", userEntity?.isAdmin == true)
    }

    @Test
    fun `login con credenciales incorrectas devuelve Failure`() = runBlocking {
        // 1. Arrange
        // "Cuando se llame a mockApiService.login(), devuelve una respuesta de error 401 Unauthorized"
        // El cuerpo del error se mockea con `relaxed = true` para que no falle al intentar leerlo.
        coEvery { mockApiService.login(any()) } returns Response.error(401, mockk(relaxed = true))

        // 2. Act
        val result = userRepository.login("incorrecto@example.com", "badpass")

        // 3. Assert
        Assert.assertTrue("El resultado debería ser un fallo", result.isFailure)
        val exception = result.exceptionOrNull()
        Assert.assertNotNull("Debería haber una excepción", exception)
        Assert.assertEquals("Email o contraseña incorrectos.", exception?.message)
    }
}