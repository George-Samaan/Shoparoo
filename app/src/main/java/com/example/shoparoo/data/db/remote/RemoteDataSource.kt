package com.example.shoparoo.data.db.remote

import com.example.shoparoo.model.DraftOrderRequest
import com.example.shoparoo.model.DraftOrderResponse
import com.example.shoparoo.model.LineItem
import com.example.shoparoo.model.Product
import com.example.shoparoo.model.SingleProduct
import com.example.shoparoo.model.SmartCollections
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    fun getSmartCollections(): Flow<SmartCollections>
    fun getForYouProducts(): Flow<Product>
    fun getProductsFromBrandsId(collectionId: String): Flow<Product>
    fun getSingleProductFromId(id: String): Flow<SingleProduct>

    // categories section
    fun getWomenProducts(): Flow<Product>
    fun getSalesProducts(): Flow<Product>
    fun getMensProducts(): Flow<Product>
    fun getKidsProducts(): Flow<Product>

    // draft order section
    suspend fun createDraftOrder(createDraftOrder: DraftOrderRequest)
    fun getDraftOrder(): Flow<DraftOrderResponse>
    fun updateDraftOrder(item: LineItem)

}