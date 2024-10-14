@file:Suppress("DEPRECATION")

package com.example.shoparoo.ui.checkOut

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoparoo.R
import com.example.shoparoo.data.db.remote.RemoteDataSourceImpl
import com.example.shoparoo.data.network.ApiClient
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.data.repository.RepositoryImpl
import com.example.shoparoo.ui.checkOut.viewModel.PaymentViewModel
import com.example.shoparoo.ui.checkOut.viewModel.PaymentViewModelFactory
import com.example.shoparoo.ui.shoppingCart.viewModel.ShoppingCartViewModel
import com.example.shoparoo.ui.shoppingCart.viewModel.ShoppingCartViewModelFactory
import com.example.shoparoo.ui.theme.primary
import kotlinx.coroutines.delay
import java.util.Calendar


@Composable
fun ChoosePaymentMethod(
    selectedPaymentMethod: String,
    onPaymentMethodSelected: (String) -> Unit,
    showAddCreditCardScreen: Boolean,
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Choose Payment Method",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(Modifier.height(10.dp))

        // Cash on delivery option
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPaymentMethodSelected("cash") }
        ) {
            RadioButton(
                selected = selectedPaymentMethod == "cash",
                onClick = { onPaymentMethodSelected("cash") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.Black,
                    unselectedColor = Color.Gray
                )
            )
            Image(
                painter = painterResource(id = R.drawable.cash),
                contentDescription = "Cash",
                modifier = Modifier.size(24.dp)
            )
            Text(
                "Cash on delivery",
                modifier = Modifier
                    .padding(start = 5.dp),
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(15.dp))

        // Credit or Debit Card option
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPaymentMethodSelected("card") }
        ) {
            RadioButton(
                selected = selectedPaymentMethod == "card",
                onClick = { onPaymentMethodSelected("card") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.Black,
                    unselectedColor = Color.Gray
                )
            )
            Image(
                painter = painterResource(id = R.drawable.card),
                contentDescription = "Card",
                modifier = Modifier.size(24.dp)
            )
            Text(
                "Credit or Debit Card",
                modifier = Modifier
                    .padding(start = 5.dp),
                fontSize = 16.sp
            )
        }

        // Show AddCreditCardScreen if "card" is selected
        if (showAddCreditCardScreen) {
            CreditCardItem()
        }
    }
}


