package com.example.shoparoo.ui.productDetails

import android.util.Log
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.shoparoo.R
import com.example.shoparoo.data.db.remote.RemoteDataSourceImpl
import com.example.shoparoo.data.network.ApiClient
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.data.repository.RepositoryImpl
import com.example.shoparoo.model.ImagesItem
import com.example.shoparoo.model.SingleProduct

@Composable

fun ProductDetails(id: String) {
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
    when ( ui.value) {
        is ApiState.Loading -> {
            Log.i("ProductDetails", "Loading")
        }
        is ApiState.Failure -> {
            Log.i("ProductDetails", "Error ${(ui.value as ApiState.Failure).message}")
        }
        is ApiState.Success -> {
         val res = ui.value as ApiState.Success
           // Log.i("ProductDetails", "Success ${res.product!!.bodyHtml}")
            productInfo(res.data as SingleProduct)
        }
    }

}

@Composable
private fun productInfo(res: SingleProduct) {
    Log.i("ProductDetails", "Success ${res.product!!.variants!![0]!!.price}")
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(
                state = rememberScrollState(),
                enabled = true
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProductImg(onClick = {},
            res.product.images
            )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = res.product.title!!,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 5.dp)
        )
//        Text(
//            text = "555"+ "$"//stringResource(id = R.string.currency)
//            ,fontSize = 20.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.align(Alignment.End).padding(end = 20.dp)
//        )
        ReviewSection()

        Text(
            text = "Stock: 10", fontSize = 20.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(5.dp)
        )

        DescriptionSection(res.product!!.bodyHtml)

        SizeSection()

        Spacer(modifier = Modifier.weight(1f))

        ButtonSection()
    }
}

@Composable
fun ProductImg(onClick: () -> Unit, images: List<ImagesItem?>?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
            LazyRow (Modifier.fillMaxWidth()){
                    items(images!!.size) { index ->
                        Log.i("ProductDetails", "Success ${images[index]!!.src}")
                        Image(
                            painter = rememberAsyncImagePainter(model = images!![index]!!.src,),
                            contentDescription = null,
                            modifier = Modifier.size(300.dp)
                                .clip(
                                    RoundedCornerShape(
                                        bottomEnd = 20.dp,
                                        bottomStart = 20.dp
                                    )
                                ),
                        )

                    }

            }

        Box(
            modifier = Modifier
                .padding(10.dp)
                .size(50.dp)
                .align(Alignment.TopStart)
                .clip(CircleShape)
                .background(
                    Color(0x4D000000)

                )
        ) {
            IconButton(
                onClick = onClick, // navController.popBackStack()
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }

        Box(modifier = Modifier
                .padding(10.dp)
                .size(50.dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(
                    Color(0x4D000000)

                )
        ) {
            IconButton(
                onClick = onClick, // navController.popBackStack()
            ) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }

    }
}


@Composable
fun ReviewSection() {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Favorite",
            tint = Color.Yellow,
            modifier = Modifier
                .size(50.dp)
                .padding(start = 5.dp)
        )
        Text(
            text = "4.5",
            fontSize = 20.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 10.dp)
        )
        Text(
            text = "(20 Reviews)",
            fontSize = 20.sp,
            color = Color.Gray,
            modifier = Modifier
                .padding(start = 10.dp)
                .clickable {
                    TODO()
                }

        )
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "555" + "$"//stringResource(id = R.string.currency)
            , fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 20.dp)
        )

    }
}

@Composable
fun DescriptionSection(bodyHtml: String?) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = "Description",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 5.dp)

        )

        Text(
            text = bodyHtml!!,
            fontSize = 20.sp,
            modifier = Modifier.padding(5.dp)
        )
    }
}

@Composable
fun SizeSection() {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = "Size",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 5.dp, top = 20.dp)

        )
        Row {

        }
    }
}

@Composable
fun ButtonSection() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp)
    ) {
        Button(
            onClick = {},
            modifier = Modifier
                .padding(horizontal = 50.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Add to Cart",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                fontSize = 20.sp,
            )

            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "Favorite",
                tint = Color.White
            )
        }
    }
}