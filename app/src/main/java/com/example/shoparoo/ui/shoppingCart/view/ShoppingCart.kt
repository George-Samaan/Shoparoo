package com.example.shoparoo.ui.shoppingCart.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shoparoo.R
import com.example.shoparoo.model.LineItem
import com.example.shoparoo.ui.checkOut.AppHeader
import com.example.shoparoo.ui.productScreen.view.LoadingIndicator
import com.example.shoparoo.ui.shoppingCart.viewModel.ShoppingCartViewModel
import com.example.shoparoo.ui.theme.primary
import kotlinx.coroutines.delay


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ShoppingCartScreen(
    navControllerBottom: NavController,
    viewModel: ShoppingCartViewModel,
    navController: NavController
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val isLoading = remember { mutableStateOf(true) }
    val showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getCartItems()
        delay(900)
        isLoading.value = false

        if (cartItems.isEmpty()) {
            showDialog.value = true
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "Cart is empty") },
            text = { Text(text = "Please add items to your cart.") },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    navControllerBottom.navigate("home")
                }) {
                    Text("OK")
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        )
    }

    if (isLoading.value) {
        Scaffold {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        }
    } else {
        Scaffold {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
            ) {
                item {
                    AppHeader(navControllerBottom, title = stringResource(R.string.cart))
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item { ProductList(cartItems, viewModel, showDialog,navController) }

                if (cartItems.isNotEmpty()) {
                    item {
                        val totalItems = cartItems.sumOf { it.quantity }
                        CheckoutButton(navControllerBottom, totalItems)
                    }
                }
            }
        }
    }
}


@Composable
fun ProductList(
    cartItems: List<LineItem>,
    viewModel: ShoppingCartViewModel,
    showDialog: MutableState<Boolean>,
    navController: NavController
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        cartItems.forEach { lineItem ->
            val imageUrl = lineItem.properties[0].value

            ProductItem(
                imageUrl = imageUrl,
                productName = lineItem.title,
                productBrand = lineItem.vendor ?: "Unknown Brand",
                price = "$${lineItem.price}",
                quantity = lineItem.quantity,
                onIncrement = {
                    viewModel.increaseQuantity(lineItem)
                },
                onDecrement = {
                    viewModel.decreaseQuantity(lineItem)
                },
                onRemove = {
                    viewModel.removeItem(lineItem)
                    if (cartItems.size == 1) {
                        showDialog.value = true
                    }
                },
                lineItem.product_id,
                navController
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
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
    productId: Long? = null,
    navController: NavController
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
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    Log.i("ProductItem", "Product ID: $productId")
                    navController.navigate("productDetails/$productId")
                },
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
                .clickable { onRemove() }
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

