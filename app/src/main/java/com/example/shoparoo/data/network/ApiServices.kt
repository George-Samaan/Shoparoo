package com.example.shoparoo.data.network

import com.example.shoparoo.model.Product
import com.example.shoparoo.model.SmartCollections
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("smart_collections.json")
    suspend fun getBrands(): Response<SmartCollections>

    @GET("products.json")
    suspend fun getForYouProducts(): Response<Product>

    @GET("products.json")
    suspend fun getProductsFromBrandsId(
        @Query("collection_id") collectionId: String
    ): Response<Product>
}