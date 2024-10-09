package com.example.shoparoo.ui.checkOut

import android.annotation.SuppressLint

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CheckoutScreen(navController: NavController, viewModel: ShoppingCartViewModel) {
    val cartItems by viewModel.cartItems.collectAsState()

    // Calculate the subtotal
    val subtotal = calculateSubtotal(cartItems)
    var selectedPaymentMethod by remember { mutableStateOf("cash") }
    var showAddCreditCardScreen by remember { mutableStateOf(false) }

    var cardHolderName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expirationMonth by remember { mutableStateOf("") }
    var expirationYear by remember { mutableStateOf("") }


    var totalDiscount by remember { mutableStateOf(0.0) }

    Scaffold {
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
        ) {
            item { AppHeader(navController, title = stringResource(R.string.check_out)) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item{Location()}
            item {
                ApplyCoupons(productList = cartItems) { discount ->
                    totalDiscount = discount
                }
            }
            item {
                OrderSummary(totalDiscount = totalDiscount, subtotal = subtotal, deliveryCharges = 0.0, total = subtotal - totalDiscount) // Pass the
            // subtotal here
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
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

            if (selectedPaymentMethod == "cash"){
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



