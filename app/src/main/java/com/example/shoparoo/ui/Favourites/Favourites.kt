package com.example.shoparoo.ui.Favourites

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shoparoo.R
import com.example.shoparoo.data.db.remote.RemoteDataSourceImpl
import com.example.shoparoo.data.network.ApiClient
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.data.repository.RepositoryImpl
import com.example.shoparoo.ui.auth.view.ReusableLottie
import com.example.shoparoo.ui.productScreen.view.LoadingIndicator
import com.example.shoparoo.ui.productScreen.view.ProductGrid
import com.example.shoparoo.ui.productScreen.view.TopBar

@Composable
fun Favourites(navController: NavController) {
    val viewModel: FavouritesViewModel = viewModel(
        factory = FavouritesViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val selectedCurrency = remember { sharedPreferences.getString("currency", "USD") ?: "USD" }
    val conversionRate = remember { sharedPreferences.getFloat("conversionRate", 1.0f) }
    val currencySymbols = mapOf(
        "USD" to "EGP ", "EGP" to "$ "
    )
    val favProducts = viewModel.productItems.collectAsStateWithLifecycle()
    val apiState = viewModel.draftOrderFav.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getFavourites()
    }

    TopBar(navController = navController, title = "Favourites", top = 50.dp)

    when (apiState.value) {
        is ApiState.Loading -> {
            Column(
                Modifier
                    .padding(top = 80.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoadingIndicator()
            }
        }

        is ApiState.Success -> {
            var isGridVisible by remember { mutableStateOf(false) }

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 100.dp)
                    .fillMaxSize()
            ) {
                LaunchedEffect(favProducts.value) {
                    isGridVisible = favProducts.value.isNotEmpty()
                }

                AnimatedVisibility(
                    visible = isGridVisible,
                    enter = scaleIn(animationSpec = tween(durationMillis = 800)),
                    exit = scaleOut(animationSpec = tween(durationMillis = 800))
                ) {
                    ProductGrid(
                        favProducts.value, navController, selectedCurrency, conversionRate, currencySymbols, true, viewModel)
                }

                if (!isGridVisible && favProducts.value.isEmpty()) {
                    Column(
                        Modifier
                            .padding(top = 45.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ReusableLottie(R.raw.cart, null, size = 400.dp, 0.66f)
                        Text(
                            text = "No Items Found",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        is ApiState.Failure -> {
            Column(
                Modifier
                    .padding(top = 80.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Failed to load favourites",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/*

@Composable
fun ProductGridd(
    filteredProducts: List<ProductsItem>,
    navController: NavController?,
    selectedCurrency: String,
    conversionRate: Float,
    currencySymbols: Map<String, String>,
    inFav: Boolean = false,
    viewModel: FavouritesViewModel,

    ) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(filteredProducts.size) { index ->
            val product = filteredProducts[index]
            val fullTitle = product.title ?: "Unknown"
            val productName = fullTitle.split("|").getOrNull(1)?.trim() ?: fullTitle
            val priceInUSD = product.variants?.get(0)?.price?.toDoubleOrNull() ?: 0.0
            val convertedPrice = priceInUSD * conversionRate
            val formattedPrice = String.format("%.2f", convertedPrice)

            ProductCard(
                productName = productName,
                productPrice = "$formattedPrice",
                productImage = product.images?.firstOrNull()?.src ?: "",
                onClick = {
                    navController!!.navigate("productDetails/${product.id}")
                },
                currencySymbol = currencySymbols.getOrDefault(selectedCurrency, "$"),
                inFav = inFav,
                onClickDeleteFav = {
                    Log.i("FavouritesViewModeldeleteeeee", "onClickDeleteFav: ${product.id}")
                  //  viewModel.deleteFav(product.id)
                    viewModel.getFavourites(true, product.id!!)


                })


        }
    }
}






@Composable
fun myProductGrid(
    filteredProducts: List<Triple<String, String, String>>,
    navController: NavController?,
    selectedCurrency: String,
    conversionRate: Float,
    currencySymbols: Map<String, String>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(filteredProducts.size) { index ->
            val product = filteredProducts[index]
            val fullTitle = filteredProducts.first() ?: "Unknown"
            val productName = "dfdf"
            val priceInUSD = 10.0f
            val convertedPrice = "riceInUSD * conversionRate"
            val formattedPrice = "%.2f"

            ProductCard(
                productName = product.second,
                productPrice = product.third.toString(),
                productImage = product.first,
                onClick = {
                   // navController!!.navigate("productDetails/${product.id}")
                },
                currencySymbol = currencySymbols.getOrDefault(selectedCurrency, "$")

            )
        }
    }
}

*/
