package com.example.shoparoo.data.network

import com.example.shoparoo.model.DraftOrderRequest
import com.example.shoparoo.model.DraftOrderResponse
import com.example.shoparoo.model.OrderResponse
import com.example.shoparoo.model.Product
import com.example.shoparoo.model.SingleProduct
import com.example.shoparoo.model.SmartCollections
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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

    @GET("products/{id}.json")
    suspend fun getSingleProduct(
        @Path("id") id: String
    ): Response<SingleProduct>

    //women's products
    @GET("products.json?collection_id=282184351843")
    suspend fun getWomenProducts(): Response<Product>

    //sales products
    @GET("products.json?collection_id=282184417379")
    suspend fun getSalesProducts(): Response<Product>

    //mens products
    @GET("products.json?collection_id=282184319075")
    suspend fun getMensProducts(): Response<Product>

    // kids products
    @GET("products.json?collection_id=282184384611")
    suspend fun getKidsProducts(): Response<Product>

    //post draft order
    @POST("draft_orders.json")
    suspend fun createDraftOrder(@Body createDraftOrder: DraftOrderRequest)

    //delete draft order
    @DELETE("draft_orders/{id}.json")
    suspend fun deleteDraftOrder(@Path("id") id: Long)

    //get draft order
    @GET("draft_orders.json")
    suspend fun getDraftOrder(): Response<DraftOrderResponse>

    //put draft order add the body
    @PUT("draft_orders/{id}.json")
    suspend fun updateDraftOrder(@Body draftOrderDetails: DraftOrderRequest, @Path("id") id: String)

    // get orders
    @GET("orders.json")
    suspend fun getOrders(): Response<OrderResponse>

    @DELETE("draft_orders/{id}.json")
    suspend fun deleteDraftOrder(@Path("id") draftOrderId: String)

    @PUT("draft_orders/{id}/complete.json")
    suspend fun addToCompleteOrder(
        @Path("id") id: String,
    )


}