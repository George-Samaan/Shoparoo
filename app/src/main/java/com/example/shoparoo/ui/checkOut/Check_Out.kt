package com.example.shoparoo.ui.checkOut

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoparoo.R
import com.example.shoparoo.ui.shoppingCart.OrderSummary
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import java.util.Calendar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CheckoutScreen(navController: NavController) {
    var selectedPaymentMethod by remember { mutableStateOf("cash") } // Default to "cash" option
    var showAddCreditCardScreen by remember { mutableStateOf(false) } // Manage visibility

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp)
        ) {
            HeaderCheck(navController)
            Spacer(modifier = Modifier.height(16.dp))
            Location()
            Spacer(modifier = Modifier.height(16.dp))
            ChoosePaymentMethod(
                selectedPaymentMethod,
                onPaymentMethodSelected = { method ->
                    selectedPaymentMethod = method
                    if (method == "card") {
                        showAddCreditCardScreen = true // Show AddCreditCardScreen on card selection
                    } else {
                        showAddCreditCardScreen = false
                    }
                },
                showAddCreditCardScreen = showAddCreditCardScreen // Pass the visibility state
            )
            Spacer(modifier = Modifier.height(16.dp))
            OrderSummary()
            Spacer(modifier = Modifier.height(16.dp))
            CheckoutButtonCheck()
        }
    }
}

// Payment method selection component
@Composable
fun ChoosePaymentMethod(
    selectedPaymentMethod: String,
    onPaymentMethodSelected: (String) -> Unit,
    showAddCreditCardScreen: Boolean
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
            fontSize = 16.sp
        )
        Spacer(Modifier.height(10.dp))

        // Cash on delivery option
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPaymentMethodSelected("cash") }
        ) {
            Image(
                painter = painterResource(id = R.drawable.cash),
                contentDescription = "Cash",
                modifier = Modifier.size(24.dp)
            )
            Text(
                "Cash on delivery",
                modifier = Modifier
                    .padding(start = 5.dp),
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.height(20.dp))

        // Credit or Debit Card option
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPaymentMethodSelected("card") }
        ) {
            Image(
                painter = painterResource(id = R.drawable.card),
                contentDescription = "Card",
                modifier = Modifier.size(24.dp)
            )
            Text(
                "Credit or Debit Card",
                modifier = Modifier
                    .padding(start = 5.dp),
                fontSize = 14.sp
            )
            Spacer(Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onPaymentMethodSelected("card") } // Click to show card form
            )
        }

        // Show AddCreditCardScreen if "card" is selected
        if (showAddCreditCardScreen) {
            CreditCardItem(onCardAdded = { onPaymentMethodSelected("cash") })
        }
    }
}

// Header for the checkout screen
@Composable
fun HeaderCheck(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5))
                .clickable { navController.popBackStack() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_back_2),
                contentDescription = stringResource(R.string.back),
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = stringResource(R.string.check_out),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(end = 50.dp)
        )
    }
}

// Location info for the checkout screen
@Composable
fun Location() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = stringResource(R.string.back),
                modifier = Modifier.size(24.dp)
            )
        }
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                text = "325 15th Eighth Avenue, NewYork",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}


@Composable
fun CreditCardItem(onCardAdded: () -> Unit) {
    val context = LocalContext.current
    var cardHolderName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expirationMonth by remember { mutableStateOf("") }
    var expirationYear by remember { mutableStateOf("") }

    var isCardHolderNameValid by remember { mutableStateOf(true) }
    var isCardNumberValid by remember { mutableStateOf(true) }
    var isExpirationMonthValid by remember { mutableStateOf(true) }
    var isExpirationYearValid by remember { mutableStateOf(true) }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100 // get last two digits of the year

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
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = !isCardHolderNameValid
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
            value = cardNumber,
            onValueChange = {
                cardNumber = it
                isCardNumberValid = it.length == 16 && it.all { char -> char.isDigit() }
            },
            label = { Text("Card number") },
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

        // Expiration Date Fields (MM/YY)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = expirationMonth,
                onValueChange = {
                    expirationMonth = it
                    isExpirationMonthValid = it.length == 2 && it.toIntOrNull() in 1..12
                },
                label = { Text("MM") },
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
                    isExpirationYearValid = it.length == 2 && it.toIntOrNull()?.let { year -> year >= currentYear } ?: false
                },
                label = { Text("YY") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                singleLine = true,
                isError = !isExpirationYearValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        if (!isExpirationMonthValid) {
            Text(
                text = "Invalid expiration month",
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (!isExpirationYearValid) {
            Text(
                text = "Invalid expiration year",
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Add Card Button
        Button(
            onClick = {
                isCardHolderNameValid = cardHolderName.isNotBlank()
                isCardNumberValid = cardNumber.length == 16 && cardNumber.all { char -> char.isDigit() }
                isExpirationMonthValid = expirationMonth.length == 2 && expirationMonth.toIntOrNull() in 1..12
                isExpirationYearValid = expirationYear.length == 2 && expirationYear.toIntOrNull()?.let { it >= currentYear } ?: false

                if (isCardHolderNameValid && isCardNumberValid && isExpirationMonthValid && isExpirationYearValid) {
                    Toast.makeText(context, "Card Added", Toast.LENGTH_SHORT).show()
                    onCardAdded()
                } else {
                    Toast.makeText(context, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF673AB7))
        ) {
            Text(text = "Add Card", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun CheckoutButtonCheck() {
    Button(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(Color(0xFF673AB7))
    ) {
        Text(text = "Place Order", fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Preview(showBackground = true, device = "id:pixel_8_pro")
@Composable
fun PreviewCheckoutScreen() {
    CheckoutScreen(navController = NavController(LocalContext.current))
}


/*
// Payment method selection component
@Composable
fun ChoosePaymentMethod(selectedPaymentMethod: String, onPaymentMethodSelected: (String) -> Unit) {
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
            fontSize = 16.sp
        )
        Spacer(Modifier.height(10.dp))

        // Cash on delivery option
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPaymentMethodSelected("cash") }
        ) {
            Image(
                painter = painterResource(id = R.drawable.cash),
                contentDescription = "Cash",
                modifier = Modifier.size(24.dp)
            )
            Text(
                "Cash on delivery",
                modifier = Modifier
                    .padding(start = 5.dp),
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.height(20.dp))

        // Credit or Debit Card option
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPaymentMethodSelected("card") }
        ) {
            Image(
                painter = painterResource(id = R.drawable.card),
                contentDescription = "Card",
                modifier = Modifier.size(24.dp)
            )
            Text(
                "Credit or Debit Card",
                modifier = Modifier
                    .padding(start = 5.dp),
                fontSize = 14.sp
            )
            Spacer(Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
*/