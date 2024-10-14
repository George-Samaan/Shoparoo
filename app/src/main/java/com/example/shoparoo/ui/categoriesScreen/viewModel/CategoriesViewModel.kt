package com.example.shoparoo.ui.categoriesScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CategoriesViewModel(private val repository: Repository) : ViewModel() {
    private val _womenProducts = MutableStateFlow<ApiState>(ApiState.Loading)
    val womenProducts: StateFlow<ApiState> get() = _womenProducts

    private val _salesProducts = MutableStateFlow<ApiState>(ApiState.Loading)
    val salesProducts: StateFlow<ApiState> get() = _salesProducts

    private val _mensProducts = MutableStateFlow<ApiState>(ApiState.Loading)
    val mensProducts: StateFlow<ApiState> get() = _mensProducts

    private val _kidsProducts = MutableStateFlow<ApiState>(ApiState.Loading)
    val kidsProducts: StateFlow<ApiState> get() = _kidsProducts

    fun getWomenProducts() {
        _womenProducts.value = ApiState.Loading
        viewModelScope.launch {
            repository.getWomenProducts().catch {
                _womenProducts.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect {
                val products = it.products ?: emptyList()
                _womenProducts.value = ApiState.Success(products)
            }
        }
    }

    fun getMensProducts() {
        _mensProducts.value = ApiState.Loading
        viewModelScope.launch {
            repository.getMensProducts().catch {
                _mensProducts.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect {
                val products = it.products ?: emptyList()
                _mensProducts.value = ApiState.Success(products)
            }
        }
    }

    fun getSalesProducts() {
        _salesProducts.value = ApiState.Loading
        viewModelScope.launch {
            repository.getSalesProducts().catch {
                _salesProducts.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect {
                val products = it.products ?: emptyList()
                _salesProducts.value = ApiState.Success(products)
            }
        }
    }

    fun getKidsProducts() {
        _kidsProducts.value = ApiState.Loading
        viewModelScope.launch {
            repository.getKidsProducts().catch {
                _kidsProducts.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect {
                val products = it.products ?: emptyList()
                _kidsProducts.value = ApiState.Success(products)
            }
        }
    }
}