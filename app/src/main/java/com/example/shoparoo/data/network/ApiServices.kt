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

    //women's products
    @GET("products.json?collection_id=281653805155")
    suspend fun getWomenProducts(): Response<Product>

    //sales products
    @GET("products.json?collection_id=281653870691")
    suspend fun getSalesProducts(): Response<Product>

    //mens products
    @GET("products.json?collection_id=281653772387")
    suspend fun getMensProducts(): Response<Product>

    // kids products
    @GET("products.json?collection_id=281653837923")
    suspend fun getKidsProducts(): Response<Product>

}