package com.example.shoparoo.ui.shoppingCart.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoparoo.data.repository.Repository
import com.example.shoparoo.model.DraftOrderDetails
import com.example.shoparoo.model.DraftOrderRequest
import com.example.shoparoo.model.DraftOrderResponse
import com.example.shoparoo.model.LineItem
import com.example.shoparoo.model.Property
import com.example.shoparoo.model.SingleProduct
import com.example.shoparoo.model.VariantsItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ShoppingCartViewModel(private val repository: Repository):ViewModel() {
    private var conversionRate: Double = 1.0 // Default conversion rate

    val userMail by lazy {
        FirebaseAuth.getInstance().currentUser?.email
    }

    private val _cartItems = MutableStateFlow<List<LineItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

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

    private fun setLineItem(
        theSingleProduct: SingleProduct,
        varient: VariantsItem,
    ) = LineItem(
        title = theSingleProduct.product!!.title!!,
        price = (varient.price?.toFloatOrNull()?.times(conversionRate)).toString(),
        quantity = 1,
        variant_id = varient.id.toString(),
        product_id = theSingleProduct.product.id!!,
        properties = listOf(
            Property("imageUrl", theSingleProduct.product.images!![0]!!.src!!),
            Property("Color", varient.option1!!),
            Property("Size", varient.option2!!),
        )
    )

    fun filterByUser(
        draftOrdersResponse: DraftOrderResponse,
        theSingleProduct: SingleProduct,
        varient: VariantsItem,
        isCart: Boolean
    ) {
        Log.i("ProductDetailsviewModel", "filter by user ${userMail}")


        var user : DraftOrderDetails? = null
        var check : Boolean = false
        if (isCart) {
            for (draftOrder in draftOrdersResponse.draft_orders!!) {
                if (draftOrder.email == userMail) {
                    // filterByItem(id, draftOrder)
                    Log.i("ProductDetailsviewModel", "filter by user ${draftOrder.email}")
                    Log.i("ProductDetailsviewModel", "Draft Order Found ${draftOrder}")

                    user = draftOrder
                    check = true
                }
            }
        } else {
            for (draftOrder in draftOrdersResponse.draft_orders!!) {
                if (draftOrder.email == "FAV_"+userMail) {
                    Log.i("ProductDetailsviewModel", "filter by user ${draftOrder.email}")
                    Log.i("ProductDetailsviewModel", "Draft Order Found ${draftOrder}")
                    user = draftOrder
                    check = true
                }
            }
        }

        if (check) {
            if (isCart)
                filterByItem(user!!, varient, theSingleProduct, isCart)
        }else{
            createDraftOrder(theSingleProduct, varient, isCart)
        }

    }

    fun createDraftOrder(theSingleProduct: SingleProduct, varient: VariantsItem, isCart: Boolean) {
        Log.i("ProductDetailsviewModel", "createDraftOrder")
        val mail = if (isCart) userMail else "FAV_" + userMail
        viewModelScope.launch {
            val order = DraftOrderRequest(
                DraftOrderDetails(
                    line_items = mutableListOf(setLineItem(theSingleProduct, varient)),
                    email = mail!!,
                    note = "Order",
                )
            )
            repository.createDraftOrder(order)
        }
    }


    fun filterByItem(
        draftOrder: DraftOrderDetails,
        varient: VariantsItem,
        theSingleProduct: SingleProduct,
        inCart: Boolean
    ) {
        var exists = false
        for (line_item in draftOrder.line_items) {
            if (line_item.variant_id == varient.id.toString())
                exists = true
        }
        if (exists) {
            updateDraftOrderItemCount(draftOrder)
        } else {
            updateDraftOrder(draftOrder, varient, theSingleProduct)
        }
    }

    private fun updateDraftOrder(
        draftOrder: DraftOrderDetails,
        varient: VariantsItem,
        theSingleProduct: SingleProduct
    ) {
        draftOrder.line_items.add(setLineItem(theSingleProduct, varient))
        viewModelScope.launch {
            val order = DraftOrderRequest(draftOrder)
            repository.updateDraftOrder(order)
        }
    }

    fun updateDraftOrderItemCount(draftOrder: DraftOrderDetails) {
        draftOrder.line_items[0].quantity += 1
        viewModelScope.launch {
            val order = DraftOrderRequest(draftOrder)
            repository.updateDraftOrder(order)
        }
    }

    fun incrementItemCount(lineItem: LineItem) {
        viewModelScope.launch {
            val updatedItems = _cartItems.value.map {
                if (it.variant_id == lineItem.variant_id) {
                    it.copy(quantity = it.quantity + 1)
                } else {
                    it
                }
            }
            _cartItems.value = updatedItems
            updateDraftOrderItemCount(lineItem) // Update your draft order in the repository if necessary
        }
    }

    fun decrementItemCount(lineItem: LineItem) {
        viewModelScope.launch {
            if (lineItem.quantity > 1) {
                val updatedItems = _cartItems.value.map {
                    if (it.variant_id == lineItem.variant_id) {
                        it.copy(quantity = it.quantity - 1)
                    } else {
                        it
                    }
                }
                _cartItems.value = updatedItems
                updateDraftOrderItemCount(lineItem) // Update your draft order in the repository if necessary
            } else if (lineItem.quantity == 1) {
                // Optionally, you can remove the item from the cart if quantity goes to 0
                val updatedItems = _cartItems.value.filterNot { it.variant_id == lineItem.variant_id }
                _cartItems.value = updatedItems
                // Remove item from draft order in your repository if necessary
            }
        }
    }

    private fun updateDraftOrderItemCount(lineItem: LineItem) {
        // Logic to update the draft order in your repository
        // You will need to find the corresponding draft order and update the quantity of the line item
    }

/*
    fun incrementItemCount(lineItem: LineItem) {
        viewModelScope.launch {
            // Increment the quantity in your data model
            val updatedItem = lineItem.copy(quantity = lineItem.quantity + 1)

            // Create a new list with the updated item
            _cartItems.value = _cartItems.value.map {
                if (it.variant_id == lineItem.variant_id) updatedItem else it
            }

            // Update the draft order with the new quantity
            updateDraftOrderItemCount(updatedItem)
        }
    }

    fun decrementItemCount(lineItem: LineItem) {
        viewModelScope.launch {
            if (lineItem.quantity > 0) {
                val updatedItem = lineItem.copy(quantity = lineItem.quantity - 1)

                // Create a new list with the updated item
                _cartItems.value = _cartItems.value.map {
                    if (it.variant_id == lineItem.variant_id) updatedItem else it
                }

                // Update the draft order with the new quantity
                updateDraftOrderItemCount(updatedItem)
            }
        }
    }


    private fun updateDraftOrderItemCount(lineItem: LineItem) {
        viewModelScope.launch {
            // Find the draft order and update the corresponding line item quantity
            val draftOrder = _cartItems.value.find { it.variant_id == lineItem.variant_id }

            if (draftOrder != null) {
                draftOrder.quantity = lineItem.quantity // Update the quantity

                val order = DraftOrderRequest(DraftOrderDetails(line_items = _cartItems.value.toMutableList())) // Send the updated list
                repository.updateDraftOrder(order) // Update the repository
            }
        }
    }*/


}



