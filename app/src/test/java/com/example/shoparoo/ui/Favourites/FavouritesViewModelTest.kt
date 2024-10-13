package com.example.shoparoo.ui.Favourites

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shoparoo.ui.homeScreen.viewModel.FakeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavouritesViewModelTest {
    private lateinit var viewModel: FavouritesViewModel
    private lateinit var fakeRepository: FakeRepository

    @Before
    fun setup() {
        fakeRepository = FakeRepository()
        viewModel = FavouritesViewModel(fakeRepository)
    }


    @Test
    fun test_get_draft_order()= runTest {
        val response = fakeRepository.getDraftOrder().first()
        viewModel.draftOrderFav.value
        assertEquals(1, response.draft_orders.size)
    }

    @Test
    fun test_draft_order_data() = runTest {
        val response = fakeRepository.getDraftOrder().first()
        viewModel.draftOrderFav.value
        assertEquals("Sneakers", response.draft_orders[0].line_items[0].title)
        assertEquals(2, response.draft_orders[0].line_items.size)
        assertEquals("customer1@example.com",response.draft_orders[0].email)
    }
}