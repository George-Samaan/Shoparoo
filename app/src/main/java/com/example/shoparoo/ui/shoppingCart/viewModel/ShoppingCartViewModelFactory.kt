package com.example.shoparoo.ui.shoppingCart.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shoparoo.data.repository.Repository

class ShoppingCartViewModelFactory(private val repository: Repository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingCartViewModel::class.java)) {
            return ShoppingCartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}