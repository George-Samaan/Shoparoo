package com.example.shoparoo.ui.checkOut

import android.annotation.SuppressLint

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shoparoo.R
import com.example.shoparoo.model.LineItem
import com.example.shoparoo.ui.shoppingCart.viewModel.ShoppingCartViewModel
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CheckoutScreen(navController: NavController, viewModel: ShoppingCartViewModel) {
    val cartItems by viewModel.cartItems.collectAsState()

    val draftOrderDetails by viewModel.draftOrderDetails.collectAsState()
    var totalDiscount by remember { mutableStateOf(0.0) }


    LaunchedEffect(Unit) {
        totalDiscount = 0.0
        viewModel.clearDiscount()
        viewModel.getDraftOrderDetails()


    }

    var selectedPaymentMethod by remember { mutableStateOf("cash") }
    var showAddCreditCardScreen by remember { mutableStateOf(false) }

    var cardHolderName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expirationMonth by remember { mutableStateOf("") }
    var expirationYear by remember { mutableStateOf("") }

    Scaffold {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
        ) {
            // Header
            item { AppHeader(navController, title = stringResource(R.string.check_out)) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { Location() }


            item {
                ApplyCoupons(
                    productList = cartItems,
                    viewModel = viewModel,
                    draftOrderId = draftOrderDetails?.id ?: 0L,
                    appliedDiscount = draftOrderDetails?.applied_discount
                ) { discount ->
                    totalDiscount = discount
                }
            }


            item {
                draftOrderDetails?.let { order ->
                    val subtotal = order.subtotal_price?.toDoubleOrNull() ?: 0.0
                    val totalTax = order.total_tax?.toDoubleOrNull() ?: 0.0
                    val discount = order.applied_discount?.amount ?: 0.0
                    val total = (order.total_price?.toDoubleOrNull() ?: 0.0) - totalDiscount

                    OrderSummary(
                        subtotal = subtotal,
                        totalTax = totalTax,
                        discount = discount,
                        total = total
                    )
                }
                viewModel.getDraftOrderDetails()
            }


            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Choose payment method
            item {
                ChoosePaymentMethod(
                    selectedPaymentMethod, onPaymentMethodSelected = { method ->
                        selectedPaymentMethod = method
                        showAddCreditCardScreen = method == "card"
                    },
                    showAddCreditCardScreen = showAddCreditCardScreen
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            if (selectedPaymentMethod == "cash") {
                item {
                    CheckoutButtonCheck(
                        selectedPaymentMethod = selectedPaymentMethod,
                        cardHolderName = cardHolderName,
                        cardNumber = cardNumber,
                        expirationMonth = expirationMonth,
                        expirationYear = expirationYear
                    )
                }
            }
        }
    }
}





