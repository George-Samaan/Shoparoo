package com.example.shoparoo.data.repository

import com.example.shoparoo.data.db.remote.RemoteDataSource
import com.example.shoparoo.model.DraftOrderRequest
import com.example.shoparoo.model.DraftOrderResponse
import com.example.shoparoo.model.OrderResponse
import com.example.shoparoo.model.Product
import com.example.shoparoo.model.SingleProduct
import com.example.shoparoo.model.SmartCollections
import kotlinx.coroutines.flow.Flow

class RepositoryImpl(private val remoteDataSource: RemoteDataSource) : Repository {

    // remote data source
    override fun getSmartCollections(): Flow<SmartCollections> {
        return remoteDataSource.getSmartCollections()
    }

    override fun getForYouProducts(): Flow<Product> {
        return remoteDataSource.getForYouProducts()
    }

    override fun getProductsFromBrandsId(collectionId: String): Flow<Product> {
        return remoteDataSource.getProductsFromBrandsId(collectionId)
    }

    override fun getSingleProductById(id: String): Flow<SingleProduct> {
        return remoteDataSource.getSingleProductFromId(id)
    }

    override fun getWomenProducts(): Flow<Product> {
        return remoteDataSource.getWomenProducts()
    }

    override fun getSalesProducts(): Flow<Product> {
        return remoteDataSource.getSalesProducts()
    }

    override fun getMensProducts(): Flow<Product> {
        return remoteDataSource.getMensProducts()
    }

    override fun getKidsProducts(): Flow<Product> {
        return remoteDataSource.getKidsProducts()
    }

    // local
    override fun saveCurrencyPreference(currency: String) {
        //  return sharedPreferences.saveCurrencyPreference(currency)
    }

    override fun getCurrencyPreference(): String {
        // return sharedPreferences.getCurrencyPreference()
        return ""
    }


    override suspend fun createDraftOrder(createDraftOrder: DraftOrderRequest) {
        remoteDataSource.createDraftOrder(createDraftOrder)
    }

    override fun getDraftOrder(): Flow<DraftOrderResponse> {
        return remoteDataSource.getDraftOrder()
    }

    override suspend fun deleteDraftOrder(draftOrderId: String) {
        remoteDataSource.deleteDraftOrder(draftOrderId)  // Call the RemoteDataSource function
    }

    override suspend fun updateDraftOrder(draftOrderDetails: DraftOrderRequest) {
        remoteDataSource.updateDraftOrder(draftOrderDetails)
    }

    override suspend fun deleteDraftOrder(id: Long) {
        remoteDataSource.deleteDraftOrder(id)
    }

    override fun getOrders(): Flow<OrderResponse> {
        return remoteDataSource.getOrders()
    }


    override suspend fun addToCompleteOrder(id: String) {
        remoteDataSource.addToCompleteOrder(id)
    }
}