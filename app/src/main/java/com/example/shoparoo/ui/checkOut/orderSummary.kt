package com.example.shoparoo.ui.checkOut

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoparoo.ui.theme.primary

@Composable
fun OrderSummary(
    subtotal: Double,
    totalTax: Double,
    discount: Double,
    total: Double
) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)


    val currencySymbols = mapOf(
        "EGP" to "$ ",
        "USD" to "EGP "
    )
    val selectedCurrency = remember { sharedPreferences.getString("currency", "USD") ?: "USD" }

    val conversionRate = remember { sharedPreferences.getFloat("conversionRate", 1.0f) }


    val priceConverted = total * conversionRate
   // val formattedPrice = String.format("%.2f", priceConverted)

    val totalD = currencySymbols[selectedCurrency]
    val sub = subtotal * conversionRate
    val tax = totalTax * conversionRate
    val dis = discount * conversionRate


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(10.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Order Summary",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Subtotal", color = Color.Gray, fontSize = 17.sp)
            Text(text = "${currencySymbols[selectedCurrency]} ${"%.2f".format(sub)}", fontSize = 17.sp)
        }
        Spacer(modifier = Modifier.height(5.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Total tax", color = Color.Gray, fontSize = 17.sp)
            Text(text = "${currencySymbols[selectedCurrency]} ${"%.2f".format(tax)}", fontSize =
            17.sp)
        }
        Spacer(modifier = Modifier.height(5.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Discount", color = Color.Gray, fontSize = 17.sp)
            Text(text = "${currencySymbols[selectedCurrency]} -${"%.2f".format(dis)}", fontSize =
            17.sp)
        }
        Spacer(modifier = Modifier.height(5.dp))

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (selectedCurrency == "EGP"){
                Text(text = "Total $", fontWeight = FontWeight.Bold, color = primary, fontSize = 20.sp)
                Text(text = "$ ${"%.2f".format(priceConverted)}", fontWeight = FontWeight.Bold, color =
                primary, fontSize = 20.sp)
            }else{
                Text(text = "Total EGP", fontWeight = FontWeight.Bold, color = primary, fontSize = 20.sp)
                Text(text = "EGP ${"%.2f".format(priceConverted)}", fontWeight = FontWeight.Bold,
                    color =
                primary, fontSize = 20.sp)
            }

        }

        if (selectedCurrency == "EGP"){
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Total EGP",
                    fontWeight = FontWeight.Bold,
                    color = primary,
                    fontSize = 20.sp
                )
                Text(
                    text = "EGP ${"%.2f".format(total)}", fontWeight = FontWeight.Bold, color = primary,
                    fontSize = 20.sp
                )
            }
        }



    }
}

