package com.example.shoparoo.ui.shoppingCart.view

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import coil.compose.AsyncImage
import com.example.shoparoo.model.LineItem
import com.example.shoparoo.ui.checkOut.AppHeader
import com.example.shoparoo.ui.shoppingCart.viewModel.ShoppingCartViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ShoppingCartScreen(
    navController: NavController,
    viewModel: ShoppingCartViewModel
) {
    val cartItems by viewModel.cartItems.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getCartItems()
    }

    val totalItems = cartItems.sumOf { it.quantity } // Calculate total items in cart

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

            item { ProductList(cartItems, viewModel) }

            item {
                CheckoutButton(navController, totalItems) // Pass the total items count
            }
        }
    }
}


@Composable
fun ProductList(cartItems: List<LineItem>, viewModel: ShoppingCartViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        cartItems.forEach { lineItem ->
            val imageUrl = lineItem.properties[0].value

            ProductItem(
                imageUrl = imageUrl,
                productName = lineItem.title,
                productBrand = lineItem.vendor ?: "Unknown Brand",
                price = "$${lineItem.price}",
                quantity = lineItem.quantity,
                onIncrement = { viewModel.incrementItemCount(lineItem) },
                onDecrement = { viewModel.decrementItemCount(lineItem) }
            )
        }
    }
}



@Composable
fun ProductItem(
    imageUrl: String,
    productName: String,
    productBrand: String,
    price: String,
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
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
                fontSize = 14.sp,
                color = primary
            )
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = productBrand,
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(5.dp))

            Text(text = price, color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.W400)
        }

        QuantitySelector(quantity = quantity, onIncrement = onIncrement, onDecrement = onDecrement)

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
fun QuantitySelector(quantity: Int, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            if (quantity > 0) {
                onDecrement()
            }
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_mini),
                contentDescription = "Minus",
                tint = Color.Gray
            )
        }
        Text(text = quantity.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        IconButton(onClick = {
            onIncrement()
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_circle),
                contentDescription = "Plus",
                tint = primary
            )
        }
    }
}



@Composable
fun CheckoutButton(navController: NavController, totalItems: Int) {
    Button(
        onClick = { navController.navigate("checkout") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(primary)
    ) {
        Text(text = "Proceed to Checkout ($totalItems items)", color = Color.White)
    }
}
