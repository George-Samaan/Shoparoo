package com.example.shoparoo.data.db.remote

import android.content.ContentValues.TAG
import android.util.Log
import com.example.shoparoo.data.network.ApiServices
import com.example.shoparoo.model.DraftOrderRequest
import com.example.shoparoo.model.DraftOrderResponse
import com.example.shoparoo.model.OrderResponse
import com.example.shoparoo.model.Product
import com.example.shoparoo.model.SingleProduct
import com.example.shoparoo.model.SmartCollections
import com.google.firebase.auth.FirebaseAuth
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

    override fun getForYouProducts(): Flow<Product> = flow {
        val response = apiService.getForYouProducts()
        if (response.isSuccessful && response.body() != null) {
            Log.d("RemoteDataSourceImpl", "Products received: ${response.body()!!.products}")
            emit(response.body()!!)
        } else {
            Log.e(
                "RemoteDataSourceImpl",
                "Error retrieving products: ${response.errorBody()?.string()}"
            )
            throw Throwable("Error retrieving products")
        }
    }

    override fun getProductsFromBrandsId(collectionId: String): Flow<Product> = flow {
        val response = apiService.getProductsFromBrandsId(collectionId)
        if (response.isSuccessful && response.body() != null) {
            Log.d("RemoteDataSourceImpl", "Products received: ${response.body()!!.products}")
            emit(response.body()!!)
        } else {
            Log.e(
                "RemoteDataSourceImpl",
                "Error retrieving products: ${response.errorBody()?.string()}"
            )
            throw Throwable("Error retrieving products")
        }
    }


    override fun getSingleProductFromId(id: String): Flow<SingleProduct> = flow {
        val response = apiService.getSingleProduct(id)
        if (response.isSuccessful && response.body() != null) {
            Log.d("RemoteDataSourceImpl", "Product received: ${response.body()!!.product}")
            emit(response.body()!!)
        } else {
            Log.e(
                "RemoteDataSourceImpl",
                "Error retrieving product: ${response.errorBody()?.string()}"
            )
            throw Throwable("Error retrieving product")
        }
    }

    override fun getWomenProducts(): Flow<Product> = flow {
        val response = apiService.getWomenProducts()
        if (response.isSuccessful && response.body() != null) {
            Log.d("RemoteDataSourceImpl", "Products received Women: ${response.body()!!.products}")

            emit(response.body()!!)
        } else {
            Log.e(
                "RemoteDataSourceImpl",

                "Error retrieving product: ${response.errorBody()?.string()}"
            )
            throw Throwable("Error retrieving product")


        }
    }

    override fun getSalesProducts(): Flow<Product> = flow {
        val response = apiService.getSalesProducts()
        if (response.isSuccessful && response.body() != null) {
            Log.d("RemoteDataSourceImpl", "Products received Sales: ${response.body()!!.products}")
            emit(response.body()!!)
        } else {
            Log.e(
                "RemoteDataSourceImpl",
                "Error retrieving products: ${response.errorBody()?.string()}"
            )
            throw Throwable("Error retrieving products")
        }
    }

    override fun getMensProducts(): Flow<Product> = flow {
        val response = apiService.getMensProducts()
        if (response.isSuccessful && response.body() != null) {
            Log.d("RemoteDataSourceImpl", "Products received Mens: ${response.body()!!.products}")
            emit(response.body()!!)
        } else {
            Log.e(
                "RemoteDataSourceImpl",
                "Error retrieving products: ${response.errorBody()?.string()}"
            )
            throw Throwable("Error retrieving products")
        }
    }

    override fun getKidsProducts(): Flow<Product> = flow {
        val response = apiService.getKidsProducts()
        if (response.isSuccessful && response.body() != null) {
            Log.d("RemoteDataSourceImpl", "Products received Kids: ${response.body()!!.products}")
            emit(response.body()!!)
        } else {
            Log.e(
                "RemoteDataSourceImpl",
                "Error retrieving products: ${response.errorBody()?.string()}"
            )
            throw Throwable("Error retrieving products")
        }
    }


    override suspend fun createDraftOrder(createDraftOrder: DraftOrderRequest) {
        Log.i("RemoteDataSourceImplCreate", "Create Draft Order")
        apiService.createDraftOrder(createDraftOrder)
    }

    override suspend fun updateDraftOrder(draftOrderDetails: DraftOrderRequest) {
        apiService.updateDraftOrder(
            draftOrderDetails,
            draftOrderDetails.draft_order.id.toString()
        )
    }

    override fun getOrders(): Flow<OrderResponse> = flow {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val userEmail = firebaseAuth.currentUser?.email

        if (userEmail != null) {
            Log.i(TAG, "checkUser: user is authenticated with email: $userEmail")
            val response = apiService.getOrders()
            if (response.isSuccessful && response.body() != null) {
                val orders = response.body()!!.orders
                val filteredOrders = orders.filter { order ->
                    order.customer?.email == userEmail
                }
                Log.d("RemoteDataSourceImpl", "Filtered Orders: $filteredOrders")
                emit(response.body()!!.copy(orders = filteredOrders))
            } else {
                throw Throwable("Error retrieving products")
            }
        } else {
            Log.e(TAG, "User is not authenticated")
        }
    }

    override fun getDraftOrder(): Flow<DraftOrderResponse> = flow {
        val response = apiService.getDraftOrder()
        if (response.isSuccessful && response.body() != null) {
            Log.d("RemoteDataSourceImpl", "Draft Order received: ${response.body()!!.draft_orders}")
            emit(response.body()!!)
        } else {
            Log.e(
                "RemoteDataSourceImpl",
                "Error retrieving draft order: ${response.errorBody()?.string()}"
            )
            throw Throwable("Error retrieving draft order")
        }
    }

}

/*    override fun getOrders(): Flow<OrderResponse> = flow {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    Log.i(TAG, "checkUser: user is authenticated+${firebaseAuth.currentUser!!.email}")
    val response = apiService.getOrders()
    if (response.isSuccessful && response.body() != null) {
        Log.d("RemoteDataSourceImpl", "Products received Orders: ${response.body()!!.orders}")

        emit(response.body()!!)
    } else {
        Log.e(
            "RemoteDataSourceImpl",

            "Error retrieving draft order: ${response.errorBody()?.string()}"
        )
        throw Throwable("Error retrieving draft order")
    }
}

override suspend fun updateDraftOrder(draftOrderDetails: DraftOrderRequest) {
   apiService.updateDraftOrder(draftOrderDetails, draftOrderDetails.draft_order.id.toString())
}
}
=======
            "Error retrieving products: ${response.errorBody()?.string()}"
        )
        throw Throwable("Error retrieving products")
    }
}
}*/