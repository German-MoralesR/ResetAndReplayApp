package com.example.resetandreplay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resetandreplay.data.local.purchase.PurchaseEntity
import com.example.resetandreplay.data.repository.PurchaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class PurchaseHistoryUiState(
    val purchases: List<PurchaseEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class PurchaseViewModel(private val repository: PurchaseRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PurchaseHistoryUiState())
    val uiState: StateFlow<PurchaseHistoryUiState> = _uiState

    fun getPurchaseHistory(userId: Long) {
        viewModelScope.launch {
            repository.getPurchasesForUser(userId)
                .catch { e -> _uiState.value = PurchaseHistoryUiState(error = e.message) }
                .collect { purchases ->
                    _uiState.value = PurchaseHistoryUiState(purchases = purchases)
                }
        }
    }

    fun savePurchase(purchase: PurchaseEntity) {
        viewModelScope.launch {
            repository.insert(purchase)
        }
    }
}
