package com.example.shoparoo.ui.checkOut.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.data.repository.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(private val repository: Repository) : ViewModel() {

    // State for order completion
    val completeOrderState = MutableStateFlow<ApiState>(ApiState.Loading)

    // State for draft order deletion
    private val deleteDraftOrderState = MutableStateFlow<ApiState>(ApiState.Loading)

    fun addToCompleteOrder(id: String) {
        viewModelScope.launch {
            completeOrderState.value = ApiState.Loading
            try {
                repository.addToCompleteOrder(id)
                completeOrderState.value = ApiState.Success("Order completed")
            } catch (e: Exception) {
                completeOrderState.value =
                    ApiState.Failure("Failed to complete order: ${e.message}")
            }
        }
    }

    fun deleteOrderFromDraft(id: String) {
        viewModelScope.launch {
            deleteDraftOrderState.value = ApiState.Loading
            delay(1000)  // Simulate delay for realism
            try {
                repository.deleteDraftOrder(id)
                deleteDraftOrderState.value = ApiState.Success("Draft order deleted")
            } catch (e: Exception) {
                deleteDraftOrderState.value =
                    ApiState.Failure("Failed to delete draft order: ${e.message}")
            }
        }
    }
}