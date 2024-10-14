package com.example.shoparoo.ui.checkOut.view

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.shoparoo.model.AppliedDiscount
import com.example.shoparoo.ui.shoppingCart.viewModel.ShoppingCartViewModel
import com.example.shoparoo.ui.theme.primary

@Composable
fun ApplyCoupons(
    viewModel: ShoppingCartViewModel,
    draftOrderId: Long,
    appliedDiscount: AppliedDiscount?,
    onApplyCoupon: (Double) -> Unit
) {
    val couponText = remember { mutableStateOf("") }
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = couponText.value,
            onValueChange = { couponText.value = it },
            label = { Text("Enter Coupon Code") },
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            singleLine = true
        )

        Button(
            onClick = {
                // Check if the coupon is already applied
                if (appliedDiscount != null) {
                    Toast.makeText(context, "Coupon already applied", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val discountValue =
                    if (couponText.value.isNotEmpty() && couponText.value == "Shoparoo20") {
                        0.20 // 20% discount
                    } else {
                        0.0
                    }

                if (discountValue > 0.0) {
                    val discount = AppliedDiscount(
                        value = discountValue * 100,
                        value_type = "percentage",
                        amount = discountValue
                    )
                    viewModel.applyDiscountToDraftOrder(draftOrderId, discount)

                    Toast.makeText(
                        context,
                        "Coupon Applied: ${couponText.value}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(context, "Invalid Coupon Code", Toast.LENGTH_SHORT).show()
                    onApplyCoupon(0.0)
                }
            },
            modifier = Modifier
                .padding(start = 8.dp, top = 4.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(primary)
        ) {
            Text(
                text = "Apply",
                color = Color.White,
            )
        }
    }
}