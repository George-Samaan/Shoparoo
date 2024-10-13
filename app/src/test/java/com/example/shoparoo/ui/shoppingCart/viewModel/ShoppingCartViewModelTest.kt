package com.example.shoparoo.ui.shoppingCart.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.shoparoo.data.repository.Repository
import com.example.shoparoo.model.AppliedDiscount
import com.example.shoparoo.model.DraftOrderDetails
import com.example.shoparoo.model.LineItem
import com.example.shoparoo.model.ShippingAddress
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingCartViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule() // For LiveData updates on main thread

    private lateinit var repository: Repository
    private lateinit var viewModel: ShoppingCartViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        repository = mock(Repository::class.java)
        viewModel = ShoppingCartViewModel(repository)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `applyDiscountToDraftOrder updates discount and saves it to repository`() = runTest {
        val discount = AppliedDiscount(20.0, "percentage", 10.0)
        val draftOrderDetails = DraftOrderDetails(1L, mutableListOf(), discount.toString())
        viewModel._draftOrderDetails.value = draftOrderDetails
        // Mock repository to simulate updating draft order
        `when`(repository.updateDraftOrder(anyOrNull())).then {
            viewModel._draftOrderDetails.value = draftOrderDetails
            null
        }
        // When
        viewModel.applyDiscountToDraftOrder(1L, discount)
        // Then
        advanceUntilIdle()
        verify(repository).updateDraftOrder(anyOrNull())
        assert(viewModel._draftOrderDetails.value?.applied_discount == discount)
    }

    @Test
    fun `clearCart clears the cart items`() {
        // Given
        viewModel._cartItems.value = listOf(LineItem(
            111111111111, 222222222222, "Test Product", "10.0", 2, "333333333333",
            listOf(), "Test Vendor"
        ))

        // When
        viewModel.clearCart()

        // Then
        assert(viewModel._cartItems.value.isNullOrEmpty())
    }

    @Test
    fun `clearDiscount clears the applied discount`() {
        // Given
        val discount = AppliedDiscount(20.0, "percentage",10.0)
        val draftOrderDetails = DraftOrderDetails(1L, mutableListOf(), discount.toString())

        viewModel._draftOrderDetails.value = draftOrderDetails

        // When
        viewModel.clearDiscount()

        // Then
        assert(viewModel._draftOrderDetails.value?.applied_discount == null)
    }

    @Test
    fun `updateShippingAddress updates the shipping address and saves it to repository`() = runTest {
        // Given
        val draftOrderId = 1L
        val newAddress = ShippingAddress("123 Test St")
        val initialDraftOrderDetails = DraftOrderDetails(
            draftOrderId,
            mutableListOf(),
            null,
             newAddress.toString()
        )

        viewModel._draftOrderDetails.value = initialDraftOrderDetails

        // Mocking the repository to return Unit
        `when`(repository.updateDraftOrder(anyOrNull())).then {
            viewModel._draftOrderDetails.value = initialDraftOrderDetails
            null
        }
        // When
        viewModel.updateShippingAddress(draftOrderId, newAddress)

        // Then
        advanceUntilIdle()
        assertNotNull(viewModel._draftOrderDetails.value?.shipping_address == newAddress)
    }


}