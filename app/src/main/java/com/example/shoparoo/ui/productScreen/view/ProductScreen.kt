@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.shoparoo.ui.productScreen.view

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoparoo.R
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.model.ProductsItem
import com.example.shoparoo.ui.homeScreen.view.ProductCard
import com.example.shoparoo.ui.productScreen.viewModel.ProductViewModel
import com.example.shoparoo.ui.theme.primary
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun ProductsScreen(
    brandId: String,
    brandTitle: String,
    navControllerBottom: NavController,
    viewModel: ProductViewModel,

     navController: NavController,


    navController: NavController

) {
    var searchQuery by remember { mutableStateOf("") }
    var products by remember { mutableStateOf(emptyList<ProductsItem>()) }
    var filteredProducts by remember { mutableStateOf(emptyList<ProductsItem>()) }
    var isGridVisible by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableIntStateOf(0) }
    var maxPrice by remember { mutableIntStateOf(2500) }
    var isInitialLoad by remember { mutableStateOf(true) }
    var isReady by remember { mutableStateOf(false) }
    var isFilteringComplete by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    // Get saved currency and conversion rate from SharedPreferences
    val selectedCurrency = remember { sharedPreferences.getString("currency", "USD") ?: "USD" }
    val conversionRate = remember { sharedPreferences.getFloat("conversionRate", 1.0f) }

    val currencySymbols = mapOf(
        "USD" to "$",
        "EGP" to "EGP "
    )


    // Reset isGridVisible and loading state on initial load
    LaunchedEffect(Unit) {
        if (isInitialLoad) {
            isGridVisible = false
            isInitialLoad = false
        }
    }

    LaunchedEffect(brandId) {
        isReady = false
        viewModel.getProductsFromBrandsId(brandId)
    }

    val productsState = viewModel.productsFromBrands.collectAsState()

    LaunchedEffect(productsState.value) {
        when (val state = productsState.value) {
            is ApiState.Success -> {
                products = (state.data as? List<ProductsItem>) ?: emptyList()
                filteredProducts = products
                maxPrice = products.map {
                    it.variants?.firstOrNull()?.price?.toFloatOrNull()?.toInt() ?: 0
                }.maxOrNull() ?: 2500
                sliderValue = maxPrice
                isReady = true
                isFilteringComplete = true
            }

            is ApiState.Loading -> {
                isReady = false
                isFilteringComplete = false
            }

            is ApiState.Failure -> {
                isReady = true
            }
        }
    }

    LaunchedEffect(searchQuery, sliderValue) {
        isFilteringComplete = false
        delay(300)
        filteredProducts = products.filter { product ->
            val productPrice = product.variants?.firstOrNull()?.price?.toFloatOrNull()?.toInt() ?: 0
            val matchesSearch = searchQuery.isEmpty() || product.title?.contains(
                searchQuery,
                ignoreCase = true
            ) == true
            val withinPriceRange = productPrice <= sliderValue
            matchesSearch && withinPriceRange
        }
        isFilteringComplete = true
    }

    LaunchedEffect(isReady, isFilteringComplete) {
        isGridVisible = isReady && isFilteringComplete
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(navControllerBottom, brandTitle)
        SearchBar(searchQuery) { query -> searchQuery = query }

        if (!isReady) {
            LoadingIndicator()
        } else {
            PriceSlider(sliderValue, maxPrice) { newValue -> sliderValue = newValue }

            AnimatedContent(targetState = filteredProducts.isEmpty()) { isEmpty ->
                ProductInfoMessage(isEmpty = isEmpty, sliderValue = sliderValue)
            }

            AnimatedVisibility(
                visible = isGridVisible,
                enter = scaleIn(animationSpec = tween(durationMillis = 600)),
                exit = scaleOut(animationSpec = tween(durationMillis = 600))
            ) {

                ProductGrid(filteredProducts,navController, selectedCurrency, conversionRate, currencySymbols)

                ProductGrid(filteredProducts, navController)

            }
        }
    }
}

@Composable
fun ProductGrid(
    filteredProducts: List<ProductsItem>,
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
            val fullTitle = product.title ?: "Unknown"
            val productName = fullTitle.split("|").getOrNull(1)?.trim() ?: fullTitle
            val priceInUSD = product.variants?.get(0)?.price?.toDoubleOrNull() ?: 0.0
            val convertedPrice = priceInUSD * conversionRate
            val formattedPrice = String.format("%.2f", convertedPrice)

            ProductCard(
                productName = productName,
                productPrice = "$formattedPrice",
                productImage = product.images?.firstOrNull()?.src ?: "",

                currencySymbol = currencySymbols.getOrDefault(selectedCurrency, "$")

                onClick = {
                    navController!!.navigate("productDetails")
                }

            )
        }
    }
}


@Composable
fun SearchBar(searchQuery: String, onSearchQueryChange: (String) -> Unit) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text(text = "Search", color = primary) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = null,
                tint = primary
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 15.dp)
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color(0xFFE0E0E0),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = primary, strokeWidth = 4.dp,
        )
    }
}

@Composable
fun TopBar(navController: NavController, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5))
                .clickable { navController.popBackStack() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = stringResource(R.string.back),
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(end = 50.dp)
        )
    }
}


@Composable
fun PriceSlider(sliderValue: Int, maxPrice: Int, onSliderValueChange: (Int) -> Unit) {
    Column {
        Text(
            "Max Price: $sliderValue",
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 4.dp)
        )

        Slider(
            value = sliderValue.toFloat(),
            onValueChange = { newValue -> onSliderValueChange(newValue.roundToInt()) },
            valueRange = 0f..maxPrice.toFloat(),
            steps = maxPrice - 1,
            modifier = Modifier.padding(horizontal = 30.dp),
            colors = SliderDefaults.colors(
                thumbColor = primary,
                activeTrackColor = primary,
                inactiveTrackColor = Color.LightGray
            )
        )
    }
}

@Composable
fun ProductInfoMessage(isEmpty: Boolean, sliderValue: Int) {
    val message = if (isEmpty) {
        "No products found. Try adjusting your filters."
    } else {
        "Adjust the slider to filter products by price. Currently showing products under $sliderValue."
    }

    Text(
        message,
        modifier = Modifier
            .padding(horizontal = 20.dp),
        style = MaterialTheme.typography.body2,
        color = MaterialTheme.colors.onSurface
    )
}