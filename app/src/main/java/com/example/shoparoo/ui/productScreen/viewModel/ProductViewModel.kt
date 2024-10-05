package com.example.shoparoo.ui.productScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: Repository) : ViewModel() {

    private val _productsFromBrands = MutableStateFlow<ApiState>(ApiState.Loading)
    val productsFromBrands: StateFlow<ApiState> get() = _productsFromBrands

    fun getProductsFromBrandsId(collectionId: String) {
        _productsFromBrands.value = ApiState.Loading
        viewModelScope.launch {
            repository.getProductsFromBrandsId(collectionId).catch {
                _productsFromBrands.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect {
                val products = it.products ?: emptyList()
                _productsFromBrands.value = ApiState.Success(products)
            }
        }
    }
}