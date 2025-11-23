package com.example.resetandreplay.data.local.purchase

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.resetandreplay.data.local.user.UserEntity

@Entity(
    tableName = "purchases",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // Si se borra el usuario, se borran sus compras
        )
    ]
)
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long, // Para saber a qué usuario pertenece la compra
    val itemsDescription: String, // Ej: "3 x Consola Retro, 1 x Joystick Arcade"
    val totalPrice: Double,
    val date: Long // Usaremos un timestamp de la fecha de compra
)
