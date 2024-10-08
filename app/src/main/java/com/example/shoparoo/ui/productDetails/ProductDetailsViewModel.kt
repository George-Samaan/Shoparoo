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

    private val _isFav = MutableStateFlow<ApiState>(ApiState.Loading)
    val isFav = _singleProductDetail.asStateFlow()

    private val _draftOrder = MutableStateFlow<ApiState>(ApiState.Loading)
    val draftOrder = _draftOrder.asStateFlow()

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

           // _isFav.value = ApiState.Loading
//            repository.getFavProducts().catch {
//                _isFav.value = ApiState.Failure(it.message ?: "Unknown Error")
//                Log.i("ProductDetails", "Error ${it.message}")
//            }.collect {
//                _isFav.value = ApiState.Success(it)
//                Log.i("ProductDetails", "Success ${it.products}")
//            }

        }
    }

    fun getDraftOrder(theSingleProduct: SingleProduct, varient: VariantsItem, isCart: Boolean) {
        viewModelScope.launch {
            _draftOrder.value = ApiState.Loading
            repository.getDraftOrder().catch {
                _draftOrder.value = ApiState.Failure(it.message ?: "Unknown Error")
                Log.i("ProductDetails", "Error ${it.message}")
            }.collect {
                _draftOrder.value = ApiState.Success(it)
                filterByUser(it, theSingleProduct, varient, isCart)
                Log.i("ProductDetails get draft order", "Success ${it.draft_orders}")
            }
        }
    }

    fun filterByUser(
        draftOrdersResponse: DraftOrderResponse,
        theSingleProduct: SingleProduct,
        varient: VariantsItem,
        isCart: Boolean
    ) {
        var user = null as DraftOrderDetails?
        if (isCart) {
            for (draftOrder in draftOrdersResponse.draft_orders!!) {
                if (draftOrder.email == userMail) {
                    // filterByItem(id, draftOrder)
                    Log.i("ProductDetails", "filter by user ${draftOrder.email}")
                    Log.i("ProductDetails", "Draft Order Found ${draftOrder}")
                    user = draftOrder
                }
            }
        }
        else {
            for (draftOrder in draftOrdersResponse.draft_orders!!) {
                if (draftOrder.email == "FAV_"+userMail) {
                    Log.i("ProductDetails", "filter by user ${draftOrder.email}")
                    Log.i("ProductDetails", "Draft Order Found ${draftOrder}")
                    user = draftOrder
                }
            }
        }

        if (user != null && isCart) //varients need to be handled
            filterByItem(user, varient, theSingleProduct, isCart)
        else if (user != null && !isCart) {
          updateFavDraftOrder(user, theSingleProduct, varient)
        }
        else {
            createDraftOrder(theSingleProduct, varient, isCart)
        }
    }

    private fun updateFavDraftOrder(
        draftOrderDetails: DraftOrderDetails,
        theSingleProduct: SingleProduct,
        varient: VariantsItem
    ) {

        var item : DraftOrderDetails? = null
        var  myLineItem : LineItem? = null
        for (line_item in draftOrderDetails.line_items) {
            if (line_item.product_id == varient.productId)
                item = draftOrderDetails
            myLineItem = line_item
        }
       if (item != null) {
      draftOrderDetails.line_items.remove(myLineItem)
           viewModelScope.launch {
               val order = DraftOrderRequest(draftOrderDetails)
               repository.updateDraftOrder(order)
           }
       } else {
           draftOrderDetails.line_items.add(
               setLineItem(theSingleProduct, varient)
           )
           viewModelScope.launch {
               val order = DraftOrderRequest(draftOrderDetails)
               repository.updateDraftOrder(order)
           }
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
        if (exists) { //update count
            updateDraftOrderItemCount(draftOrder)
        } else {  // add new item
            updateDraftOrder(draftOrder, varient, theSingleProduct)
        }
    }

    private fun updateDraftOrder(
        draftOrder: DraftOrderDetails,
        varient: VariantsItem,
        theSingleProduct: SingleProduct
    ) {
        draftOrder.line_items.add(
            setLineItem(theSingleProduct, varient)
        )
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


    fun createDraftOrder(theSingleProduct: SingleProduct, varient: VariantsItem, isCart: Boolean) {
        Log.i("ProductDetails", "createDraftOrder")
        val mail = if (isCart) userMail else "FAV_" + userMail

        viewModelScope.launch {
            var order = DraftOrderRequest(
                DraftOrderDetails(
                    line_items = mutableListOf(
                        setLineItem(theSingleProduct, varient)
                    ),
                    email = mail!!,
                    note = "Order",
                )
            )
            repository.createDraftOrder(order)

        }

    }

    private fun setLineItem(
        theSingleProduct: SingleProduct,
        varient: VariantsItem,
    ) = LineItem(
        title = theSingleProduct.product!!.title!!,
        price = varient.price!!,
        quantity = 1,
        variant_id = varient.id.toString(),
        product_id = theSingleProduct.product.id!!,
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