package com.example.shoparoo.ui.productDetails.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.data.network.currencyApi
import com.example.shoparoo.data.repository.Repository
import com.example.shoparoo.model.DraftOrderDetails
import com.example.shoparoo.model.DraftOrderRequest
import com.example.shoparoo.model.DraftOrderResponse
import com.example.shoparoo.model.LineItem
import com.example.shoparoo.model.Property
import com.example.shoparoo.model.SingleProduct
import com.example.shoparoo.model.VariantsItem
import com.example.shoparoo.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProductDetailsViewModel(private val repository: Repository) : ViewModel() {
    private var conversionRate: Double = 1.0 // Default conversion rate


    private val _singleProductDetail = MutableStateFlow<ApiState>(ApiState.Loading)
    val singleProductDetail = _singleProductDetail.asStateFlow()

    private val _isFav = MutableStateFlow<Boolean>(false)
    var isFav = _isFav.asStateFlow()

    private val _draftOrder = MutableStateFlow<ApiState>(ApiState.Loading)
    val draftOrder = _draftOrder.asStateFlow()

    val userMail by lazy {
        FirebaseAuth.getInstance().currentUser?.email
    }



    fun getSingleProductDetail(productId: String, selectedCurrency: String, context: Context) {
        viewModelScope.launch {
            _singleProductDetail.value = ApiState.Loading
            repository.getSingleProductFromId(productId).catch {
                _singleProductDetail.value = ApiState.Failure(it.message ?: "Unknown Error")
                Log.i("ProductDetailsviewModel", "Error ${it.message}")
            }.collect {
                _singleProductDetail.value = ApiState.Success(it)

                Log.i("ProductDetailsviewModel", "Success ${it.product!!.bodyHtml}")
                getDraftOrder(it, it.product.variants!![0]!!, false, true)

                Log.i("ProductDetailsssss", "Success ${it.product!!.bodyHtml}")
                fetchConversionRate(context, selectedCurrency)


            }
        }
    }

    fun getDraftOrder(
        theSingleProduct: SingleProduct,
        varient: VariantsItem,
        isCart: Boolean,
        infav: Boolean = false
    ) {
        Log.i("ProductDetailsviewModel", "get draft order bOOL ")
        viewModelScope.launch {
            _draftOrder.value = ApiState.Loading
            repository.getDraftOrder().catch {
                _draftOrder.value = ApiState.Failure(it.message ?: "Unknown Error")
                Log.i("ProductDetailsviewModel", "Error ${it.message}")
            }.collect {
                _draftOrder.value = ApiState.Success(it)
                filterByUser(it, theSingleProduct, varient, isCart, infav)
                Log.i("ProductDetailsviewModel get draft order", "Success ")
            }
        }
    }

    fun filterByUser(
        draftOrdersResponse: DraftOrderResponse,
        theSingleProduct: SingleProduct,
        varient: VariantsItem,
        isCart: Boolean,
        infav: Boolean = false
    ) {

        Log.i("ProductDetailsviewModel", "filter by user ${userMail}")
        var user: DraftOrderDetails? = null
        var check: Boolean = false

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
                if (draftOrder.email == "FAV_" + userMail) {
                    Log.i("ProductDetailsviewModel", "filter by user ${draftOrder.email}")
                    Log.i("ProductDetailsviewModel", "Draft Order Found ${draftOrder}")
                    user = draftOrder
                    check = true
                }
            }
        }

        /*
        //
        //        if (user != null && isCart)
        //            filterByItem(user, varient, theSingleProduct, isCart)
        //        else if (user != null && !isCart) {
        //          updateFavDraftOrder(user, theSingleProduct, varient)
        //        }
        //        else {
        //            createDraftOrder(theSingleProduct, varient, isCart)
        //        }

                */


        if (check) {
            if (isCart)
                filterByItem(user!!, varient, theSingleProduct, isCart)
            else
                FilterFavDraftOrder(user!!, theSingleProduct, varient, infav)
        } else if (!infav)
            createDraftOrder(theSingleProduct, varient, isCart)
    }

    private fun FilterFavDraftOrder(
        draftOrderDetails: DraftOrderDetails,
        theSingleProduct: SingleProduct,
        varient: VariantsItem,
        infav: Boolean = false
    ) {
        var item: DraftOrderDetails? = null
        var myLineItem: LineItem? = null
        for (line_item in draftOrderDetails.line_items) {
            if (line_item.product_id == varient.productId && line_item.variant_id == varient.id.toString())
                item = draftOrderDetails        //item already exists in the favourites
            myLineItem = line_item
        }

        if (infav && item != null) {
            _isFav.value = true
        } else if (infav && item == null) {
            _isFav.value = false
        } else {
            UpdateFavDraftOrder(draftOrderDetails, item, myLineItem, theSingleProduct, varient)
        }
    }

    private fun UpdateFavDraftOrder(
        draftOrderDetails: DraftOrderDetails,
        item: DraftOrderDetails?,
        myLineItem: LineItem?,
        theSingleProduct: SingleProduct,
        varient: VariantsItem
    ) {

        if (item != null) {
            Log.i("xoxoxoxoxox", "Update draft order")
            //if the product already exists in the favourites
            _isFav.value = false
            draftOrderDetails.line_items.remove(myLineItem)
            if (draftOrderDetails.line_items.isEmpty()) {
                viewModelScope.launch {
                    repository.deleteDraftOrder(draftOrderDetails.id!!)
                }
            }
            else {
                viewModelScope.launch {
                    val order = DraftOrderRequest(draftOrderDetails)
                    repository.updateDraftOrder(order)
                }
            }
        } else {                                                        // add the product to the favourites
            Log.i("xoxoxoxoxox", "add draft order")
            draftOrderDetails.line_items.add(setLineItem(theSingleProduct, varient))
            _isFav.value = true

            viewModelScope.launch {
                val order = DraftOrderRequest(draftOrderDetails)
                repository.updateDraftOrder(order)
            }
        }
    }


