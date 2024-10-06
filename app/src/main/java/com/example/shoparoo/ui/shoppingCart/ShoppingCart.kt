package com.example.shoparoo.ui.shoppingCart

import android.annotation.SuppressLint
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.shoparoo.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ShoppingCartScreen(navController: NavController) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            Header(navController)
            Spacer(modifier = Modifier.height(16.dp))
            ProductList()
            Spacer(modifier = Modifier.height(16.dp))
            OrderSummary()
            Spacer(modifier = Modifier.height(16.dp))
            CheckoutButton(navController)
        }
    }
}

@Composable
fun Header(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp)
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
            text = stringResource(R.string.cart),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(end = 50.dp)
        )
    }
}

@Composable
fun ProductList() {
    Column(modifier = Modifier.fillMaxWidth()) {
        ProductItem(
            imageRes = R.drawable.ic_watch,
            productName = "Watch",
            productBrand = "Rolex",
            price = "$40",
            quantity = 2
        )
        ProductItem(
            imageRes = R.drawable.ic_watch,
            productName = "Airpods",
            productBrand = "Apple",
            price = "$333",
            quantity = 2
        )
        ProductItem(
            imageRes = R.drawable.ic_watch,
            productName = "Hoodie",
            productBrand = "Puma",
            price = "$50",
            quantity = 2
        )
    }
}

@Composable
fun ProductItem(imageRes: Int, productName: String, productBrand: String, price: String, quantity: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(10.dp))
            .padding(15.dp),
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
            Text(text = productName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = productBrand, color = Color.Gray, fontSize = 14.sp)
            Text(text = price, color = Color(0xFF673AB7), fontWeight = FontWeight.Bold)
        }

        QuantitySelector(quantity = quantity)

        Icon(
            painter = painterResource(id = R.drawable.ic_delete),
            contentDescription = "Remove",
            tint = Color.Red,
            modifier = Modifier
                .padding(8.dp)
                .size(24.dp)
                .clickable { /* Handle remove item */ }
        )
    }
}

@Composable
fun QuantitySelector(quantity: Int) {
    var count by remember { mutableStateOf(quantity) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { if (count > 0) count-- }) {
            Icon(painter = painterResource(id = R.drawable.ic_mini), contentDescription = "Minus")
        }
        Text(text = count.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        IconButton(onClick = { count++ }) {
            Icon(painter = painterResource(id = R.drawable.ic_add_circle), contentDescription = "Plus")
        }
    }
}

@Composable
fun OrderSummary() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(10.dp))
            .padding(16.dp)
    ) {
        Text(text = "Order Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Items", color = Color.Gray)
            Text(text = "3")
        }
        Spacer(modifier = Modifier.height(5.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Subtotal", color = Color.Gray)
            Text(text = "$423")
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Discount", color = Color.Gray)
            Text(text = "$4")
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Delivery Charges", color = Color.Gray)
            Text(text = "$2")
        }
        Spacer(modifier = Modifier.height(5.dp))

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Total", fontWeight = FontWeight.Bold)
            Text(text = "$423", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CheckoutButton(navController: NavController) {
    Button(
        onClick = {
            navController.navigate("checkout")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(Color.Black)
    ) {
        Text(text = "Check Out", fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Preview(showSystemUi = true, device = "id:pixel_8_pro")
@Composable
fun PreviewShoppingCartScreen() {
    ShoppingCartScreen(navController = NavController(LocalContext.current))
}
