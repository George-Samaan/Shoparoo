package com.example.shoparoo.data.network

import com.example.shoparoo.model.SmartCollections
import retrofit2.Response
import retrofit2.http.GET

interface ApiServices {
    @GET("smart_collections.json")
    suspend fun getBrands(): Response<SmartCollections>
}