/*

    private fun UpdateFavDraftOrder(draftOrderDetails: DraftOrderDetails, item: DraftOrderDetails?, myLineItem: LineItem?, theSingleProduct: SingleProduct, varient: VariantsItem
    ) {
        if (item != null) {
            // If the item already exists in favorites, remove it
            Log.i("xoxoxoxoxox", "Update draft order - removing from favorites")
            _isFav.value = false
            draftOrderDetails.line_items.remove(myLineItem)

            if (draftOrderDetails.line_items.isEmpty()) {
                viewModelScope.launch {
                    repository.deleteDraftOrder(draftOrderDetails.id!!)
                }
            } else {
                // Otherwise, update the draft order without the removed item
                viewModelScope.launch {
                    val order = DraftOrderRequest(draftOrderDetails)
                    repository.updateDraftOrder(order)
                }
            }
        } else {
            // If the item does not exist, add it to favorites
            Log.i("xoxoxoxoxox", "Adding item to favorites")
            draftOrderDetails.line_items.add(setLineItem(theSingleProduct, varient))
            _isFav.value = true
            viewModelScope.launch {
                val order = DraftOrderRequest(draftOrderDetails)
                repository.updateDraftOrder(order)
            }
        }
    }
*/


    fun filterByItem(
        draftOrder: DraftOrderDetails,
        varient: VariantsItem,
        theSingleProduct: SingleProduct,
        inCart: Boolean
    ) {
        var exists = false
        var line: LineItem? = null
        for (line_item in draftOrder.line_items) {
            if (line_item.variant_id == varient.id.toString()){
                exists = true
                line = line_item
                break
            }

        }
        if (exists) {
            updateDraftOrderItemCount(draftOrder,line!!)
        } else {
            updateDraftOrder(draftOrder, varient, theSingleProduct)
        }
    }

    private fun updateDraftOrder(draftOrder: DraftOrderDetails, varient: VariantsItem, theSingleProduct: SingleProduct) {
        draftOrder.line_items.add(setLineItem(theSingleProduct, varient))
        viewModelScope.launch {
            val order = DraftOrderRequest(draftOrder)
            repository.updateDraftOrder(order)
        }
    }

    fun updateDraftOrderItemCount(draftOrder: DraftOrderDetails, line: LineItem) {
        draftOrder.line_items.find { it.variant_id == line.variant_id }?.quantity = line.quantity + 1
        viewModelScope.launch {
            val order = DraftOrderRequest(draftOrder)
            repository.updateDraftOrder(order)
        }
    }

    fun createDraftOrder(theSingleProduct: SingleProduct, varient: VariantsItem, isCart: Boolean) {
        Log.i("ProductDetailsviewModel", "createDraftOrder")
        if (!isCart)
            _isFav.value = true
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

    fun fetchConversionRate(context: Context, selectedCurrency: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = currencyApi.getRates(Constants.currencyApi)
                if (response.isSuccessful) {
                    val rates = response.body()?.rates
                    rates?.let {
                        conversionRate = it[selectedCurrency] ?: 1.0
                        updatePrices(context, conversionRate)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updatePrices(context: Context, conversionRate: Double) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putFloat("conversionRate", conversionRate.toFloat()).apply()

        CoroutineScope(Dispatchers.Main).launch {
            // Call necessary composables to update UI
        }
    }

    private fun setLineItem(theSingleProduct: SingleProduct, varient: VariantsItem, ) = LineItem(
        title = theSingleProduct.product!!.title!!,
        price = (varient.price?.toFloatOrNull()?.times(conversionRate)).toString(),
        quantity = 1,
        variant_id = varient.id.toString(),
        product_id = theSingleProduct.product.id!!,
        properties = listOf(
            Property("imageUrl", theSingleProduct.product.images!![0]!!.src!!),
            Property("Color", varient.option1!!),
            Property("Size", varient.option2!!)
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