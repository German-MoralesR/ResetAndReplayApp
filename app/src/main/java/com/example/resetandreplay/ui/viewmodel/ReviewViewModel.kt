package com.example.resetandreplay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resetandreplay.data.remote.dto.ResenaDto
import com.example.resetandreplay.data.remote.dto.ResenaRequest
import com.example.resetandreplay.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReviewUiState(
    val reviews: List<ResenaDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val reviewSubmitted: Boolean = false
)

class ReviewViewModel(private val repository: ReviewRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState

    fun loadReviews(productId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.getReviewsForProduct(productId).collect { reviews ->
                    _uiState.update { it.copy(isLoading = false, reviews = reviews) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun submitReview(resenaRequest: ResenaRequest) {
        viewModelScope.launch {
            val result = repository.createReview(resenaRequest)
            if (result.isSuccess) {
                _uiState.update { it.copy(reviewSubmitted = true) }
                // Recargamos las reseñas para mostrar la nueva
                loadReviews(resenaRequest.idProducto)
            } else {
                _uiState.update { it.copy(error = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun clearSubmissionStatus() {
        _uiState.update { it.copy(reviewSubmitted = false, error = null) }
    }
}