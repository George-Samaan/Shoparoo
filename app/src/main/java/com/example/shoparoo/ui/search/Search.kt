package com.example.shoparoo.ui.search

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shoparoo.R
import com.example.shoparoo.data.db.remote.RemoteDataSourceImpl
import com.example.shoparoo.data.network.ApiClient
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.data.repository.RepositoryImpl
import com.example.shoparoo.model.ProductsItem
import com.example.shoparoo.ui.Favourites.viewModel.FavouritesViewModel
import com.example.shoparoo.ui.Favourites.viewModel.FavouritesViewModelFactory
import com.example.shoparoo.ui.homeScreen.viewModel.HomeViewModel
import com.example.shoparoo.ui.homeScreen.viewModel.HomeViewModelFactory
import com.example.shoparoo.ui.productScreen.view.LoadingIndicator
import com.example.shoparoo.ui.productScreen.view.ProductGrid
import com.example.shoparoo.ui.productScreen.view.TopBar
import com.example.shoparoo.ui.theme.primary
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(navController: NavController) {
    val favViewModel: FavouritesViewModel = viewModel(
        factory = FavouritesViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )
    LaunchedEffect(Unit) {
        favViewModel.getFavourites()
    }

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )
    LaunchedEffect(Unit) {
        viewModel.getAllProducts()
    }
    val allProducts = viewModel.allProducts.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var filteredProducts by remember { mutableStateOf(emptyList<ProductsItem>()) }
    var isFilteringComplete by remember { mutableStateOf(false) }
    var isGridVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(10.dp))
        TopBar(navController = navController, title = "Search", 50.dp)

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .padding(horizontal = 15.dp)
                .height(56.dp),
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                isFilteringComplete = false
                Log.i("Search", "Search query: $query")
            },
            placeholder = { Text(text = "Search", color = primary) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    tint = primary
                )
            },
            shape = RoundedCornerShape(28.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE0E0E0),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        when (allProducts.value) {
            is ApiState.Loading -> {
                Log.i("Search", "Loading")
                LoadingIndicator()
            }

            is ApiState.Failure -> {
                Log.i("Search", "Failure")
            }

            is ApiState.Success -> {
                val products = (allProducts.value as ApiState.Success).data as List<ProductsItem>

                LaunchedEffect(searchQuery) {
                    delay(200)
                    filteredProducts =
                        products.filter { it.title!!.contains(searchQuery, ignoreCase = true) }
                    isFilteringComplete = true
                }

                LaunchedEffect(isFilteringComplete) {
                    isGridVisible = isFilteringComplete
                }

                AnimatedVisibility(
                    visible = isGridVisible,
                    enter = scaleIn(animationSpec = tween(durationMillis = 800)) + slideInHorizontally(),
                    exit = scaleOut(animationSpec = tween(durationMillis = 800)) + slideOutHorizontally()
                ) {
                    if (filteredProducts.isEmpty()) {
                        ProductInfoMessage(
                            isEmpty = true,
                        )
                    } else {
                        FilterItems(filteredProducts, searchQuery, navController, favViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductInfoMessage(isEmpty: Boolean) {
    val message = if (isEmpty) {
        "No products found. Try adjusting your search."
    } else {
        ""
    }

    AnimatedVisibility(
        visible = isEmpty,
        enter = fadeIn(animationSpec = tween(800)) + scaleIn(),
        exit = fadeOut(animationSpec = tween(800))
    ) {
        Text(
            message,
            modifier = Modifier.padding(top = 17.dp, start = 27.dp),
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FilterItems(
    products: List<ProductsItem>,
    query: String,
    navController: NavController,
    favViewModel: FavouritesViewModel
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val selectedCurrency = remember { sharedPreferences.getString("currency", "USD") ?: "USD" }
    val conversionRate = remember { sharedPreferences.getFloat("conversionRate", 1.0f) }

    val currencySymbols = mapOf(
        "EGP" to "$ ",
        "USD" to "EGP "
    )
    var filteredProducts: MutableList<ProductsItem> = mutableListOf()
    for (product in products) {
        if (product.title!!.contains(query, ignoreCase = true)) {
            Log.i("Search", "Product: ${product.title}")
            filteredProducts.add(product)
            Log.i("SearchFilter", "Filtered products: $filteredProducts")
        }
    }
    ProductGrid(filteredProducts, navController,
        selectedCurrency, conversionRate, currencySymbols, false,
        viewModel = favViewModel
    )
}
