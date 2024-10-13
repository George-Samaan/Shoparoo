package com.example.shoparoo.ui.shoppingCart.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shoparoo.R
import com.example.shoparoo.model.LineItem
import com.example.shoparoo.ui.auth.view.ReusableLottie
import com.example.shoparoo.ui.homeScreen.view.capitalizeWords
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


    LaunchedEffect(Unit) {
        isLoading.value = true
        viewModel.getCartItems()
        delay(900)
        isLoading.value = false
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
        } else if (cartItems.isEmpty()) {
            Scaffold {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        Text(
                            text = "Cart",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = primary,
                            textAlign = TextAlign.Center,
                        )
                    }

                Box(
                    modifier = Modifier
                        .fillMaxSize().fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        ReusableLottie(R.raw.cart, null, size = 400.dp, speed = 0.66f)
                        Text(
                            text = "No Items Found",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        } else {
            Scaffold {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, start = 55.dp)
                        ) {
                            Text(
                                text = "Cart",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = primary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(end = 50.dp)
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item { ProductList(cartItems, viewModel, navController) }

                    if (cartItems.isNotEmpty()) {
                        item {
                            val totalItems = cartItems.sumOf { it.quantity }
                            CheckoutButton(navControllerBottom, totalItems, viewModel)
                        }
                    }

                }
            }
        }

}

@SuppressLint("DefaultLocale")
@Composable
fun ProductList(
    cartItems: List<LineItem>,
    viewModel: ShoppingCartViewModel,
    navController: NavController,
) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    val selectedCurrency = remember { sharedPreferences.getString("currency", "USD") ?: "USD" }
    val conversionRate = remember { sharedPreferences.getFloat("conversionRate", 1.0f) }

    val currencySymbols = mapOf(
        "EGP" to "$ ",
        "USD" to "EGP "
    )


    Column(modifier = Modifier.fillMaxWidth()) {
        cartItems.forEach { lineItem ->
            val imageUrl = lineItem.properties[0].value

            val productPrice = lineItem.price.toDoubleOrNull()
            val priceConverted = productPrice?.times(conversionRate)
            val formattedPrice = String.format("%.2f", priceConverted)


            ProductItem(
                imageUrl = imageUrl,
                productName = lineItem.title.capitalizeWords(),
                productBrand = lineItem.vendor?.capitalizeWords() ?: "Unknown Brand",
                price = "${currencySymbols[selectedCurrency]}${formattedPrice}",
                quantity = lineItem.quantity,
                onIncrement = {
                    val currentQ = lineItem.quantity
                    if (currentQ >=5){
                        Toast.makeText(context,"Capacity full for you", Toast.LENGTH_SHORT).show()
                    }else{
                        viewModel.increaseQuantity(lineItem)
                    }

                },
                onDecrement = {
                    viewModel.decreaseQuantity(lineItem)
                },
                onRemove = {
                    viewModel.removeItem(lineItem)
                },
                lineItem.product_id,
                navController,
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
                tint = Color.Black
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
fun CheckoutButton(
    navController: NavController,
    totalItems: Int,
    viewModel: ShoppingCartViewModel
) {

    if (totalItems == 15){
        Toast.makeText(LocalContext.current, "Capacity full for you", Toast.LENGTH_SHORT).show()
    }
    Button(
        onClick = {
            navController.navigate("checkout")
            //viewModel.clearCart()
        },
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
