package com.example.shoparoo.data.repository

import com.example.shoparoo.model.Product
import com.example.shoparoo.model.SingleProduct
import com.example.shoparoo.model.SmartCollections
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getSmartCollections(): Flow<SmartCollections>
    fun getForYouProducts(): Flow<Product>

    fun getProductsFromBrandsId(collectionId: String): Flow<Product>

    fun getSingleProductFromId(id: String): Flow<SingleProduct>

    // categories section
    fun getWomenProducts(): Flow<Product>
    fun getSalesProducts(): Flow<Product>
    fun getMensProducts(): Flow<Product>
    fun getKidsProducts(): Flow<Product>


    fun saveCurrencyPreference(currency: String)
    fun getCurrencyPreference(): String
}