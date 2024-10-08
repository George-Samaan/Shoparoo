package com.example.shoparoo.ui.ordersScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shoparoo.data.repository.Repository

@Suppress("UNCHECKED_CAST")
class OrdersViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrdersViewModel::class.java)) {
            return OrdersViewModel(repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}