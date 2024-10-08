package com.example.shoparoo.ui.ordersScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class OrdersViewModel(private val repository: Repository) : ViewModel() {

    private val _orders = MutableStateFlow<ApiState>(ApiState.Loading)
    val orders: MutableStateFlow<ApiState> get() = _orders

    fun getOrders() {
        _orders.value = ApiState.Loading
        viewModelScope.launch {
            repository.getOrders().catch {
                _orders.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect {
                val orders = it.orders ?: emptyList()
                _orders.value = ApiState.Success(orders)
            }
        }
    }
}