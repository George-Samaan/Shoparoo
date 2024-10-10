package com.example.shoparoo.ui.checkOut.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoparoo.data.repository.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaymentViewModel(private val repository: Repository) : ViewModel() {


    fun addToCompleteOrder(id: String) {
        viewModelScope.launch {
            repository.addToCompleteOrder(id)
        }
    }

    fun deleteOrderFromDraft(id: String) {
        viewModelScope.launch {
            delay(1000)
            repository.deleteDraftOrder(id)
        }
    }
}