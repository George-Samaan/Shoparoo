package com.example.shoparoo.repository

import com.example.shoparoo.data.db.remote.RemoteDataSource
import com.example.shoparoo.model.DraftOrderDetails
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
import com.example.shoparoo.ui.homeScreen.viewModel.mockData3.createMockDraftOrderResponse
import com.example.shoparoo.ui.homeScreen.viewModel.mockData3.mockDraftOrderRequesttobeUpdated
import com.example.shoparoo.ui.homeScreen.viewModel.mockData3.mockProductItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRemoteDataSource : RemoteDataSource {

    private val testForYouProduct = Product(
        products = listOf(testProduct1, testProduct2, testProduct3)
    )

    private val draftOrders = mutableListOf(
        DraftOrderDetails(1L, mutableListOf(), "Test Email", "Test Note", "Test Tags")
    )


    override fun getSmartCollections(): Flow<SmartCollections> = flow {
        emit(testSmartCollectionProduct)
    }

    override fun getForYouProducts(): Flow<Product> = flow {
        emit(testForYouProduct)
    }


    /*    override fun getDraftOrder(): Flow<DraftOrderResponse> {
            return flow {
                emit(DraftOrderResponse(draft_orders = draftOrders))
            }
        }*/


    override suspend fun deleteDraftOrder(id: Long) {
        draftOrders.removeIf { it.id == id }
    }

    override fun getProductsFromBrandsId(collectionId: String): Flow<Product> {
        TODO("Not yet implemented")
    }

    override fun getSingleProductFromId(id: String): Flow<SingleProduct> = flow {
        emit(mockProductItem)

    }

    /*    override fun getSingleProductFromId(id: String): Flow<SingleProduct> {
            TODO("Not yet implemented")
        }*/

    override fun getSalesProducts(): Flow<Product> {
        TODO("Not yet implemented")
    }

    override fun getMensProducts(): Flow<Product> {
        TODO("Not yet implemented")
    }
    override fun getWomenProducts(): Flow<Product> {
        TODO("Not yet implemented")
    }

    override fun getKidsProducts(): Flow<Product> {
        TODO("Not yet implemented")
    }

    override suspend fun createDraftOrder(createDraftOrder: DraftOrderRequest) {
        TODO("Not yet implemented")
    }



    override fun getDraftOrder(): Flow<DraftOrderResponse>  = flow {
      emit(createMockDraftOrderResponse())
    }


    override suspend fun updateDraftOrder(draftOrderDetails: DraftOrderRequest) {
        mockDraftOrderRequesttobeUpdated = draftOrderDetails
    }

    override fun getOrders(): Flow<OrderResponse> {
        TODO("Not yet implemented")
    }


    override suspend fun deleteDraftOrder(draftOrderId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addToCompleteOrder(id: String) {
        TODO("Not yet implemented")
    }
}