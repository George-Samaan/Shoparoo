package com.example.shoparoo.data.db.remote

import com.example.shoparoo.model.Product
import com.example.shoparoo.model.SmartCollections
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    fun getSmartCollections(): Flow<SmartCollections>
    fun getForYouProducts(): Flow<Product>
    fun getProductsFromBrandsId(collectionId: String): Flow<Product>

    // categories section
    fun getWomenProducts(): Flow<Product>
    fun getSalesProducts(): Flow<Product>
    fun getMensProducts(): Flow<Product>
    fun getKidsProducts(): Flow<Product>

}