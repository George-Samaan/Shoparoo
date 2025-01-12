package com.example.shoparoo.ui.homeScreen.viewModel

import com.example.shoparoo.data.repository.Repository
import com.example.shoparoo.model.DraftOrderRequest
import com.example.shoparoo.model.DraftOrderResponse
import com.example.shoparoo.model.OrderResponse
import com.example.shoparoo.model.Product
import com.example.shoparoo.model.SingleProduct
import com.example.shoparoo.model.SmartCollections
import com.example.shoparoo.ui.homeScreen.viewModel.MockData.testProduct1
import com.example.shoparoo.ui.homeScreen.viewModel.MockData.testProduct2
import com.example.shoparoo.ui.homeScreen.viewModel.MockData.testProduct3
import com.example.shoparoo.ui.homeScreen.viewModel.MockData.testSmartCollectionProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository : Repository {
    private var shouldReturnEmpty = false

    fun setEmptyData() {
        shouldReturnEmpty = true
    }

    private val testForYouProduct = Product(
        products = listOf(testProduct1, testProduct2, testProduct3)
    )

    override fun getSmartCollections(): Flow<SmartCollections> {
        return flow {
            emit(testSmartCollectionProduct)
        }
    }

    override fun getForYouProducts(): Flow<Product> {
        if (shouldReturnEmpty) {
            return flow {
                emit(Product(products = emptyList()))
            }
        }

        return flow {
            emit(testForYouProduct)
        }
    }

    override fun getProductsFromBrandsId(collectionId: String): Flow<Product> {
        TODO("Not yet implemented")
    }

    override fun getSingleProductById(id: String): Flow<SingleProduct> {
        TODO("Not yet implemented")
    }

    override fun getWomenProducts(): Flow<Product> {
        TODO("Not yet implemented")
    }

    override fun getSalesProducts(): Flow<Product> {
        TODO("Not yet implemented")
    }

    override fun getMensProducts(): Flow<Product> {
        TODO("Not yet implemented")
    }

    override fun getKidsProducts(): Flow<Product> {
        TODO("Not yet implemented")
    }

    override fun saveCurrencyPreference(currency: String) {
        TODO("Not yet implemented")
    }

    override fun getCurrencyPreference(): String {
        TODO("Not yet implemented")
    }

    override suspend fun createDraftOrder(createDraftOrder: DraftOrderRequest) {
        TODO("Not yet implemented")
    }

    override fun getDraftOrder(): Flow<DraftOrderResponse> {
        return flow {
            emit(mockDraftOrderResponse)
        }
    }
    override suspend fun updateDraftOrder(draftOrderDetails: DraftOrderRequest) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDraftOrder(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDraftOrder(draftOrderId: String) {
        TODO("Not yet implemented")
    }

    override fun getOrders(): Flow<OrderResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun addToCompleteOrder(id: String) {
        TODO("Not yet implemented")
    }
}


