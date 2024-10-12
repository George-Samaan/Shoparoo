package com.example.shoparoo.ui.Favourites

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.example.shoparoo.data.repository.RepositoryImpl
import com.example.shoparoo.ui.auth.view.ReusableLottie
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

    val favProducts = viewModel.productItems.collectAsStateWithLifecycle()

    // may run multiple times
    LaunchedEffect(favProducts.value) {
        viewModel.getFavourites()
    }

    if (favProducts.value.isEmpty()) {
        Column(
            Modifier
                .padding(top = 100.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //   Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Favourites", modifier = Modifier.size(300.dp))

            ReusableLottie(R.raw.empty, null, size = 400.dp)
            Text(text = "No Favourites Found", fontSize = 35.sp, fontWeight = FontWeight.Bold)
        }
    }
    TopBar(navController = navController, title = "Favourites", top = 50.dp)
    Column(
        Modifier
            .padding(top = 100.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val context = LocalContext.current
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        // Get saved currency and conversion rate from SharedPreferences
        val selectedCurrency = remember { sharedPreferences.getString("currency", "USD") ?: "USD" }
        val conversionRate = remember { sharedPreferences.getFloat("conversionRate", 1.0f) }
        val currencySymbols = mapOf(
            "USD" to "EGP ",
            "EGP" to "$ "
        )


        //galal handle currency conversion
        //  ProductGridd(favProducts.value, navController, "gg", 1.0f, emptyMap(),true,viewModel)

        ProductGrid(favProducts.value, navController, selectedCurrency, conversionRate, currencySymbols,true, viewModel)

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
