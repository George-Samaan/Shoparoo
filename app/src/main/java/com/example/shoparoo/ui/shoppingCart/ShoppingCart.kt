package com.example.shoparoo.ui.shoppingCart

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.shoparoo.R
import com.example.shoparoo.ui.theme.primary
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

// Data class to represent products in the cart
data class Product(
    val imageRes: Int,
    val productName: String,
    val productBrand: String,
    val price: Double,
    var quantity: Int
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ShoppingCartScreen(navController: NavController) {
    // Sample product list
    val productList = remember {
        mutableStateListOf(
            Product(
                imageRes = R.drawable.ic_watch,
                productName = "Watch",
                productBrand = "Rolex",
                price = 40.0,
                quantity = 1
            ),
            Product(
                imageRes = R.drawable.ic_watch,
                productName = "Airpods",
                productBrand = "Apple",
                price = 333.0,
                quantity = 1
            ),
            Product(
                imageRes = R.drawable.ic_watch,
                productName = "Hoodie",
                productBrand = "Puma",
                price = 50.0,
                quantity = 1
            )
        )
    }

    var totalDiscount by remember { mutableStateOf(0.0) }

    Scaffold {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp)
        ) {
            item {
                Header(navController)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(productList) { product ->
                ProductItem(
                    imageRes = product.imageRes,
                    productName = product.productName,
                    productBrand = product.productBrand,
                    price = "$${product.price}",
                    quantity = product.quantity
                )
            }
            item {
                ApplyCoupons(productList = productList) { discount ->
                    totalDiscount = discount
                }
            }
            item {
                OrderSummary(productList = productList, totalDiscount = totalDiscount)
            }
            item {
                CheckoutButton(navController)
            }
        }
    }
}


@Composable
fun Header(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 5.dp)
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
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = stringResource(R.string.back),
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = stringResource(R.string.cart),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(end = 50.dp)
        )
    }
}

@Composable
fun ProductList(productList: List<Product>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        productList.forEach { product ->
            ProductItem(
                imageRes = product.imageRes,
                productName = product.productName,
                productBrand = product.productBrand,
                price = "$${product.price}",
                quantity = product.quantity
            )
        }
    }
}

@Composable
fun ProductItem(
    imageRes: Int,
    productName: String,
    productBrand: String,
    price: String,
    quantity: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = productName,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = productName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = primary
            )
            Text(
                text = productBrand,
                color = Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = price, color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.W400)
        }
        QuantitySelector(quantity = quantity)

        Icon(
            painter = painterResource(id = R.drawable.ic_delete),
            contentDescription = "Remove",
            tint = Color.Red,
            modifier = Modifier
                .padding(8.dp)
                .size(30.dp)
                .clickable { /* Handle remove item */ }
        )
    }
}

@Composable
fun QuantitySelector(quantity: Int) {
    var count by remember { mutableStateOf(quantity) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { if (count > 0) count-- }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_mini),
                contentDescription = "Minus",
                tint = Color.Gray
            )
        }
        Text(text = count.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        IconButton(onClick = { count++ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_circle),
                contentDescription = "Plus",
                tint = primary
            )
        }
    }
}

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


@Composable
fun OrderSummary(productList: List<Product>, totalDiscount: Double) {
    val totalItems = productList.sumOf { it.quantity }
    val subtotal = calculateSubtotal(productList)
    val deliveryCharges = 50.0
    val total = subtotal - totalDiscount + deliveryCharges

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
            Text(text = "Items", color = Color.Gray, fontSize = 17.sp)
            Text(text = totalItems.toString(), fontSize = 17.sp)
        }
        Spacer(modifier = Modifier.height(5.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Subtotal", color = Color.Gray, fontSize = 17.sp)
            Text(text = "$${"%.2f".format(subtotal)}", fontSize = 17.sp)
        }
        Spacer(modifier = Modifier.height(5.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Discount", color = Color.Gray, fontSize = 17.sp)
            Text(
                text = "-$${"%.2f".format(totalDiscount)}",
                fontSize = 17.sp
            )  // Format totalDiscount correctly
        }
        Spacer(modifier = Modifier.height(5.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Delivery Charges", color = Color.Gray, fontSize = 17.sp)
            Text(text = "$deliveryCharges", fontSize = 17.sp)
        }
        Spacer(modifier = Modifier.height(5.dp))

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Total", fontWeight = FontWeight.Bold, color = primary, fontSize = 20.sp)
            Text(
                text = "$${"%.2f".format(total)}",
                fontWeight = FontWeight.Bold,
                color = primary,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun CheckoutButton(navController: NavController) {
    Button(
        onClick = { navController.navigate("checkout") },
        modifier = Modifier
            .fillMaxWidth()

            .padding(16.dp)
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(primary)
    ) {
        Text(text = "Proceed to Checkout", color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingCartScreenPreview() {
    ShoppingCartScreen(navController = rememberNavController())
}
