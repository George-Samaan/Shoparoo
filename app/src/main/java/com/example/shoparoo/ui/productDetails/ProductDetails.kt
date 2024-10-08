package com.example.shoparoo.ui.productDetails

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.shoparoo.data.db.remote.RemoteDataSourceImpl
import com.example.shoparoo.data.network.ApiClient
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.data.repository.RepositoryImpl
import com.example.shoparoo.model.ImagesItem
import com.example.shoparoo.model.SingleProduct
import com.example.shoparoo.model.VariantsItem
import com.example.shoparoo.ui.theme.Purple40
import com.smarttoolfactory.ratingbar.RatingBar
import com.smarttoolfactory.ratingbar.model.Shimmer
import kotlin.random.Random

@Composable
fun ProductDetails(id: String, navController: NavHostController) {
    val viewModel: ProductDetailsViewModel = viewModel(
        factory = ProductDetailsViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )
    val ui = viewModel.singleProductDetail.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.getSingleProductDetail(id)
    }
    when (ui.value) {
        is ApiState.Loading -> {
            Log.i("ProductDetails", "Loading")
        }

        is ApiState.Failure -> {
            Log.i("ProductDetails", "Error ${(ui.value as ApiState.Failure).message}")
        }

        is ApiState.Success -> {
            val res = ui.value as ApiState.Success
            // Log.i("ProductDetails", "Success ${res.product!!.bodyHtml}")
            productInfo(res.data as SingleProduct, navController,viewModel)
        }
    }

}

@Composable
private fun productInfo(
    res: SingleProduct,
    NavController: NavHostController,
    viewModel: ProductDetailsViewModel,
    ) {
    Log.i("ProductDetails", "Success ${res.product!!.variants!![0]!!.price}")
    val selected = remember { mutableStateOf(res.product.variants!![0]) }

    Column(
        Modifier.padding(25.dp)
            .fillMaxSize()
            .verticalScroll(
                state = rememberScrollState(),
                enabled = true
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProductImg(onClick = { NavController.popBackStack() }, images = res.product.images)


        Text(
            text = res.product.title!!,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 5.dp, bottom = 5.dp)
        )

        ReviewSection()

        stockAndPrice(selected)

        VariantSection(res.product.variants, selected)

        DescriptionSection(res.product!!.bodyHtml)

        Spacer(modifier = Modifier.weight(1f))

        BottomSection(onClick = {
            if (viewModel.userMail == null) { //this is bullshit but i'll change it later
               Toast.makeText(NavController.context, "Please login to add to cart", Toast.LENGTH_SHORT).show()
            } else  {
                viewModel.getDraftOrder(res, selected.value!!)
               Toast.makeText(NavController.context, "Added to cart", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

@Composable
private fun stockAndPrice(selected: MutableState<VariantsItem?>) {
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = (selected.value!!.inventoryQuantity).toString() + " items left",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color =  if (selected.value!!.inventoryQuantity!! > 10) Color.Gray else Color.Red
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = selected.value!!.price + " USD",              //stringResource(id = R.string.currency)
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun ProductImg(onClick: () -> Unit, images: List<ImagesItem?>?) {
    Row {
        Box(
            modifier = Modifier
                .size(50.dp)
              //  .align(Alignment.TopStart)
                .clip(CircleShape)
                .background(
                    Color(0x4D000000)

                )
        ) {
            IconButton(
                onClick, // navController.popBackStack()
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }
            Spacer(modifier = Modifier.weight(1f))

    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        LazyRow(Modifier.fillMaxWidth()) {
            items(images!!.size) { index ->
                Log.i("ProductDetails", "Success ${images[index]!!.src}")
                Image(
                    painter = rememberAsyncImagePainter(model = images!![index]!!.src),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewSection() {
    val showContactUsSheet = remember { mutableStateOf(false) }
    val reviews = remember { getRandomReviews(Random.nextInt(2, 22)) }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        RatingBar(
            rating = reviews.second.toFloat(),
            space = 2.dp,
            //change those to use local vector for laterrr
            imageVectorEmpty = Icons.Default.StarOutline,
            imageVectorFFilled = Icons.Default.StarRate,
            tintEmpty = Color.Black,
            tintFilled = Purple40,
            itemSize = 25.dp,
            gestureEnabled = false,
            animationEnabled = true,
            shimmer =  Shimmer(
                color = Purple40,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 5000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ), drawBorder = false,
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
            fontSize = 20.sp,
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
                        fontSize = 20.sp,
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

@Composable
fun ReviewItem(reviews: List<Reviews>) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, top = 5.dp, bottom = 10.dp).verticalScroll(scrollState),
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
                    tintFilled = Purple40,
                    itemSize = 25.dp,
                    gestureEnabled = false,
                    animationEnabled = true,
                    shimmer =  Shimmer(
                        color = Purple40,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 10000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ), drawBorder = false,
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
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = "Description",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 5.dp, top = 5.dp)

        )

        Text(
            text = bodyHtml!!,
            fontSize = 20.sp,
            modifier = Modifier.padding(5.dp)
        )
    }
}

@Composable
fun VariantSection(variants: List<VariantsItem?>?, selected: MutableState<VariantsItem?>) {
    val scrollState = rememberScrollState()
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp) .horizontalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically,

    ) {
        Text(
            text = "Sizes available : ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 5.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))

        for (variant in variants!!) {
            Row(modifier = Modifier
                .background(
                    if (selected.value == variant) Color(0x4D000000) else Color.Transparent,
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
        }
    }
}

@Composable
fun BottomSection(onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 15.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        //add to cart button
        Button(
            onClick = onClick,
           modifier = Modifier.weight(3f)
        ) {
            Text(
                text = "Add to Cart",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                fontSize = 20.sp,
            )

            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "Add to Cart",
                tint = Color.White
            )
        }
      //  Spacer(modifier = Modifier.weight(1f))
        //favorite button
        Button(onClick = {},
            colors = ButtonColors(
                containerColor = Color.Gray,
                //those are placeholders
                contentColor = Color.White,
                disabledContentColor = Color.Gray,
                disabledContainerColor = Color(0xFF000000)),
            modifier = Modifier.padding(horizontal = 10.dp).weight(1f)
            ) {

            Icon(
                imageVector = Icons.Filled.FavoriteBorder,
                contentDescription = "Add to Cart",
                tint = Color.White,
                modifier = Modifier.padding(vertical = 5.dp),

            )
        }
    }
}

@Composable
private fun colorSetter(
    variant: VariantsItem?): Color {
    when (variant!!.option2) {
        "black" -> return Color.Black
        "blue" -> return Color.Blue
        "red" -> return Color.Red
        "white" -> return Color.White
        "gray" -> return Color.Gray
        "yellow" -> return Color.Yellow
        "beige" -> return Color(0xFFF5F5DC)
        "light_brown" -> return Color(0xFFC4A484)
        "burgandy" -> return Color(0x800020)
        else -> return Color.Transparent
    }
}