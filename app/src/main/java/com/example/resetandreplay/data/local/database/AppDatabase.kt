package com.example.resetandreplay.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.resetandreplay.R
import com.example.resetandreplay.data.local.product.ProductDao
import com.example.resetandreplay.data.local.product.ProductEntity
import com.example.resetandreplay.data.local.purchase.PurchaseDao
import com.example.resetandreplay.data.local.purchase.PurchaseEntity
import com.example.resetandreplay.data.local.user.UserDao
import com.example.resetandreplay.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class, ProductEntity::class, PurchaseEntity::class], // 1. Añadimos la nueva entidad
    version = 7, // 2. Incrementamos la versión
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun purchaseDao(): PurchaseDao // 3. Exponemos el nuevo DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "resetandreplay.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val userdao = getInstance(context).userDao()
                                val productdao = getInstance(context).productDao()

                                // Precarga de usuarios
                                val userSeed = listOf(
                                    UserEntity(name = "Admin", email = "admin@duoc.cl", phone = "+56911111111", password = "Admin123!", isAdmin = true),
                                    UserEntity(name = "Usuario", email = "user@duoc.cl", phone = "+56922222222", password = "User123!", isAdmin = false)
                                )
                                if (userdao.count() == 0) {
                                    userSeed.forEach { userdao.insert(it) }
                                }

                                // Precarga de productos
//                                val productSeed = listOf(
//                                    ProductEntity(name = "Consola Retro", description = "Consola con 500 juegos clásicos de 8-bit.", price = 99990.0, imageUrl = R.drawable.consola_retro, stock = 10, sku = "CON-RET-001", category = "Consolas"),
//                                    ProductEntity(name = "Joystick Arcade", description = "Joystick estilo arcade para PC y consolas.", price = 49990.0, imageUrl = R.drawable.joystick_arcade, stock = 5, sku = "ACC-ARC-002", category = "Accesorios"),
//                                    ProductEntity(name = "Pistola de Luz", description = "Pistola para juegos de caza de patos retro.", price = 69990.0, imageUrl = R.drawable.pistola_luz, stock = 15, sku = "ACC-LIG-003", category = "Accesorios"),
//                                    ProductEntity(name = "Consola SNES", description = "Consola SNES en buen estado de funcionamiento.", price = 119990.0, imageUrl = R.drawable.consola_snes, stock = 7, sku = "CON-RET-004", category = "Consolas"),
//                                    ProductEntity(name = "Control SNES", description = "Control réplica con cable largo.", price = 19990.0, imageUrl = R.drawable.control_snes, stock = 21, sku = "ACC-RET-005", category = "Accesorios"),
//                                    ProductEntity(name = "Donkey Kong Country (SNES)", description = "Cartucho original de Donkey Kong.", price = 49990.0, imageUrl = R.drawable.dk_country, stock = 3, sku = "JGO-RET-006", category = "Juegos"),
//                                    ProductEntity(name = "Gameboy Color", description = "Game Boy Color + cargador + juego.", price = 89990.0, imageUrl = R.drawable.gb_color, stock = 6, sku = "CON-RET-007", category = "Consolas"),
//                                    ProductEntity(name = "Pokemon Cristal", description = "Cartucho Pokémon Crystal para GBC.", price = 49990.0, imageUrl = R.drawable.pokemon_crystal, stock = 3, sku = "JGO-RET-008", category = "Juegos"),
//                                    ProductEntity(name = "Pokemon Gold", description = "Cartucho Pokémon Gold para GBC.", price = 49990.0, imageUrl = R.drawable.pokemon_gold, stock = 2, sku = "JGO-RET-009", category = "Juegos"),
//                                    ProductEntity(name = "Pokemon Silver", description = "Cartucho Pokémon Silver para GBC.", price = 49990.0, imageUrl = R.drawable.pokemon_silver, stock = 4, sku = "JGO-RET-010", category = "Juegos"),
//                                    ProductEntity(name = "Pokemon Snap", description = "Cartucho original, probado.", price = 39990.0, imageUrl = R.drawable.pokemon_snap, stock = 8, sku = "JGO-RET-011", category = "Juegos"),
//                                    ProductEntity(name = "Polera SNES", description = "Polera de algodón, diseño SNES pixel art.", price = 29990.0, imageUrl = R.drawable.polera_snes, stock = 22, sku = "MRC-RET-012", category = "Merchandising"),
//                                    ProductEntity(name = "PlayStation 1", description = "Consola PS1 edición Slim.", price = 99990.0, imageUrl = R.drawable.ps1, stock = 2, sku = "CON-RET-013", category = "Consola"),
//                                    ProductEntity(name = "SEGA VR - Headset", description = "Auriculares retro compatibles.", price = 149990.0, imageUrl = R.drawable.sega_vr, stock = 1, sku = "ACC-RET-014", category = "Accesorios"),
//                                    ProductEntity(name = "Street Fighter 2", description = "Cartucho original de Street Fighter II.", price = 39990.0, imageUrl = R.drawable.street_fighter2, stock = 9, sku = "JGO-RET-015", category = "Juegos"),
//                                    ProductEntity(name = "Super Mario World", description = "Cartucho original de Super Mario World para SNES.", price = 44990.0, imageUrl = R.drawable.super_mario_world, stock = 12, sku = "JGO-RET-016", category = "Juegos"),
//                                    ProductEntity(name = "The Legend Of Zelda - Majora's Mask", description = "Cartucho N64 en excelente estado.", price = 44990.0, imageUrl = R.drawable.tloz_majoras_mask, stock = 6, sku = "JGO-RET-017", category = "Juegos"),
//                                    ProductEntity(name = "The Legend Of Zelda - Ocarina of Time", description = "Cartucho N64, versión completa.", price = 89990.0, imageUrl = R.drawable.tloz_ocarina, stock = 1, sku = "JGO-RET-018", category = "Juegos")
//                                )
//                                if (productdao.count() == 0) {
//                                    productSeed.forEach { productdao.insert(it) }
//                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
