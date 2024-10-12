package com.example.shoparoo.ui.homeScreen.viewModel

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {
    private lateinit var viewModel: HomeViewModel
    private lateinit var fakeRepository: FakeRepository

    @Before
    fun setup() {
        fakeRepository = FakeRepository()
        viewModel = HomeViewModel(fakeRepository)
    }

    // Test case 1: Test when smart collections are returned
    @Test
    fun getSmartCollections_with_data() = runTest {
        val result = fakeRepository.getSmartCollections().first()
        viewModel.getSmartCollections()
        assertEquals(2, result.smartCollections?.size)
    }

    // Test case 1: Test when products are returned
    @Test
    fun test_get_for_you_products_with_data() = runTest {
        val result = fakeRepository.getForYouProducts().first()
        viewModel.forYouProducts.first()
        assertEquals(3, result.products?.size)
        assertEquals("Test Product 1", result.products?.get(0)?.title)
        assertEquals("Dress", result.products?.get(1)?.productType)
        assertEquals("Test Product 3", result.products?.get(2)?.title)
    }

    // Test case 2: Test when no products are returned
    @Test
    fun test_get_for_you_products_with_empty_data() = runTest {
        fakeRepository.setEmptyData()
        viewModel.forYouProducts.first()
        val result = fakeRepository.getForYouProducts().first()
        assertEquals(0, result.products?.size)
    }

}