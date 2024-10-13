package com.example.shoparoo.ui.productDetails.view

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.shoparoo.R
import com.example.shoparoo.data.db.remote.RemoteDataSourceImpl
import com.example.shoparoo.data.network.ApiClient
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.data.repository.RepositoryImpl
import com.example.shoparoo.model.DraftOrderDetails
import com.example.shoparoo.model.ImagesItem
import com.example.shoparoo.model.SingleProduct
import com.example.shoparoo.model.VariantsItem
import com.example.shoparoo.ui.auth.viewModel.AuthState
import com.example.shoparoo.ui.auth.viewModel.AuthViewModel
import com.example.shoparoo.ui.homeScreen.view.capitalizeWords
import com.example.shoparoo.ui.productDetails.viewModel.ProductDetailsViewModel
import com.example.shoparoo.ui.productDetails.viewModel.ProductDetailsViewModelFactory
import com.example.shoparoo.ui.productScreen.view.LoadingIndicator
import com.example.shoparoo.ui.theme.darkGreen
import com.example.shoparoo.ui.theme.grey
import com.example.shoparoo.ui.theme.primary
import com.smarttoolfactory.ratingbar.RatingBar
import com.smarttoolfactory.ratingbar.model.Shimmer
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ProductDetails(id: String, navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val viewModel: ProductDetailsViewModel = viewModel(
        factory = ProductDetailsViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )
    val ui = viewModel.singleProductDetail.collectAsState()
    val order = viewModel.userOrder.collectAsState()
    val isFav = viewModel.isFav.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getSingleProductDetail(id)
    }

    var itemsIncart by remember { mutableIntStateOf(0) }
    when (ui.value) {
        is ApiState.Loading -> {
            Log.i("ProductDetails", "Loading")
            Box(Modifier.padding(top = 70.dp)) {
                LoadingIndicator()
            }
        }

        is ApiState.Failure -> {
            Log.i("ProductDetails", "Error ${(ui.value as ApiState.Failure).message}")
        }

        is ApiState.Success -> {
            val data = ui.value as ApiState.Success
            ProductInfo(
                data.data as SingleProduct,
                navController,
                viewModel,
                isFav.value,
                itemsIncart
            )
        }
    }

    when (order.value) {
        is ApiState.Loading -> {
            Log.i("ProductDetails", "Loading")
        }

        is ApiState.Failure -> {
            Log.i("ProductDetails", "Error ${(order.value as ApiState.Failure).message}")
        }

        is ApiState.Success -> {
            val gg = order.value as ApiState.Success
            val data = gg.data as DraftOrderDetails
            itemsIncart = data.line_items[0].quantity
            Log.i("ProductDetailsUserOrderrrrrrrrr", "Success ${data.line_items.size}")
        }
    }

}

