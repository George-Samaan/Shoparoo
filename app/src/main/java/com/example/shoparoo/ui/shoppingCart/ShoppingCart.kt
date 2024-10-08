package com.example.shoparoo.ui.shoppingCart

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoparoo.R
import com.example.shoparoo.ui.theme.primary
import androidx.compose.foundation.lazy.LazyColumn
import com.example.shoparoo.ui.checkOut.AppHeader

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
            ),
        )

    }


    Scaffold {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp)
        ) {
            item {
                AppHeader(navController, title = stringResource(R.string.cart))
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
          item { ProductList(productList)}

            item {
                CheckoutButton(navController)
            }
        }
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
fun ProductItem(imageRes: Int, productName: String, productBrand: String, price: String, quantity: Int) {
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


