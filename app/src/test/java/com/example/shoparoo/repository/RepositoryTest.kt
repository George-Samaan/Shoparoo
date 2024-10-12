package com.example.shoparoo.repository

import com.example.shoparoo.data.db.remote.RemoteDataSource
import com.example.shoparoo.data.repository.Repository
import com.example.shoparoo.data.repository.RepositoryImpl
import com.example.shoparoo.model.Product
import com.example.shoparoo.model.SingleProduct
import com.example.shoparoo.ui.homeScreen.viewModel.MockData.testProduct1
import com.example.shoparoo.ui.homeScreen.viewModel.MockData.testProduct2
import com.example.shoparoo.ui.homeScreen.viewModel.MockData.testProduct3
import com.example.shoparoo.ui.homeScreen.viewModel.MockData.testSmartCollectionProduct
import com.example.shoparoo.ui.homeScreen.viewModel.mockData3.createMockDraftOrderResponse
import com.example.shoparoo.ui.homeScreen.viewModel.mockData3.mockDraftOrderRequest
import com.example.shoparoo.ui.homeScreen.viewModel.mockData3.mockDraftOrderRequesttobeUpdated
import com.example.shoparoo.ui.homeScreen.viewModel.mockData3.mockProductItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify

class RepositoryTest {
    private lateinit var repository: Repository
    private lateinit var fakeRemoteDataSource: RemoteDataSource

    @Before
    fun setup() {
        fakeRemoteDataSource = FakeRemoteDataSource()
        repository = RepositoryImpl(fakeRemoteDataSource)
    }

    private val testForYouProduct = Product(
        products = listOf(testProduct1, testProduct2, testProduct3)
    )

    @Test
    fun test_get_smart_collection_assert_size() = runTest {
        val smartCollections = repository.getSmartCollections()
        val checkSize = smartCollections.first()
        assertEquals(
            checkSize.smartCollections?.size,
            testSmartCollectionProduct.smartCollections?.size
        )
    }

    @Test
    fun test_get_smart_collection_data() = runTest {
        val smartCollections = repository.getSmartCollections()
        val checkData = smartCollections.first()
        assertEquals(checkData, testSmartCollectionProduct)
    }

    @Test
    fun test_get_for_you_products_assert_size() = runTest {
        val forYouProducts = repository.getForYouProducts()
        val checkSize = forYouProducts.first()
        assertEquals(checkSize, testForYouProduct)
    }

    @Test
    fun test_get_for_you_products_data() = runTest {
        val forYouProducts = repository.getForYouProducts()
        val checkData = forYouProducts.first()
        assertEquals(checkData, testForYouProduct)
    }



    @Test
    fun test_get_draft_order_assert_size() = runTest {
        val draftOrder = repository.getDraftOrder()
        val checkSize = draftOrder.first()
        assertEquals(checkSize, createMockDraftOrderResponse())
    }


    @Test
    fun test_get_draft_order_matches_expected_response() = runTest {
        val draftOrder = repository.getDraftOrder()
        val actualData = draftOrder.first()
        assertEquals(actualData, createMockDraftOrderResponse())
    }

    @Test
    fun test_get_single_product_from_id() = runTest {
        val singleProduct = repository.getSingleProductFromId("123")
        val actualProductId = singleProduct.first().product?.id
        assertEquals(actualProductId, mockProductItem.product?.id)
    }

    @Test
    fun update_draft_order() = runTest {
        val draftOrder = repository.updateDraftOrder(mockDraftOrderRequest)
       assertEquals(mockDraftOrderRequest, mockDraftOrderRequesttobeUpdated)
    }

}