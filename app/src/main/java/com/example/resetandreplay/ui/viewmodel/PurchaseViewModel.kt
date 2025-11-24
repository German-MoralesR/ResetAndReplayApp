package com.example.resetandreplay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resetandreplay.data.remote.dto.CompraDto
import com.example.resetandreplay.data.remote.dto.CompraRequest
import com.example.resetandreplay.data.repository.PurchaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Un único UiState para manejar tanto el historial como el estado de una nueva compra
data class PurchaseUiState(
    val purchaseHistory: List<CompraDto> = emptyList(), // Para el historial
    val isLoading: Boolean = false,
    val error: String? = null,
    val purchaseSuccess: Boolean = false // Flag para indicar si la última compra fue exitosa
)

class PurchaseViewModel(private val repository: PurchaseRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PurchaseUiState())
    val uiState: StateFlow<PurchaseUiState> = _uiState

    fun getPurchaseHistory(userId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.getPurchasesForUser(userId).collect { purchases ->
                    _uiState.update { it.copy(isLoading = false, purchaseHistory = purchases) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // NUEVA FUNCIÓN PARA CREAR LA COMPRA
    fun createPurchase(compraRequest: CompraRequest) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, purchaseSuccess = false) }
            val result = repository.createPurchase(compraRequest)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, purchaseSuccess = true) }
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido al realizar la compra"
                _uiState.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }

    // Función para resetear el estado después de una operación
    fun clearPurchaseResult() {
        _uiState.update { it.copy(purchaseSuccess = false, error = null) }
    }
}
