package com.example.shoparoo.ui.productDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shoparoo.data.network.ApiState
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

class ProductDetailsViewModel(private val repository: Repository) : ViewModel() {
    private val _singleProductDetail = MutableStateFlow<ApiState>(ApiState.Loading)
    val singleProductDetail = _singleProductDetail.asStateFlow()

    private val _draftOrder = MutableStateFlow<ApiState>(ApiState.Loading)
    val draftOrder = _draftOrder.asStateFlow()

    private val _exists = MutableStateFlow<Boolean>(false)
    var exists = _exists.asStateFlow()

    val userMail by lazy {
        FirebaseAuth.getInstance().currentUser?.email
    }

    fun getSingleProductDetail(productId: String) {
        viewModelScope.launch {
            _singleProductDetail.value = ApiState.Loading
            repository.getSingleProductFromId(productId).catch {
                _singleProductDetail.value = ApiState.Failure(it.message ?: "Unknown Error")
                Log.i("ProductDetails", "Error ${it.message}")
            }.collect {
                _singleProductDetail.value = ApiState.Success(it)
                Log.i("ProductDetailsssss", "Success ${it.product!!.bodyHtml}")
                //getDraftOrder(it, selected.value!!)
            }
        }
    }

    fun getDraftOrder(theSingleProduct: SingleProduct, varient: VariantsItem) {
        viewModelScope.launch {
            _draftOrder.value = ApiState.Loading
            repository.getDraftOrder().catch {
                _draftOrder.value = ApiState.Failure(it.message ?: "Unknown Error")
                Log.i("ProductDetails", "Error ${it.message}")
            }.collect {
                _draftOrder.value = ApiState.Success(it)
               filterByUser(it, theSingleProduct, varient)
                Log.i("ProductDetails get draft order", "Success ${it.draft_orders}")
            }
        }
    }

    fun filterByUser(
        draftOrdersResponse: DraftOrderResponse,
        theSingleProduct: SingleProduct,
        varient: VariantsItem
    ) {
        var user = null as DraftOrderDetails?
        for (draftOrder in draftOrdersResponse.draft_orders!!) {
            if (draftOrder.email == userMail) {
                // filterByItem(id, draftOrder)
                Log.i("ProductDetails", "filter by user ${draftOrder.email}")
                 Log.i("ProductDetails", "Draft Order Found ${draftOrder}")
                user = draftOrder
            }
        }
        if (user != null) //varients need to be handled
            filterByItem(varient.id.toString(), user)
        else {
            createDraftOrder(theSingleProduct, varient)
        }
    }

    fun filterByItem(varientId: String, draftOrder: DraftOrderDetails) {
       var exists   = false
     for (line_item in draftOrder.line_items){
         if (line_item.variant_id==varientId)
            exists = true
     }
        if (exists)
            _exists.value = true

    }

    fun createDraftOrder(theSingleProduct: SingleProduct, varient: VariantsItem) {
        Log.i("ProductDetails", "createDraftOrder")
        viewModelScope.launch {
            var order = DraftOrderRequest(
                DraftOrderDetails(
                    line_items = listOf(
                        LineItem(
                            title = theSingleProduct.product!!.title!!,
                            price = varient.price!!,
                            quantity = 1,
                            variant_id =varient.id.toString(),
                            properties = listOf(
                                Property(
                                    "imageUrl",
                                    theSingleProduct.product.images!![0]!!.src!!
                                ),
                                Property(
                                    "Color",
                                   varient.option1!!
                                ),
                                Property(
                                    "Size",
                                    varient.option2!!
                                )
                            )
                        )
                    ),
                    email = userMail!!,
                    note = "Order",
                )
            )
            repository.createDraftOrder(order)

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