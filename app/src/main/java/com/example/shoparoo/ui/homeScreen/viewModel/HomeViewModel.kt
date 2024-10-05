package com.example.shoparoo.ui.homeScreen.viewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.data.repository.Repository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {
    private val _smartCollections = MutableStateFlow<ApiState>(ApiState.Loading)
    val smartCollections: StateFlow<ApiState> get() = _smartCollections

    private val _forYouProducts = MutableStateFlow<ApiState>(ApiState.Loading)
    val forYouProducts: StateFlow<ApiState> get() = _forYouProducts

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> get() = _userName

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading // Expose it

    init {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val db = Firebase.firestore
        db.collection("users").document(firebaseAuth.currentUser?.uid ?: "Guest")
            .get()
            .addOnSuccessListener { result ->
                _userName.value = result.data?.get("name") as? String
                _isLoading.value = false // Set loading to false after fetching the name
                Log.d(TAG, "getSmartCollections: ${_userName.value}")
            }
            .addOnFailureListener {
                _isLoading.value = false // Set loading to false if there is an error
            }
    }

    fun getSmartCollections() {
        _smartCollections.value = ApiState.Loading
        viewModelScope.launch {
            repository.getSmartCollections().catch {
                _smartCollections.value = ApiState.Failure(it.message ?: "Error fetching brands")
            }.collect { brands ->
                _smartCollections.value = ApiState.Success(brands.smartCollections!!)
            }
        }
    }

    fun getForYouProducts() {
        _forYouProducts.value = ApiState.Loading
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