@Composable
private fun ProductInfo(
    singleProductDetail: SingleProduct,
    navController: NavHostController,
    viewModel: ProductDetailsViewModel,
    isFav: Boolean,
    itemsIncart: Int,

    ) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val selectedCurrency = remember { sharedPreferences.getString("currency", "USD") ?: "USD" }
    val conversionRate = remember { sharedPreferences.getFloat("conversionRate", 1.0f) }
    val selected = remember { mutableStateOf(singleProductDetail.product!!.variants!![0]) }
    val isLoggedIn = AuthViewModel().authState.collectAsState()
    val descriptionVisible = remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    // Trigger the animation when the product info is loaded
    LaunchedEffect(singleProductDetail) {
        descriptionVisible.value = true
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(top= 5.dp)
            .verticalScroll(state = rememberScrollState(), enabled = true),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProductImg(
            onClick = { navController.popBackStack() },
            images = singleProductDetail.product!!.images
        )

        Column(
            modifier = Modifier.padding(start = 25.dp, end = 25.dp, bottom = 25.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = singleProductDetail.product.title!!.capitalizeWords(),
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 5.dp)
            )
            ReviewSection()
            StockAndPrice(selected, selectedCurrency, conversionRate)
            VariantSection(singleProductDetail.product.variants, selected)


            AnimatedVisibility(
                visible = descriptionVisible.value,
                enter = slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn(),
                exit = fadeOut()
            ) {
                DescriptionSection(singleProductDetail.product.bodyHtml)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        BottomSection(
            onClickCart = {
                Log.i(
                    "ProductDetail",
                    "Cartclickeddd   ${selected.value!!.inventoryQuantity}    $itemsIncart"
                )
                if (selected.value!!.inventoryQuantity!! < 1) {
                    Toast.makeText(context, "Out of stock", Toast.LENGTH_SHORT).show()
                } /*else if (itemsIncart >= selected.value!!.inventoryQuantity!!) {
                    Toast.makeText(
                        context,
                        "you've already added $itemsIncart ",
                        Toast.LENGTH_SHORT
                    ).show()
                } */ else {
                    if (isLoggedIn.value != AuthState.Authenticated && isLoggedIn.value != AuthState.UnVerified) { //this is bullshit but i'll change it later
//                        Toast.makeText(context, "Please login to add to cart", Toast.LENGTH_SHORT).show()
                        showDialog = true
//
                    } else {
                        viewModel.getDraftOrder(singleProductDetail, selected.value!!, true)
                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onClickFav = {
                if (isLoggedIn.value != AuthState.Authenticated && isLoggedIn.value != AuthState.UnVerified) { //this is bullshit but i'll change it later
                    showDialog = true

                } else {
                    viewModel.getDraftOrder(singleProductDetail, selected.value!!, false)
                    if (!isFav)
                        Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                }
            },
            buttonColors = if (isFav) {
                ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.Yellow,
                    disabledContentColor = Color.Gray,
                    disabledContainerColor = Color(0xFF000000),
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White,
                    disabledContentColor = Color.Gray,
                    disabledContainerColor = Color(0xFF000000),
                )
            },
            isFav = isFav
        )

    }
    // Show the login dialog if required
    if (showDialog) {
        LoginDialog(
            onDismiss = { showDialog = false },
            onLogin = {
                navController.navigate("login")
                {
                    popUpTo("login") {
                        inclusive = true
                    }
                }
            }
        )
    }

}


@Composable
fun ProductImg(onClick: () -> Unit, images: List<ImagesItem?>?) {
    val imageVisible = remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        imageVisible.value = true
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(top = 30.dp, start = 5.dp)
    ) {

        AnimatedVisibility(
            visible = imageVisible.value,
            enter = slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn(),
            exit = fadeOut()
        ) {
            LazyRow(Modifier.fillMaxWidth()) {
                items(images!!.size) { index ->
                    Image(
                        painter = rememberAsyncImagePainter(model = images[index]!!.src),
                        contentDescription = null,
                        modifier = Modifier
                            .size(300.dp)
                            .clip(
                                RoundedCornerShape(
                                    bottomEnd = 20.dp,
                                    bottomStart = 20.dp
                                )
                            ),
                    )
                }
            }
        }

        IconButton(onClick = onClick) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    //.padding(top = 10.dp)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_back_2),
                    contentDescription = stringResource(R.string.back),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewSection() {
    val showContactUsSheet = remember { mutableStateOf(false) }
    val reviews = remember { getRandomReviews(Random.nextInt(2, 22)) }
    val reviewVisible = remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        reviewVisible.value = true
    }


    AnimatedVisibility(
        visible = reviewVisible.value,
        enter = slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RatingBar(
                rating = reviews.second.toFloat(),
                space = 2.dp,
                imageVectorEmpty = Icons.Default.StarOutline,
                imageVectorFFilled = Icons.Default.StarRate,
                tintEmpty = Color.Gray,
                itemSize = 30.dp,
                gestureEnabled = false,
                animationEnabled = true,
                shimmer = Shimmer(
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 5000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    drawBorder = false,
                ),
            )
            Text(
                text = reviews.second.toString(),
                fontSize = 20.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 10.dp)
            )

            Text(
                text = "(${reviews.first.size} reviews)",
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable { showContactUsSheet.value = true }
            )

            if (showContactUsSheet.value) {
                ModalBottomSheet(onDismissRequest = { showContactUsSheet.value = false }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Reviews",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(5.dp)
                        )
                        ReviewItem(reviews.first)
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StockAndPrice(
    selected: MutableState<VariantsItem?>,
    selectedCurrency: String,
    conversionRate: Float
) {
    val currencySymbols = mapOf(
        "EGP" to "$ ",
        "USD" to "EGP "
    )
    val priceInUSD = selected.value?.price?.toFloatOrNull() ?: 0f
    val convertedPrice = priceInUSD * conversionRate
    val formattedPrice = String.format("%.2f", convertedPrice)


    val stockPriceVisible = remember { mutableStateOf(false) }

    // Trigger the animation when StockAndPrice is loaded
    LaunchedEffect(Unit) {
        stockPriceVisible.value = true
    }

    // Animate the stock and price section sliding in from the right
    AnimatedVisibility(
        visible = stockPriceVisible.value,
        enter = slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = if (selected.value!!.inventoryQuantity!! < 0) " 0 In Stock" else (selected.value!!.inventoryQuantity!!).toString() + " item left",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selected.value!!.inventoryQuantity!! > 6) darkGreen else Color.Red
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = formattedPrice + " " + currencySymbols[selectedCurrency],
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun VariantSection(variants: List<VariantsItem?>?, selected: MutableState<VariantsItem?>) {
    val scrollState = rememberScrollState()
    val variantSectionVisible = remember { mutableStateOf(false) }

    // Trigger the animation when VariantSection is loaded
    LaunchedEffect(Unit) {
        variantSectionVisible.value = true
    }

    // Animate the variant section sliding in from the right
    AnimatedVisibility(
        visible = variantSectionVisible.value,
        enter = slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
                .horizontalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Sizes Available : ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 5.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))

            for (variant in variants!!) {
                Row(
                    modifier = Modifier
                        .background(
                            if (selected.value == variant) Color(0xFFEFEFEF) else Color.Transparent,
                            RoundedCornerShape(40.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .clickable {
                            selected.value = variant
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val color = colorSetter(variant)
                    Box(
                        modifier = Modifier
                            .size(17.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(color)
                    )
                    Text(
                        text = " " + variant!!.option1!!,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
            }
        }
    }
}


@Composable
fun ReviewItem(reviews: List<Reviews>) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, top = 5.dp, bottom = 10.dp)
            .verticalScroll(scrollState),
    ) {
        reviews.forEach() {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 5.dp),

                ) {

                Image(
                    painter = painterResource(id = it.userImage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )

                Text(
                    text = it.name,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(5.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                RatingBar(
                    rating = it.rating,
                    space = 2.dp,
                    imageVectorEmpty = Icons.Default.StarOutline,
                    imageVectorFFilled = Icons.Default.StarRate,
                    tintEmpty = Color.Black,
                    itemSize = 25.dp,
                    gestureEnabled = false,
                    animationEnabled = true,
                    shimmer = Shimmer(
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 10000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        drawBorder = false,
                    ),
                )

            }
            Text(
                text = it.review,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

@Composable
fun DescriptionSection(bodyHtml: String?) {
    // Animate the offset from the left
    val offsetX by animateDpAsState(targetValue = if (bodyHtml != null) 0.dp else -100.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = offsetX)
    ) {
        Text(
            text = "Description:",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 5.dp, top = 5.dp, bottom = 15.dp)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFEFEF), RoundedCornerShape(8.dp))
                .padding(12.dp),
            text = bodyHtml ?: "",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Normal,

            )
    }
}

@Composable
fun BottomSection(
    onClickCart: () -> Unit,
    onClickFav: () -> Unit,
    buttonColors: ButtonColors,
    isFav: Boolean
) {
    var isAnimatingFav by remember { mutableStateOf(false) }

    // Trigger the animation when the button is clicked
    val iconScale by animateFloatAsState(targetValue = if (isAnimatingFav) 1.5f else 1f)
    val iconTint = if (isFav) Color.White else Color.White // Fill color when favorited

    // Reset the animation state after a delay when animating
    if (isAnimatingFav) {
        LaunchedEffect(Unit) {
            delay(300) // Duration of the animation
            isAnimatingFav = false
        }
    }
    var isAnimating by remember { mutableStateOf(false) }

    // Trigger the animation when the button is clicked
    val iconOffset by animateFloatAsState(targetValue = if (isAnimating) 70f else 0f)
    val textVisibility by animateFloatAsState(targetValue = if (isAnimating) 0f else 1f)
    val buttonHeight by animateDpAsState(targetValue = if (isAnimating) 40.dp else 50.dp) // Change the height here

    // Reset the animation state after a delay when animating
    if (isAnimating) {
        LaunchedEffect(Unit) {
            delay(1000) // Duration of the animation
            isAnimating = false
        }
    }


    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 15.dp, top = 15.dp, start = 25.dp, end = 25.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        // Add to Cart button
        Button(
            onClick = {
                isAnimating = true
                onClickCart()
            },
            colors = ButtonDefaults.buttonColors(primary),
            modifier = Modifier
                .weight(3f)
                .height(buttonHeight) // Use the animated height here
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "Add to Cart",
                tint = Color.White,
                modifier = Modifier.offset(x = iconOffset.dp)
            )

            AnimatedVisibility(visible = textVisibility > 0) {
                Text(
                    text = "Add to Cart",
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .alpha(textVisibility),
                    fontSize = 20.sp
                )
            }
        }
        // Favorite button
        Button(
            onClick = {
                isAnimatingFav = true
                onClickFav()
            },
            colors = buttonColors,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .size(50.dp)
                .weight(1f)
        ) {
            Icon(
                imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Add to Favorites",
                tint = iconTint,
                modifier = Modifier.scale(iconScale) // Apply scale animation
            )
        }
    }
}


@Composable
private fun colorSetter(variant: VariantsItem?): Color {
    when (variant!!.option2) {
        "black" -> return Color.Black
        "blue" -> return Color.Blue
        "red" -> return Color.Red
        "white" -> return Color.White
        "gray" -> return Color.Gray
        "yellow" -> return Color.Yellow
        "beige" -> return Color(0xFFF5F5DC)
        "light_brown" -> return Color(0xFFC4A484)
        "burgandy" -> return Color(0xFF800020)
        else -> return Color.Transparent
    }
}

@Composable
fun LoginDialog(
    onDismiss: () -> Unit,
    onLogin: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Authentication Required") },
        text = { Text("You must log in to access this feature.") },
        confirmButton = {
            Button(onClick = {
                onLogin()
                onDismiss()
            }, colors = ButtonDefaults.buttonColors(primary)) {
                Text("Login")
            }
        },
        containerColor = Color.White,
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(grey)
            ) {
                Text("Cancel")
            }
        }
    )
}
