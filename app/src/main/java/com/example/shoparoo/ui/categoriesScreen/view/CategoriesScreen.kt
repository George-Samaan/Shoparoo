@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.shoparoo.ui.categoriesScreen.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoparoo.R
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.model.ProductsItem
import com.example.shoparoo.ui.categoriesScreen.viewModel.CategoriesViewModel
import com.example.shoparoo.ui.productScreen.view.LoadingIndicator
import com.example.shoparoo.ui.productScreen.view.PriceSlider
import com.example.shoparoo.ui.productScreen.view.ProductGrid
import com.example.shoparoo.ui.productScreen.view.ProductInfoMessage
import com.example.shoparoo.ui.theme.Purple40
import com.example.shoparoo.ui.theme.bg
import com.example.shoparoo.ui.theme.primary
import kotlinx.coroutines.delay

@Composable
fun CategoriesScreen(viewModel: CategoriesViewModel, navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Men") } // Default filter
    var sliderValue by remember { mutableIntStateOf(0) }
    var maxPrice by remember { mutableIntStateOf(2500) }
    var isReady by remember { mutableStateOf(false) }
    var isFilteringComplete by remember { mutableStateOf(false) }
    var products by remember { mutableStateOf<List<ProductsItem>>(emptyList()) }
    var filteredProducts by remember { mutableStateOf(emptyList<ProductsItem>()) }

    var showProductTypeMenu by remember { mutableStateOf(false) }
    var selectedProductType by remember { mutableStateOf("Shoes") } // Default product type

    val womenProductsState = viewModel.womenProducts.collectAsState()
    val menProductsState = viewModel.mensProducts.collectAsState()
    val salesProductsState = viewModel.salesProducts.collectAsState()
    val kidsProductsState = viewModel.kidsProducts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getWomenProducts()
        viewModel.getMensProducts()
        viewModel.getSalesProducts()
        viewModel.getKidsProducts()
    }

    // Update filtering when product type or other filters change
    LaunchedEffect(
        selectedFilter,
        selectedProductType,
        menProductsState.value,
        womenProductsState.value,
        salesProductsState.value,
        kidsProductsState.value
    ) {
        val allProducts = when (selectedFilter) {
            "Men" -> menProductsState.value
            "Women" -> womenProductsState.value
            "Kids" -> kidsProductsState.value
            "Sale" -> salesProductsState.value
            else -> null
        }

        when (val state = allProducts) {
            is ApiState.Success -> {
                products = (state.data as? List<ProductsItem>) ?: emptyList()
                maxPrice = products.map {
                    it.variants?.firstOrNull()?.price?.toFloatOrNull()?.toInt() ?: 0
                }.maxOrNull() ?: 2500
                sliderValue = maxPrice
                isReady = true
                filteredProducts =
                    filterProductsByType(products, selectedProductType, searchQuery, sliderValue)
                isFilteringComplete = true
            }

            is ApiState.Loading -> {
                isReady = false
                isFilteringComplete = false
            }

            is ApiState.Failure -> {
                isReady = true
            }

            else -> {}
        }
    }

    LaunchedEffect(searchQuery, sliderValue, selectedProductType) {
        isFilteringComplete = false
        delay(300)
        filteredProducts =
            filterProductsByType(products, selectedProductType, searchQuery, sliderValue)
        isFilteringComplete = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            com.example.shoparoo.ui.productScreen.view.SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { query -> searchQuery = query },
            )

            FilterBar(selectedFilter) { filter ->
                selectedFilter = filter
            }

            PriceSlider(sliderValue, maxPrice) { newValue -> sliderValue = newValue }

            if (!isReady) {
                LoadingIndicator()
            } else {
                AnimatedContent(targetState = filteredProducts.isEmpty()) { isEmpty ->
                    ProductInfoMessage(isEmpty = isEmpty, sliderValue = sliderValue)
                }

                AnimatedVisibility(
                    visible = isReady && isFilteringComplete,
                    enter = scaleIn(animationSpec = tween(durationMillis = 600)),
                    exit = scaleOut(animationSpec = tween(durationMillis = 600))
                ) {

                    ProductGrid(filteredProducts, navController = navController)
                }
            }
        }

        // Main Floating Action Button
        val fabModifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 8.dp, end = 16.dp)

        FloatingActionButton(
            onClick = { showProductTypeMenu = !showProductTypeMenu },
            containerColor = primary,
            contentColor = Color.White,
            modifier = fabModifier
        ) {
            if (showProductTypeMenu) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Show Filters"
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = "Filter Products"
                )
            }
        }

        // FABs appearing under the main FAB
        if (showProductTypeMenu) {
            FilterTypeFABs(
                selectedProductType = selectedProductType,
                onProductTypeSelected = { type ->
                    selectedProductType = type
                    showProductTypeMenu = false // Close menu after selection
                },
                modifier = Modifier.align(Alignment.TopEnd) // Aligning to the right
            )
        }
    }
}

@Composable
fun FilterTypeFABs(
    selectedProductType: String,
    onProductTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val productTypes = listOf("Shoes", "Accessories", "T-Shirts")

    // Column layout for FABs to display vertically
    Column(
        modifier = modifier
            .padding(top = 70.dp)
            .padding(end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        productTypes.forEach { type ->
            FloatingActionButton(
                onClick = { onProductTypeSelected(type) },
                containerColor = if (type == selectedProductType) Purple40 else Color.LightGray,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                when (type) {
                    "Shoes" -> Icon(
                        painter = painterResource(id = R.drawable.ic_shoes),
                        contentDescription = "Shoes"
                    )

                    "Accessories" -> Icon(
                        painter = painterResource(id = R.drawable.ic_accessories),
                        contentDescription = "Accessories"
                    )

                    "T-Shirts" -> Icon(
                        painter = painterResource(id = R.drawable.ic_shirt),
                        contentDescription = "T-Shirts"
                    )
                }
            }
        }
    }
}

// Filtering logic function for product types
fun filterProductsByType(
    products: List<ProductsItem>,
    productType: String,
    searchQuery: String,
    sliderValue: Int
): List<ProductsItem> {
    return products.filter { product ->
        val productPrice =
            product.variants?.firstOrNull()?.price?.toFloatOrNull()?.toInt() ?: 0
        val matchesSearch = searchQuery.isEmpty() || product.title?.contains(
            searchQuery,
            ignoreCase = true
        ) == true
        val withinPriceRange = productPrice <= sliderValue
        val matchesProductType =
            product.productType?.equals(productType, ignoreCase = true) ?: false

        matchesSearch && withinPriceRange && matchesProductType
    }
}

@Composable
fun FilterBar(
    selectedFilter: String?,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("Men", "Women", "Kids", "Sale")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        filters.forEach { filter ->
            Text(
                text = filter,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (filter == selectedFilter) primary else Color.Black,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (filter == selectedFilter) bg else Color.Transparent)
                    .clickable { onFilterSelected(filter) }
                    .padding(8.dp)
            )
        }
    }
}
