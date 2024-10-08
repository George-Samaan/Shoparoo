package com.example.shoparoo.ui.shoppingCart

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoparoo.ui.theme.primary

@Composable
fun ApplyCoupons(productList: List<Product>, onApplyCoupon: (Double) -> Unit) {
    val couponText = remember { mutableStateOf("") }
    val context = LocalContext.current
    var totalDiscount: Double

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // EditText for Coupon Code
        OutlinedTextField(
            value = couponText.value,
            onValueChange = { couponText.value = it },
            label = { Text(text = "Enter Coupon Code", color = primary) },
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp), // Add space between EditText and button
            singleLine = true
        )

        // Apply Button
        Button(
            onClick = {
                // Check if the coupon code is valid and apply discount
                val discount = if (couponText.value.isNotEmpty() && couponText.value == "Shoparoo20") {
                    0.20 // 20% discount
                } else {
                    0.0
                }

                if (couponText.value == "Shoparoo20") {
                    totalDiscount = discount * calculateSubtotal(productList)
                    onApplyCoupon(totalDiscount)
                    Toast.makeText(context, "Coupon Applied: ${couponText.value}", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "Invalid Coupon Code", Toast.LENGTH_SHORT).show()
                    onApplyCoupon(0.0)
                }


                Toast.makeText(context, "Coupon Applied: ${couponText.value}", Toast.LENGTH_SHORT)
                    .show()

            },
            modifier = Modifier
                .padding(start = 8.dp, top = 4.dp)
                .height(56.dp), // Adjust height to match EditText
            colors = ButtonDefaults.buttonColors(primary)
        ) {
            Text(text = "Apply", color = Color.White)
        }
    }
}

// Helper function to calculate subtotal
fun calculateSubtotal(productList: List<Product>): Double {
    return productList.sumOf { it.price * it.quantity }
}


