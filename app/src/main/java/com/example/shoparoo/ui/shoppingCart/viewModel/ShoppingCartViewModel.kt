package com.example.shoparoo.ui.shoppingCart.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoparoo.data.network.ApiServices
import com.example.shoparoo.data.repository.Repository
import com.example.shoparoo.model.AppliedDiscount
import com.example.shoparoo.model.DraftOrderDetails
import com.example.shoparoo.model.DraftOrderRequest
import com.example.shoparoo.model.LineItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ShoppingCartViewModel(private val repository: Repository):ViewModel() {


    val userMail by lazy {
        FirebaseAuth.getInstance().currentUser?.email
    }

    private val _cartItems = MutableStateFlow<List<LineItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    private val _draftOrderDetails = MutableStateFlow<DraftOrderDetails?>(null)
    val draftOrderDetails = _draftOrderDetails.asStateFlow()


    fun getDraftOrderDetails() {
        viewModelScope.launch {
            repository.getDraftOrder()
                .catch { exception ->
                    Log.e("ShoppingCartViewModel", "Error: ${exception.message}")
                }
                .collect { draftOrderResponse ->
                    val userDraftOrder = draftOrderResponse.draft_orders.find { it.email == userMail }
                    userDraftOrder?.let {
                        _draftOrderDetails.value = it
                    }
                }
        }
    }

    fun applyDiscountToDraftOrder(draftOrderId: Long, discount: AppliedDiscount) {
        viewModelScope.launch {
            val draftOrder = _draftOrderDetails.value?.copy(applied_discount = discount)
            if (draftOrder != null) {
                val updatedDraftOrder = repository.updateDraftOrder(DraftOrderRequest(draftOrder))
                _draftOrderDetails.value = updatedDraftOrder as? DraftOrderDetails
            }
        }
    }

    fun clearDiscount() {
        _draftOrderDetails.value = _draftOrderDetails.value?.copy(applied_discount = null)
    }


    fun getCartItems() {
        viewModelScope.launch {
            repository.getDraftOrder().catch {
                Log.e("ProductDetailsViewModel", "Error: ${it.message}")
            }.collect { draftOrderResponse ->
                val userDraftOrder = draftOrderResponse.draft_orders.find { it.email == userMail }
                userDraftOrder?.let {
                    _cartItems.value = it.line_items
                }
            }
        }
    }


    fun increaseQuantity(lineItem: LineItem) {
        _cartItems.value = _cartItems.value.map {
            if (it.variant_id == lineItem.variant_id) {
                it.copy(quantity = it.quantity + 1)
            } else {
                it
            }
        }
        viewModelScope.launch {
            repository.getDraftOrder().collect { draftOrderResponse ->
                val userDraftOrder = draftOrderResponse.draft_orders.find { it.email == userMail }
                userDraftOrder?.let {
                    val item = it.line_items.find { it.variant_id == lineItem.variant_id }
                    item?.let {
                        item.quantity += 1
                        val order = DraftOrderRequest(userDraftOrder)
                        repository.updateDraftOrder(order)
                        repository.getDraftOrder().collect { draftOrderResponse ->
                            val userDraftOrder = draftOrderResponse.draft_orders.find { it.email == userMail }
                            userDraftOrder?.let {
                                _cartItems.value = it.line_items
                            }
                        }
                    }
                }
            }
        }
    }


    fun decreaseQuantity(lineItem: LineItem) {
        if (lineItem.quantity > 0) {
            _cartItems.value = _cartItems.value.map {
                if (it.variant_id == lineItem.variant_id) {
                    it.copy(quantity = it.quantity - 1)
                } else {
                    it
                }
            }

            viewModelScope.launch {
                repository.getDraftOrder().collect { draftOrderResponse ->
                    val userDraftOrder = draftOrderResponse.draft_orders.find { it.email == userMail }
                    userDraftOrder?.let {
                        val item = it.line_items.find { it.variant_id == lineItem.variant_id }
                        item?.let {
                            item.quantity -= 1

                            if (item.quantity == 0) {
                                removeItem(item)
                            } else {
                                val order = DraftOrderRequest(userDraftOrder)
                                repository.updateDraftOrder(order)
                            }
                        }
                    }
                }
            }
        }
    }


    fun removeItem(lineItem: LineItem) {
        _cartItems.value = _cartItems.value.filter { it.variant_id != lineItem.variant_id }

        viewModelScope.launch {
            repository.getDraftOrder().collect { draftOrderResponse ->
                val userDraftOrder = draftOrderResponse.draft_orders.find { it.email == userMail }
                userDraftOrder?.let {
                    it.line_items = it.line_items.filter { it.variant_id != lineItem.variant_id }.toMutableList()

                    if (it.line_items.isEmpty()) {
                        Log.d("RemoveItem", "Deleting draft order ID: ${it.id}")
                        repository.deleteDraftOrder(it.id.toString())
                    } else {
                        Log.d("RemoveItem", "Updating draft order ID: ${it.id} with remaining items: ${it.line_items.size}")
                        val order = DraftOrderRequest(userDraftOrder)
                        repository.updateDraftOrder(order)
                    }
                }
            }
        }
    }


}

