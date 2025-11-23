package com.example.resetandreplay.data.repository

import com.example.resetandreplay.data.local.purchase.PurchaseDao
import com.example.resetandreplay.data.local.purchase.PurchaseEntity
import kotlinx.coroutines.flow.Flow

class PurchaseRepository(private val purchaseDao: PurchaseDao) {

    fun getPurchasesForUser(userId: Long): Flow<List<PurchaseEntity>> {
        return purchaseDao.getPurchasesForUser(userId)
    }

    suspend fun insert(purchase: PurchaseEntity) {
        purchaseDao.insert(purchase)
    }
}
