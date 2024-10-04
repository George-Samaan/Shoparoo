package com.example.shoparoo.ui.homeScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoparoo.data.db.repository.Repository
import com.example.shoparoo.data.network.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {
    private val _smartCollections = MutableStateFlow<ApiState>(ApiState.Loading)
    val smartCollections: StateFlow<ApiState> get() = _smartCollections

    private val _forYouProducts = MutableStateFlow<ApiState>(ApiState.Loading)
    val forYouProducts: StateFlow<ApiState> get() = _forYouProducts

    fun getSmartCollections() {
        viewModelScope.launch {
            repository.getSmartCollections().catch {
                _smartCollections.value = ApiState.Failure(it.message ?: "Error fetching brands")
            }.collect { brands ->
                _smartCollections.value = ApiState.Success(brands.smartCollections!!)
            }
        }
    }

    fun getForYouProducts() {
        viewModelScope.launch {
            repository.getForYouProducts().catch { exception ->
                _forYouProducts.value =
                    ApiState.Failure(exception.message ?: "Error fetching products")
            }.collect { products ->
                _forYouProducts.value = ApiState.Success(products.products!!)
            }
        }
    }

    fun refreshData() {
        _smartCollections.value = ApiState.Loading
        _forYouProducts.value = ApiState.Loading

        // Fetch data and update states
        getSmartCollections()
        getForYouProducts()
    }
}