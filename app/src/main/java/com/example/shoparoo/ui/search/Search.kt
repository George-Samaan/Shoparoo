package com.example.shoparoo.ui.search

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.shoparoo.ui.homeScreen.viewModel.HomeViewModel
import com.example.shoparoo.ui.homeScreen.viewModel.HomeViewModelFactory
import com.example.shoparoo.ui.productScreen.view.ProductGrid
import com.example.shoparoo.ui.productScreen.view.TopBar
import com.example.shoparoo.ui.theme.primary


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(navController: NavController) {

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

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(10.dp))
        TopBar(navController = navController, title = "Search")

        val searchQuery = remember { mutableStateOf("") }
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .padding(horizontal = 15.dp)
                .height(56.dp),
            value = searchQuery.value,
            onValueChange = { value ->
                searchQuery.value = value
                Log.i("Search", "Search query: $value")
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
            }

            is ApiState.Failure -> {
                Log.i("Search", "Failure")
            }

            is ApiState.Success -> {
                Log.i("Searchdddddddd", "Success")
                val products = (allProducts.value as ApiState.Success).data as List<ProductsItem>
                filterItems(products, searchQuery.value, navController)
            }
        }
    }
}

@Composable
fun filterItems(products: List<ProductsItem>, query: String, navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    // Get saved currency and conversion rate from SharedPreferences
    val selectedCurrency = remember { sharedPreferences.getString("currency", "EGP") ?: "EGP" }
    val conversionRate = remember { sharedPreferences.getFloat("conversionRate", 1.0f) }


    val currencySymbols = mapOf(
        "USD" to "$ ",
        "EGP" to "EGP "
    )
    var filteredProducts: MutableList<ProductsItem> = mutableListOf()
    for (product in products) {
        if (product.title!!.contains(query, ignoreCase = true)) {
            Log.i("Search", "Product: ${product.title}")
            filteredProducts.add(product)
           Log.i("SearchFilter", "Filtered products: $filteredProducts")
        }
    }
    ProductGrid(filteredProducts, navController, selectedCurrency, conversionRate, currencySymbols, false)
}
