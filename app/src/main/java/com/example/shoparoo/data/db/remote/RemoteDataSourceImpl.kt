package com.example.shoparoo.data.db.remote

import android.util.Log
import com.example.shoparoo.data.network.ApiServices
import com.example.shoparoo.model.SmartCollections
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteDataSourceImpl(private val apiService: ApiServices) : RemoteDataSource {
    override fun getSmartCollections(): Flow<SmartCollections> = flow {
        val response = apiService.getBrands()
        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
            Log.d(
                "RemoteDataSourceImpl",
                "Brands collection received: ${response.body()!!.smartCollections}"
            )
        } else {
            // Log detailed error information
            Log.e(
                "RemoteDataSourceImpl",
                "Error retrieving brands collection: ${response.errorBody()?.string()}"
            )
            throw Throwable("Error retrieving brands collection")
        }
    }
}