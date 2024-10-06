@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.shoparoo.ui.homeScreen.view

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.shoparoo.R
import com.example.shoparoo.data.db.remote.RemoteDataSourceImpl
import com.example.shoparoo.data.network.ApiClient
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.data.repository.RepositoryImpl
import com.example.shoparoo.model.ProductsItem
import com.example.shoparoo.model.SmartCollectionsItem
import com.example.shoparoo.ui.auth.view.LoginScreen
import com.example.shoparoo.ui.categoriesScreen.view.CategoriesScreen
import com.example.shoparoo.ui.categoriesScreen.viewModel.CategoriesViewModel
import com.example.shoparoo.ui.categoriesScreen.viewModel.CategoriesViewModelFactory
import com.example.shoparoo.ui.checkOut.CheckoutScreen
import com.example.shoparoo.ui.homeScreen.viewModel.HomeViewModel
import com.example.shoparoo.ui.homeScreen.viewModel.HomeViewModelFactory
import com.example.shoparoo.ui.nav.BottomNav
import com.example.shoparoo.ui.nav.BottomNavigationBar
import com.example.shoparoo.ui.productScreen.view.ProductsScreen
import com.example.shoparoo.ui.productScreen.viewModel.ProductViewModel
import com.example.shoparoo.ui.productScreen.viewModel.ProductViewModelFactory
import com.example.shoparoo.ui.settingsScreen.ProfileScreen
import com.example.shoparoo.ui.settingsScreen.SettingsScreen
import com.example.shoparoo.ui.shoppingCart.ShoppingCartScreen
import com.example.shoparoo.ui.theme.primary
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Suppress("UNCHECKED_CAST")
@Composable
fun HomeScreenDesign(
    userName: String,
    onFavouriteClick: () -> Unit,
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    smartCollectionsState: ApiState,
    forYouProductsState: ApiState,
    onRefresh: () -> Unit = {},
    navController: NavController
) {
    val isRefreshing = remember { mutableStateOf(false) }
    isRefreshing.value =
        smartCollectionsState is ApiState.Loading || forYouProductsState is ApiState.Loading

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing.value),
        onRefresh = {
            isRefreshing.value = true
            onRefresh()
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Header(userName, onFavouriteClick)
            }
            item {
                SearchBar(query, onQueryChange)
            }
            item {
                CouponsSliderWithIndicator(
                    imageList = listOf(
                        R.drawable.black_friday,
                        R.drawable.nike_ads,
                        R.drawable.discount
                    ),
                    couponText = "20% Off All Products"
                )
            }
            when (smartCollectionsState) {
                is ApiState.Loading -> {}
                is ApiState.Failure -> {
                    item {
                        Text(
                            text = "Error fetching brands",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                is ApiState.Success -> {
                    val smartCollections = (smartCollectionsState).data
                    item {
                        BrandsSection(
                            navController = navController,
                            smartCollections as List<SmartCollectionsItem?>
                        ) // Pass navController here
                    }
                }
            }
            when (forYouProductsState) {
                is ApiState.Loading -> {}
                is ApiState.Failure -> {
                    item {
                        val errorMessage = (forYouProductsState)
                        Text(
                            text = "Error fetching products: $errorMessage",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                is ApiState.Success -> {
                    val forYouProducts = (forYouProductsState).data
                    item {
                        ForYouSection(forYouProducts as List<ProductsItem>)
                    }
                }
            }
        }
    }
}

@Composable
fun Header(userName: String, onFavouriteClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ProfileSection(userName)
        FavouriteButton(onFavouriteClick)
    }
}

@Composable
fun ProfileSection(userName: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text("Hello!", fontSize = 18.sp)
            Text(userName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FavouriteButton(onFavouriteClick: () -> Unit) {
    IconButton(
        onClick = onFavouriteClick,
        modifier = Modifier.size(70.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_home_favourite),
            contentDescription = null
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: TextFieldValue, onQueryChange: (TextFieldValue) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
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
fun BrandsSection(
    navController: NavController,
    smartCollections: List<SmartCollectionsItem?>
) {
    val visible = remember { mutableStateOf(false) }

    LaunchedEffect(smartCollections) {
        if (smartCollections.isNotEmpty()) {
            visible.value = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Brands",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        AnimatedVisibility(
            visible = visible.value,
            enter = slideInHorizontally(
                initialOffsetX = { -it }, // Start from the left
                animationSpec = tween(durationMillis = 600) // Duration of the animation
            ),
        ) {
            LazyRow(
                modifier = Modifier.height(150.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(smartCollections.size) { index ->
                    val collection = smartCollections[index]
                    if (collection != null) {
                        CircularBrandCard(
                            brandName = collection.title ?: "Unknown",
                            brandImage = collection.image?.src!!,
                            onClick = {
                                // Log the ID of the clicked brand
                                Log.d("BrandsSection", "Clicked brand ID: ${collection.id}")
                                navController.navigate("brand/${collection.id}/${collection.title}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CircularBrandCard(brandName: String, brandImage: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(6.dp)
            .clickable { onClick() }
    ) {
        Card(
            modifier = Modifier.size(110.dp),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Image(
                painter = rememberAsyncImagePainter(brandImage),
                contentDescription = brandName,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
        }
        Text(
            text = brandName, color = primary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
fun ForYouSection(products: List<ProductsItem>) {
    val randomProducts = remember { products.shuffled().take(5) }
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(randomProducts) {
        if (randomProducts.isNotEmpty()) {
            visible.value = true
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "For You",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        AnimatedVisibility(
            visible = visible.value,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis = 600) // Duration of the animation
            ),
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                items(randomProducts.size) { index ->
                    val product = randomProducts[index]
                    val price = product.variants?.get(0)?.price?.toDoubleOrNull()?.toInt()
                        ?: 0 // Convert to int

                    ProductCard(
                        productName = product.title.toString(),
                        productPrice = "$price",
                        productImage = product.images?.get(0)?.src,
                        onClick = {

                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCard(productName: String, productPrice: String, productImage: String?,onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .height(240.dp)
            .padding(6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(model = productImage),
                contentDescription = productName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = productName,
                color = primary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 7.dp, start = 5.dp, end = 5.dp),
                maxLines = 2, // Set to 2 lines
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.weight(1f))
            Text(
                // this USD if changed will change in all the app till now
                text = "$productPrice USD",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 7.dp, start = 5.dp, end = 5.dp)
            )
        }
    }
}


@Composable
fun MainScreen(
    onFavouriteClick: () -> Unit,
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    navController: NavController
) {
    val navControllerBottom = rememberNavController()
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )

    val productViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )
    val categoryViewModel: CategoriesViewModel = viewModel(
        factory = CategoriesViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )

    val smartCollectionsState by viewModel.smartCollections.collectAsState()
    val forYouProductsState by viewModel.forYouProducts.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

// Call to fetch smart collections
    LaunchedEffect(Unit) {
        viewModel.getSmartCollections()
        viewModel.getForYouProducts()
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navControllerBottom) }
    ) {
        NavHost(
            navController = navControllerBottom,
            startDestination = BottomNav.Home.route,
            modifier = Modifier.padding(it)
        ) {
            composable(BottomNav.Home.route) {
                HomeScreenDesign(
                    if (isLoading) "" else userName ?: "Guest",
                    onFavouriteClick,
                    query,
                    onQueryChange,
                    smartCollectionsState,
                    forYouProductsState,
                    onRefresh = {
                        viewModel.refreshData()
                    },
                    navControllerBottom
                )
            }
            composable(BottomNav.Categories.route) {
                CategoriesScreen(categoryViewModel, navController)
            }
            composable(BottomNav.Cart.route) {
                ShoppingCartScreen(navControllerBottom)
            }

            composable(BottomNav.Profile.route) { ProfileScreen(navControllerBottom, navController) }
            composable("settings") { SettingsScreen(navControllerBottom) }
            composable("login") { LoginScreen(navControllerBottom)}
            composable(BottomNav.Profile.route) {
                ProfileScreen(navControllerBottom,navController)
            }
            composable("settings") {
                SettingsScreen(navControllerBottom)
            }
            composable("checkout") {
                CheckoutScreen(navControllerBottom)
            }
            composable("brand/{brandId}/{brandTitle}") { backStackEntry ->
                val brandId = backStackEntry.arguments?.getString("brandId") ?: return@composable
                val brandTitle =
                    backStackEntry.arguments?.getString("brandTitle") ?: return@composable
                ProductsScreen(brandId, brandTitle, navControllerBottom, productViewModel,navController)
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreenDesign(
        "George",
        {},
        query = TextFieldValue(""),
        onQueryChange = {},
        smartCollectionsState = ApiState.Loading,
        forYouProductsState = ApiState.Loading,
        navController = rememberNavController()
    )
}