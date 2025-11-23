package com.example.resetandreplay.data.repository

import com.example.resetandreplay.data.local.user.UserDao
import com.example.resetandreplay.data.local.user.UserEntity

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)
        return if (user != null && user.password == password) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    suspend fun register(name: String, email: String, phone: String, password: String): Result<Long> {
        val exists = userDao.getByEmail(email) != null
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }
        val id = userDao.insert(
            UserEntity(
                name = name,
                email = email,
                phone = phone,
                password = password
            )
        )
        return Result.success(id)
    }

    suspend fun getUserByEmail(email: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(NoSuchElementException("Usuario no encontrado con ese email"))
        }
    }

    // 1. Nueva función para cambiar la contraseña de un usuario
    suspend fun resetPassword(email: String, newPassword: String): Result<Unit> {
        val user = userDao.getByEmail(email)
        return if (user != null) {
            // Creamos una copia del usuario con la nueva contraseña y lo actualizamos
            val updatedUser = user.copy(password = newPassword)
            userDao.update(updatedUser)
            Result.success(Unit)
        } else {
            Result.failure(NoSuchElementException("No se pudo cambiar la contraseña. Usuario no encontrado."))
        }
    }
}