@Composable
fun CreditCardItem() {
    var selectedPaymentMethod by remember { mutableStateOf("card") } // Default to "cash" option
    var cardHolderName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expirationMonth by remember { mutableStateOf("") }
    var expirationYear by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    var isCardHolderNameValid by remember { mutableStateOf(true) }
    var isCardNumberValid by remember { mutableStateOf(true) }
    var isExpirationMonthValid by remember { mutableStateOf(true) }
    var isExpirationYearValid by remember { mutableStateOf(true) }
    var isCvvValid by remember { mutableStateOf(true) }

    val currentYear =
        Calendar.getInstance().get(Calendar.YEAR) % 100 // get last two digits of the year
    val baseYear = 19

    val shoppingCartViewModel: ShoppingCartViewModel = viewModel(
        factory = ShoppingCartViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Name on Card Field
        OutlinedTextField(
            value = cardHolderName,
            onValueChange = {
                cardHolderName = it
                isCardHolderNameValid = cardHolderName.isNotBlank()
            },
            label = { Text("Name on card") },
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = !isCardHolderNameValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)

        )

        if (!isCardHolderNameValid) {
            Text(
                text = "Name on card cannot be empty",
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card Number Field
        OutlinedTextField(
            value = cardNumber, onValueChange = {
                cardNumber = it
                isCardNumberValid = it.length == 16 && it.all { char -> char.isDigit() }
            },
            label = { Text("Card number") },
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = !isCardNumberValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        if (!isCardNumberValid) {
            Text(
                text = "Card number must be 16 digits",
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Cvv Number Field
        OutlinedTextField(
            value = cvv, onValueChange = {
                cvv = it
                isCvvValid = it.length == 3 && it.all { char -> char.isDigit() }
            },
            label = { Text("CVV number") },
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = !isCvvValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        if (!isCvvValid) {
            Text(
                text = "CVV number must be 3 digits",
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        /*  // Expiration Date Fields (MM/YY)
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              OutlinedTextField(
                  value = expirationMonth,
                  onValueChange = {
                      expirationMonth = it
                      isExpirationMonthValid = it.length == 2 && it.toIntOrNull() in 1..12
                  },
                  label = { Text("MM") },
                  shape = RoundedCornerShape(25.dp),
                  modifier = Modifier
                      .weight(1f)
                      .padding(end = 8.dp),
                  singleLine = true,
                  isError = !isExpirationMonthValid,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
              )

              OutlinedTextField(
                  value = expirationYear,
                  onValueChange = {
                      expirationYear = it
                      isExpirationYearValid = it.length == 2 && expirationYear.toIntOrNull()?.let { year ->
                          year in baseYear..59
                      } ?: false
                  },
                  label = { Text("YY") },
                  shape = RoundedCornerShape(25.dp),
                  modifier = Modifier
                      .weight(1f)
                      .padding(start = 8.dp),
                  singleLine = true,
                  isError = !isExpirationYearValid,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
              )
          }
          if (!isExpirationYearValid) {
              Text(
                  text = "Invalid expiration year",
                  color = Color.Red,
                  modifier = Modifier.padding(start = 8.dp)
              )
          }

          if (!isExpirationMonthValid) {
              Text(
                  text = "Invalid expiration month",
                  color = Color.Red,
                  modifier = Modifier.padding(start = 8.dp)
              )
          }*/
        // Expiration Date Fields (MM/YY)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = expirationMonth,
                    onValueChange = {
                        expirationMonth = it
                        isExpirationMonthValid = it.length == 2 && it.toIntOrNull() in 1..12
                    },
                    label = { Text("MM") },
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier.padding(end = 8.dp),
                    singleLine = true,
                    isError = !isExpirationMonthValid,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                if (!isExpirationMonthValid) {
                    Text(
                        text = "Invalid expiration month",
                        color = Color.Red,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = expirationYear,
                    onValueChange = {
                        expirationYear = it
                        isExpirationYearValid = it.length == 2 && expirationYear.toIntOrNull()?.let { year ->
                            year in baseYear..59
                        } ?: false
                    },
                    label = { Text("YY") },
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier.padding(start = 8.dp),
                    singleLine = true,
                    isError = !isExpirationYearValid,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                if (!isExpirationYearValid) {
                    Text(
                        text = "Invalid expiration year",
                        color = Color.Red,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }
            }
        }





        Spacer(modifier = Modifier.height(32.dp))


        CheckoutButtonCheck(
            selectedPaymentMethod = selectedPaymentMethod,
            shoppingCartViewModel,
            cardHolderName = cardHolderName, cardNumber = cardNumber,
            expirationMonth = expirationMonth, expirationYear = expirationYear
        )
    }
}

@Composable
fun CheckoutButtonCheck(
    selectedPaymentMethod: String,
    viewModel: ShoppingCartViewModel,
    cardHolderName: String = null.toString(),
    cardNumber: String = null.toString(),
    expirationMonth: String = null.toString(),
    expirationYear: String = null.toString(),
) {
    // ViewModels
    val shoppingCartViewModel: ShoppingCartViewModel = viewModel(
        factory = ShoppingCartViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )
    val paymentViewModel: PaymentViewModel = viewModel(
        factory = PaymentViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )
    val context = LocalContext.current

    // States
    var isProcessing by remember { mutableStateOf(false) }
    var paymentSuccess by remember { mutableStateOf(false) }
    var orderPlaced by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) } // Existing dialog
    var showConfirmationDialog by remember { mutableStateOf(false) } // New state for confirmation dialog
    var buttonText by remember { mutableStateOf("Place Order") }

    val draftOrderDetails by shoppingCartViewModel.draftOrderDetails.collectAsState()
    val completeOrderState by paymentViewModel.completeOrderState.collectAsState()

    // Button text updates
    buttonText = when {
        isProcessing -> "Processing..."
        orderPlaced -> "Order Placed"
        else -> "Place Order"
    }

    // Fetch draft order details
    LaunchedEffect(Unit) {
        shoppingCartViewModel.getDraftOrderDetails()
    }

    // Function to complete the order
    fun completeOrderIfPossible() {
        val orderId = draftOrderDetails?.id
        if (orderId != null) {
            paymentViewModel.addToCompleteOrder(orderId.toString())
        } else {
            Log.d("CheckoutButtonCheck", "Order ID is null")
        }
    }

    // Observe API state to delete draft after completing the order
    LaunchedEffect(completeOrderState) {
        if (completeOrderState is ApiState.Success) {
            val orderId = draftOrderDetails?.id
            orderId?.let {
                paymentViewModel.deleteOrderFromDraft(it.toString())
                orderPlaced = true
                viewModel.clearCart()
            }
        } else if (completeOrderState is ApiState.Failure) {
            Log.d("CheckoutButtonCheck", "Failed to complete the order")
        }
    }

    // Handle the button click
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                if (orderPlaced) {
                    // Order already placed, do nothing
                } else {
                    showConfirmationDialog = true // Show confirmation dialog
                }
            },
            enabled = if (orderPlaced || isProcessing) false else true,
            colors = ButtonDefaults.buttonColors(primary),
            modifier = Modifier
                .run {
                    if (isProcessing) {
                        size(50.dp)
                    } else {
                        fillMaxWidth()
                            .padding(horizontal = 70.dp)
                    }
                }
                .animateContentSize(),
            contentPadding = PaddingValues(15.dp),
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(buttonText, fontSize = 18.sp)
            }
        }
    }

    // Confirmation dialog
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirm Order", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to place this order?", fontSize = 16.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationDialog = false
                        if (selectedPaymentMethod == "card") {
                            if (validateCardDetails(
                                    cardHolderName,
                                    cardNumber,
                                    expirationMonth,
                                    expirationYear
                                )
                            ) {
                                isProcessing = true
                                completeOrderIfPossible()
                            } else {
                                Toast.makeText(context, "Invalid Card Details", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else if (selectedPaymentMethod == "cash") {
                            val totalAmount =
                                draftOrderDetails?.total_price?.toDoubleOrNull() ?: 0.0
                            if (totalAmount > 1500) {
                                showDialog = true
                            } else {
                                isProcessing = true
                                completeOrderIfPossible()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(primary)
                ) {
                    Text("Yes, Place Order")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmationDialog = false },
                    colors = ButtonDefaults.buttonColors(Color.Gray)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White
        )
    }

    // Existing alert dialog (e.g., when cash limit is exceeded)
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Order Limit Exceeded", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Cash on Delivery is not available for orders exceeding 1500 EGP. Please choose another payment method.",
                    fontSize = 15.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(primary)
                ) {
                    Text("OK")
                }
            },
            containerColor = Color.White
        )
    }

    // Simulate processing when placing an order
    if (isProcessing) {
        LaunchedEffect(Unit) {
            delay(5000L) // Simulate 5-second delay for processing
            isProcessing = false
            paymentSuccess = true
            if (selectedPaymentMethod == "cash") {
                Toast.makeText(context, "Order placed with Cash on Delivery", Toast.LENGTH_SHORT)
                    .show()
            } else if (selectedPaymentMethod == "card" && validateCardDetails(
                    cardHolderName,
                    cardNumber,
                    expirationMonth,
                    expirationYear
                )
            ) {
                Toast.makeText(context, "Order placed with Credit/Debit Card", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // Show success message for card payment
    if (paymentSuccess && selectedPaymentMethod == "card") {
        Toast.makeText(context, "Payment successful", Toast.LENGTH_SHORT).show()
    }
}


fun validateCardDetails(
    cardHolderName: String,
    cardNumber: String,
    expirationMonth: String,
    expirationYear: String
): Boolean {
    return cardHolderName.isNotBlank() &&
            cardNumber.length == 16 && cardNumber.all { it.isDigit() } &&
            expirationMonth.length == 2 && expirationMonth.toIntOrNull() in 1..12 &&
            expirationYear.length == 2 && expirationYear.toIntOrNull() != null
}

