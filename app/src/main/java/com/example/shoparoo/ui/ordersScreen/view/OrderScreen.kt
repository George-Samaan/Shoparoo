package com.example.shoparoo.ui.ordersScreen.view

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.model.Order
import com.example.shoparoo.ui.ordersScreen.viewModel.OrdersViewModel
import com.example.shoparoo.ui.productScreen.view.LoadingIndicator
import com.example.shoparoo.ui.theme.primary
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OrderScreen(
    orderViewModel: OrdersViewModel,
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 55.dp)
        ) {
            Text(
                text = "Orders",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(end = 50.dp)
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        LaunchedEffect(Unit) {
            orderViewModel.getOrders()
        }

        val ordersState by orderViewModel.orders.collectAsState()

        when (ordersState) {
            is ApiState.Loading -> {
                LoadingIndicator()
            }

            is ApiState.Success -> {
                val orders = (ordersState as ApiState.Success).data
                Log.d("OrdersScreen", "Orders loaded: $orders")
                OrderList(orders as List<Order>)
            }

            is ApiState.Failure -> {
                val errorMessage = (ordersState as ApiState.Failure).message
                Log.d("OrdersScreen", "Failed to load orders: $errorMessage")
            }
        }
    }
}

@Composable
fun OrderList(orders: List<Order>) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    LazyColumn {
        items(orders) { order ->
            AnimatedVisibility(
                visible = isVisible,
                enter = scaleIn(animationSpec = tween(durationMillis = 600)),
                exit = scaleOut(animationSpec = tween(durationMillis = 600))
            ) {
                OrderItem(order = order)
            }
        }
    }
}

@Composable
fun OrderItem(order: Order) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEEEE)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    val formattedDate = changeDateFormat(order.created_at.toString())
                    Text(
                        text = formattedDate,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${order.current_total_price} ${order.currency}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = primary
                    )
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.line_items?.size} Items",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )

                Text(
                    text = order.name.toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier
                        .background(primary, shape = RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(300)) + fadeIn(
                    animationSpec = tween(
                        300
                    )
                ),
                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(
                    animationSpec = tween(
                        300
                    )
                )
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    // Email
                    Text(
                        text = order.email.toString(),
                        fontSize = 15.sp,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow {
                        order.line_items?.forEach { lineItem ->
                            lineItem.properties?.forEach { property ->
                                if (property.name == "imageUrl") {
                                    item {
                                        Image(
                                            painter = rememberAsyncImagePainter(model = property.value),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(80.dp)
                                                .padding(end = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun changeDateFormat(inputDate: String): String {
    val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
    val outputFormatter = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
    val date = inputFormatter.parse(inputDate)
    return if (date != null) {
        outputFormatter.format(date)
    } else {
        "Invalid Date"
    }
}