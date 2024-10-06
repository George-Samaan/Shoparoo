package com.example.shoparoo.data.repository

import android.util.Log
import com.example.shoparoo.data.db.remote.RemoteDataSource
import com.example.shoparoo.data.db.sharedPrefs.SharedPreferences
import com.example.shoparoo.model.Product
import com.example.shoparoo.model.SingleProduct
import com.example.shoparoo.model.SmartCollections
import kotlinx.coroutines.flow.Flow

class RepositoryImpl(private val remoteDataSource: RemoteDataSource) : Repository {
    lateinit var sharedPreferences: SharedPreferences
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

    override fun getSingleProductFromId(id: String): Flow<SingleProduct> {
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
        return sharedPreferences.saveCurrencyPreference(currency)
    }

    override fun getCurrencyPreference(): String {
        return sharedPreferences.getCurrencyPreference()
    }
}