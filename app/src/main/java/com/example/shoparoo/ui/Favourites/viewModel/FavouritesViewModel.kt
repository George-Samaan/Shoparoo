package com.example.shoparoo.ui.Favourites.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.data.repository.Repository
import com.example.shoparoo.model.DraftOrderDetails
import com.example.shoparoo.model.DraftOrderRequest
import com.example.shoparoo.model.DraftOrderResponse
import com.example.shoparoo.model.ImagesItem
import com.example.shoparoo.model.LineItem
import com.example.shoparoo.model.ProductsItem
import com.example.shoparoo.model.Property
import com.example.shoparoo.model.SingleProduct
import com.example.shoparoo.model.VariantsItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouritesViewModel(private val repository: Repository) : ViewModel() {
    private val _draftOrderFav = MutableStateFlow<ApiState>(ApiState.Loading)
    val draftOrderFav = _draftOrderFav.asStateFlow()

    private val _productItems: MutableStateFlow<MutableList<ProductsItem>> =
        MutableStateFlow(mutableListOf())
    val productItems = _productItems.asStateFlow()

    val userMail by lazy {
        FirebaseAuth.getInstance().currentUser?.email
    }
    //get all favourites and have the option to delete a favourite(we are sure item exists in favourites)
    fun getFavourites(clkDelete: Boolean = false, id: Long = 0) {
        viewModelScope.launch {
            _draftOrderFav.value = ApiState.Loading
            repository.getDraftOrder().catch {
                _draftOrderFav.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect {
                _draftOrderFav.value = ApiState.Success(it)
                var filter = filterByUser(it, userMail)

                if (filter.first) { //handle this in ui
                    Log.d("FavouritesViewModel", "Favourites found")
                    if (clkDelete) {
                        deleteItem(filter.second!!, id)
                    } else
                        getFavData(filter.second)
                } else {
                    _productItems.value = mutableListOf()
                    Log.d("FavouritesViewModel", "Favourites not found")
                }

            }
        }

    }

    //show all favourites (favourite screen)
    private fun getFavData(second: DraftOrderDetails?) {
        _productItems.value = mutableListOf()
        for (lineItem in second!!.line_items) {
            Log.i("FavouritesViewModel", "getFavData: ${lineItem.product_id}")
            _productItems.value.add(
                ProductsItem(
                    title = lineItem.title,
                    id = lineItem.product_id,
                    variants = listOf(
                        VariantsItem(
                            title = lineItem.title,
                            id = lineItem.variant_id.toLong(),
                            price = lineItem.price
                        )
                    ),
                    images = listOf(
                        ImagesItem(
                            src = lineItem.properties[0].value
                        )
                    )
                )
            )
        }
    }

    private fun deleteItem(second: DraftOrderDetails, id: Long?) {
        viewModelScope.launch {
            for (lineItem in second.line_items) {
                if (lineItem.product_id == id) {
                    second.line_items.remove(lineItem)
                    break
                }
            }

            if (second.line_items.isEmpty()) {
                _productItems.value = mutableListOf()
                repository.deleteDraftOrder(second.id!!)
            } else {
                _productItems.value = _productItems.value.filter { it.id != id }.toMutableList()
                val order = DraftOrderRequest(second)
                repository.updateDraftOrder(order)
            }
        }
    }

    fun addFav(id: Long) {
        viewModelScope.launch {
            _draftOrderFav.value = ApiState.Loading
            repository.getDraftOrder().catch {
               // _draftOrderFav.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect {
                _draftOrderFav.value = ApiState.Success(it)
                var filter = filterByUser(it, userMail)
                if (filter.first) {
                    getProductDataById(filter.second!!, id)
                } else {
                    createDraftOrderFav(id)
                    getFavourites()
                }
            }
        }

    }

    //get product data by id to add, remove or update favourites
    private fun getProductDataById(userDraftOrder: DraftOrderDetails, id: Long) {
        viewModelScope.launch {
            repository.getSingleProductById(id.toString()).catch {
                _draftOrderFav.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect {
                _draftOrderFav.value = ApiState.Success(it)
                val singleProduct = it as SingleProduct
                val varient = singleProduct.product!!.variants!![0]
                editFavDraftOrder(userDraftOrder, singleProduct, varient!!)
            }
        }
    }

    //create draft order for favourites if user has no favourites
    private fun createDraftOrderFav(id: Long) {
        viewModelScope.launch {
            repository.getSingleProductById(id.toString()).catch {
                _draftOrderFav.value = ApiState.Failure(it.message ?: "Unknown Error")
            }.collect {
                _draftOrderFav.value = ApiState.Success(it)
                val singleProduct = it as SingleProduct
                val varient = singleProduct.product!!.variants!![0]
                val lineItem = setLineItem(singleProduct, varient!!)
                val order = DraftOrderRequest(
                    DraftOrderDetails(
                        line_items = mutableListOf(lineItem),
                        email = "FAV_" + userMail
                    )
                )
                val us = singleProduct.product
                productItems.value.add(us)
                repository.createDraftOrder(order)
            }

        }
    }


    fun editFavDraftOrder( //add or remove item from favourites
        draftOrderDetails: DraftOrderDetails,
        theSingleProduct: SingleProduct,
        variant: VariantsItem,
    ) {
        val myLineItem = draftOrderDetails.line_items.find {
            it.product_id == variant.productId
        }

        if (myLineItem != null) { //item is in fav

            draftOrderDetails.line_items.remove(myLineItem)

            if (draftOrderDetails.line_items.isEmpty()) {
                _productItems.value = mutableListOf()

                viewModelScope.launch { //item is the only one in fav  -> remove from fav & delete draft order
                    repository.deleteDraftOrder(draftOrderDetails.id!!)
                    getFavourites()
                }
            } else {
                _productItems.value = _productItems.value.filter { it.id != theSingleProduct.product!!.id }.toMutableList()
                viewModelScope.launch { //item is not the only one in fav -> remove from fav
                    val order = DraftOrderRequest(draftOrderDetails)
                    repository.updateDraftOrder(order)
                    getFavourites()
                }
            }
        } else {   //item is not in fav and there exists another items -> add to fav
            draftOrderDetails.line_items.add(setLineItem(theSingleProduct, variant))
            val us = theSingleProduct.product
            productItems.value.add(us!!)
            viewModelScope.launch {
                val order = DraftOrderRequest(draftOrderDetails)
                repository.updateDraftOrder(order)
                getFavourites()
            }
        }
    }

    //create line item for draft order requests
    private fun setLineItem(theSingleProduct: SingleProduct, varient: VariantsItem) = LineItem(
        title = theSingleProduct.product!!.title!!,
        price = (varient.price?.toFloatOrNull().toString()),
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

//filter draft order by user and return the draft order details
fun filterByUser(
    draftOrderResponse: DraftOrderResponse,
    userMail: String?
): Pair<Boolean, DraftOrderDetails?> {
    var favExists: Boolean = false
    var UserdraftOrder: DraftOrderDetails? = null
    for (draftOrder in draftOrderResponse.draft_orders!!) {
        if (draftOrder.email == "FAV_" + userMail) {
            UserdraftOrder = draftOrder
            favExists = true
            break
        }
    }
    return Pair(favExists, UserdraftOrder)
}


class FavouritesViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavouritesViewModel::class.java)) {
            return FavouritesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}