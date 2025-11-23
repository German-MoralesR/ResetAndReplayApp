package com.example.resetandreplay.data.local.purchase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchase: PurchaseEntity)

    @Query("SELECT * FROM purchases WHERE userId = :userId ORDER BY date DESC")
    fun getPurchasesForUser(userId: Long): Flow<List<PurchaseEntity>>
}
