package com.example.shoparoo.ui.checkOut

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            Text(text = "$${"%.2f".format(subtotal)}", fontSize = 17.sp)
        }
        Spacer(modifier = Modifier.height(5.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Total tax", color = Color.Gray, fontSize = 17.sp)
            Text(text = "$${"%.2f".format(totalTax)}", fontSize = 17.sp)
        }
        Spacer(modifier = Modifier.height(5.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Discount", color = Color.Gray, fontSize = 17.sp)
            Text(text = "-$${"%.2f".format(discount)}", fontSize = 17.sp)
        }
        Spacer(modifier = Modifier.height(5.dp))

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Total", fontWeight = FontWeight.Bold, color = primary, fontSize = 20.sp)
            Text(text = "$${"%.2f".format(total)}", fontWeight = FontWeight.Bold, color = primary, fontSize = 20.sp)
        }
    }
}

