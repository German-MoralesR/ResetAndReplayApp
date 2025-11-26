package com.example.resetandreplay.data.repository.fakes

import com.example.resetandreplay.data.local.user.UserDao
import com.example.resetandreplay.data.local.user.UserEntity

// Simula el comportamiento de la base de datos usando una lista mutable.
class FakeUserDao : UserDao {

    // Nuestra "base de datos" en memoria
    private val userStorage = mutableListOf<UserEntity>()

    override suspend fun insert(user: UserEntity): Long {
        // Simulamos la generación de un ID autoincremental
        val nextId = (userStorage.maxOfOrNull { it.id } ?: 0L) + 1
        val newUser = user.copy(id = nextId)
        userStorage.add(newUser)
        return nextId
    }

    override suspend fun update(user: UserEntity) {
        val index = userStorage.indexOfFirst { it.id == user.id }
        if (index != -1) {
            userStorage[index] = user
        }
    }

    override suspend fun getByEmail(email: String): UserEntity? {
        // Buscamos en nuestra lista en memoria
        return userStorage.firstOrNull { it.email == email }
    }

    override suspend fun count(): Int {
        return userStorage.size
    }

    override suspend fun getAll(): List<UserEntity> {
        return userStorage.toList() // Devolvemos una copia para evitar modificaciones externas
    }

    // Función extra para los tests, para limpiar los datos entre pruebas
    fun clear() {
        userStorage.clear()
    }
}