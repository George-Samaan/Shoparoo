package com.example.shoparoo.repository

import com.example.shoparoo.data.db.remote.RemoteDataSource
import com.example.shoparoo.data.repository.Repository
import com.example.shoparoo.data.repository.RepositoryImpl
import com.example.shoparoo.model.Product
import com.example.shoparoo.ui.homeScreen.viewModel.MockData.testProduct1
import com.example.shoparoo.ui.homeScreen.viewModel.MockData.testProduct2
import com.example.shoparoo.ui.homeScreen.viewModel.MockData.testProduct3
import com.example.shoparoo.ui.homeScreen.viewModel.MockData.testSmartCollectionProduct
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

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


    // test draft order (get & delete)
    @Test
    fun test_get_draft_order() = runTest {
        val draftOrderResponse = repository.getDraftOrder()
        val draftOrderDetails = draftOrderResponse.first().draft_orders
        assertEquals(1, draftOrderDetails.size)
        assertEquals("Test Email", draftOrderDetails[0].email)
    }

    @Test
    fun test_delete_draft_order() = runTest {
        var draftOrderResponse = repository.getDraftOrder()
        assertEquals(1, draftOrderResponse.first().draft_orders.size)
        repository.deleteDraftOrder(1L)
        draftOrderResponse = repository.getDraftOrder()
        assertEquals(0, draftOrderResponse.first().draft_orders.size)
    }
}