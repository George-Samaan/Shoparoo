package com.example.shoparoo.ui.shoppingCart.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoparoo.data.repository.Repository
import com.example.shoparoo.model.AppliedDiscount
import com.example.shoparoo.model.DraftOrderDetails
import com.example.shoparoo.model.DraftOrderRequest
import com.example.shoparoo.model.LineItem
import com.example.shoparoo.model.ShippingAddress
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ShoppingCartViewModel(private val repository: Repository) : ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    val userMail by lazy {
        firebaseAuth.currentUser?.email
    }

    val _cartItems = MutableStateFlow<List<LineItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    val _draftOrderDetails = MutableStateFlow<DraftOrderDetails?>(null)
    val draftOrderDetails = _draftOrderDetails.asStateFlow()

    val _shippingAddress = MutableStateFlow<Pair<String,String>>(Pair("",""))
    val shippingAddress = _shippingAddress.asStateFlow()

    fun getDraftOrderDetails() {
        viewModelScope.launch {
            repository.getDraftOrder()
                .catch { exception ->
                    Log.e("ShoppingCartViewModel", "Error: ${exception.message}")
                }
                .collect { draftOrderResponse ->
                    val userDraftOrder =
                        draftOrderResponse.draft_orders.find { it.email == userMail }
                    userDraftOrder?.let {
                        _draftOrderDetails.value = it
                    }
                }
        }
    }

    /*    fun applyDiscountToDraftOrder(draftOrderId: Long, discount: AppliedDiscount) {
            viewModelScope.launch {
                val draftOrder = _draftOrderDetails.value?.copy(applied_discount = discount)
                if (draftOrder != null) {
                    val updatedDraftOrder = repository.updateDraftOrder(DraftOrderRequest(draftOrder))
                    _draftOrderDetails.value = updatedDraftOrder as? DraftOrderDetails
                }
            }
        }*/

    fun applyDiscountToDraftOrder(draftOrderId: Long, discount: AppliedDiscount) {
        viewModelScope.launch {
            val currentOrderDetails = _draftOrderDetails.value
            if (currentOrderDetails != null) {
                val updatedOrderDetails = currentOrderDetails.copy(applied_discount = discount)
                repository.updateDraftOrder(DraftOrderRequest(updatedOrderDetails))
                _draftOrderDetails.value = updatedOrderDetails
            }
        }
    }

    fun updateShippingAddress(draftOrderId: Long, address: ShippingAddress) {
        viewModelScope.launch {
            val draftOrder = _draftOrderDetails.value?.copy(shipping_address = address)
            if (draftOrder != null) {
                val updatedDraftOrder = repository.updateDraftOrder(DraftOrderRequest(draftOrder))
//                _draftOrderDetails.value = updatedDraftOrder as? DraftOrderDetails
            }
        }
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

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun clearDiscount() {
        _draftOrderDetails.value = _draftOrderDetails.value?.copy(applied_discount = null)
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
                            val userDraftOrder =
                                draftOrderResponse.draft_orders.find { it.email == userMail }
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
                    val userDraftOrder =
                        draftOrderResponse.draft_orders.find { it.email == userMail }
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
                    it.line_items = it.line_items.filter { it.variant_id != lineItem.variant_id }
                        .toMutableList()

                    if (it.line_items.isEmpty()) {
                        Log.d("RemoveItem", "Deleting draft order ID: ${it.id}")
                        repository.deleteDraftOrder(it.id.toString())
                    } else {
                        Log.d(
                            "RemoveItem",
                            "Updating draft order ID: ${it.id} with remaining items: ${it.line_items.size}"
                        )
                        val order = DraftOrderRequest(userDraftOrder)
                        repository.updateDraftOrder(order)
                    }
                }
            }
        }
    }

    //get address and phone number from firebase
    fun getUserData(): Pair<String,String> {
        var location = ""
        var phoneNum = ""
        db.collection("users").document(firebaseAuth.currentUser!!.uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                     location = document.data?.get("location").toString()
                     phoneNum = document.data?.get("phoneNum").toString()
                    _shippingAddress.value = Pair(location, phoneNum)
                    Log.d("ShoppingCartViewModel", "Location: $location, Phone Number: $phoneNum")
                } else {
                    Log.d("ShoppingCartViewModel", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ShoppingCartViewModel", "get failed with ", exception)
            }
        return Pair(location, phoneNum)
    }

}