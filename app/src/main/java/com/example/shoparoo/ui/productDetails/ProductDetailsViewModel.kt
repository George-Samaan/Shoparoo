package com.example.shoparoo.ui.productDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProductDetailsViewModel(private val repository: Repository) : ViewModel() {
    private val _singleProductDetail = MutableStateFlow<ApiState>(ApiState.Loading)
    val singleProductDetail = _singleProductDetail.asStateFlow()

    fun getSingleProductDetail(productId: String) {
        viewModelScope.launch {
            _singleProductDetail.value = ApiState.Loading
            repository.getSingleProductFromId(productId).catch {
                _singleProductDetail.value = ApiState.Failure(it.message ?: "Unknown Error")
                Log.i("ProductDetails", "Error ${it.message}")
            }.collect {
                _singleProductDetail.value = ApiState.Success(it)
                Log.i("ProductDetailsssss", "Success ${it.product!!.bodyHtml}")

            }
        }
    }

}

class ProductDetailsViewModelFactory(private val repository: Repository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailsViewModel::class.java)) {
            return ProductDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}