package com.example.shoparoo.ui.checkOut

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shoparoo.R
import com.example.shoparoo.ui.checkOut.view.AppHeader
import com.example.shoparoo.ui.checkOut.view.ApplyCoupons
import com.example.shoparoo.ui.checkOut.view.Location
import com.example.shoparoo.ui.checkOut.view.OrderSummary
import com.example.shoparoo.ui.shoppingCart.viewModel.ShoppingCartViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CheckoutScreen(navController: NavController, viewModel: ShoppingCartViewModel) {
    val draftOrderDetails by viewModel.draftOrderDetails.collectAsState()
    var totalDiscount by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(Unit) {
        totalDiscount = 0.0
        viewModel.clearDiscount()
        viewModel.getDraftOrderDetails()
    }

    var selectedPaymentMethod by remember { mutableStateOf("cash") }
    var showAddCreditCardScreen by remember { mutableStateOf(false) }

    Scaffold {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                //.padding(top = 5.dp)
        ) {
            // Header
            item { AppHeader(navController, title = stringResource(R.string.check_out)) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { Location(viewModel, draftOrderDetails?.id ?: 0L) }


            item {
                ApplyCoupons(
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
                    val address = order.shipping_address
                    Log.d("CheckoutScreen", "Address: $address")

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
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

