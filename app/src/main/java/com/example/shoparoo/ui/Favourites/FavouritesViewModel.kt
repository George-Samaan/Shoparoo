package com.example.shoparoo.ui.Favourites

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
import com.example.shoparoo.model.ProductsItem
import com.example.shoparoo.model.VariantsItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouritesViewModel(private val repository: Repository) : ViewModel() {
    private val _draftOrderFav = MutableStateFlow<ApiState>(ApiState.Loading)
    val draftOrderFav = _draftOrderFav.asStateFlow()


    private val _productItems: MutableStateFlow<MutableList<ProductsItem>> = MutableStateFlow(mutableListOf())
    val productItems = _productItems.asStateFlow()

    val userMail by lazy {
        FirebaseAuth.getInstance().currentUser?.email
    }

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


    /*
        fun deleteFav(id: Long?) {
            Log.i("FavouritesViewModeldelete", "deleteFav: $id")

            //   getFavourites(false, id!!)
    //        viewModelScope.launch {
    //            _draftOrderFav.value = ApiState.Loading
    //            repository.getDraftOrder().catch {
    //                _draftOrderFav.value = ApiState.Failure(it.message ?: "Unknown Error")
    //            }.collect {
    //                _draftOrderFav.value = ApiState.Success(it)
    //                var filter = filterByUser(it, userMail)
    //
    //                if (filter.first) { //handle this in ui
    //                    deleteItem(filter.second, id)
    //                } else {
    //                    Log.d("FavouritesViewModeldelete", "Favourites not found")
    //                }
    //            }
    //        }


        }
    */

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


}

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