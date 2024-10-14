@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("DEPRECATION")

package com.example.shoparoo.ui.homeScreen.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.shoparoo.ui.Favourites.viewModel.FavouritesViewModel
import com.example.shoparoo.ui.Favourites.viewModel.FavouritesViewModelFactory
import com.example.shoparoo.ui.auth.view.LoginScreen
import com.example.shoparoo.ui.auth.view.ReusableLottie
import com.example.shoparoo.ui.auth.viewModel.AuthState
import com.example.shoparoo.ui.auth.viewModel.AuthViewModel
import com.example.shoparoo.ui.categoriesScreen.view.CategoriesScreen
import com.example.shoparoo.ui.categoriesScreen.viewModel.CategoriesViewModel
import com.example.shoparoo.ui.categoriesScreen.viewModel.CategoriesViewModelFactory
import com.example.shoparoo.ui.checkOut.CheckoutScreen
import com.example.shoparoo.ui.homeScreen.viewModel.HomeViewModel
import com.example.shoparoo.ui.homeScreen.viewModel.HomeViewModelFactory
import com.example.shoparoo.ui.nav.BottomNav
import com.example.shoparoo.ui.nav.BottomNavigationBar
import com.example.shoparoo.ui.ordersScreen.view.OrderScreen
import com.example.shoparoo.ui.ordersScreen.viewModel.OrdersViewModel
import com.example.shoparoo.ui.ordersScreen.viewModel.OrdersViewModelFactory
import com.example.shoparoo.ui.productScreen.view.ProductsScreen
import com.example.shoparoo.ui.productScreen.viewModel.ProductViewModel
import com.example.shoparoo.ui.productScreen.viewModel.ProductViewModelFactory
import com.example.shoparoo.ui.settingsScreen.view.ProfileScreen
import com.example.shoparoo.ui.shoppingCart.view.ShoppingCartScreen
import com.example.shoparoo.ui.shoppingCart.viewModel.ShoppingCartViewModel
import com.example.shoparoo.ui.shoppingCart.viewModel.ShoppingCartViewModelFactory
import com.example.shoparoo.ui.theme.grey
import com.example.shoparoo.ui.theme.primary
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import networkListener

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreenDesign(
    userName: String,
    onFavouriteClick: () -> Unit,
    smartCollectionsState: ApiState,
    forYouProductsState: ApiState,
    onRefresh: () -> Unit = {},
    bottomNavController: NavController,
    navController: NavController,

    ) {
    val favViewModel: FavouritesViewModel = viewModel(
        factory = FavouritesViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )
    val fav by favViewModel.productItems.collectAsState()
    Log.i("FavouritesViewModel", "ProductItems: $fav")
    LaunchedEffect(Unit) {
        favViewModel.getFavourites()
    }

    val isNetworkAvailable = networkListener()
    if (!isNetworkAvailable.value) {
        // Show No Internet connection message
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ReusableLottie(R.raw.no_internet, R.drawable.white_bg, 400.dp, 1f)
        }
    } else {
        val context = LocalContext.current
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val selectedCurrency = remember { sharedPreferences.getString("currency", "USD") ?: "USD" }
        val conversionRate = remember { sharedPreferences.getFloat("conversionRate", 1.0f) }

        val currencySymbols = mapOf(
            "EGP" to "$ ",
            "USD" to "EGP "
        )

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
                    Header(userName, onFavouriteClick, onSearchClick = {
                        navController.navigate("search")
                    })
                }
                item {
                    CouponsSliderWithIndicator(
                        imageList = listOf(
                            R.drawable.black_friday,
                            R.drawable.nike_ads,
                            R.drawable.discount
                        ),
                        couponText = "Shoparoo20"
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
                                navController = bottomNavController,
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
                            ForYouSection(
                                products = forYouProducts as List<ProductsItem>,
                                navController,
                                selectedCurrency = selectedCurrency,
                                conversionRate = conversionRate,
                                currencySymbols = currencySymbols
                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun Header(userName: String, onFavouriteClick: () -> Unit, onSearchClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        //  horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ProfileSection(userName)
        Spacer(modifier = Modifier.weight(1f))
        if (userName != "" && userName != "Guest") {
            FavouriteButton(onFavouriteClick)
        }
        SearchButton(onSearchClick = onSearchClick)
    }
}

@Composable
fun ProfileSection(userName: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.user3),
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

@Composable
fun SearchButton(onSearchClick: () -> Unit) {
    IconButton(
        onClick = onSearchClick,
        modifier = Modifier.size(50.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_search_24),
            contentDescription = null
        )
    }
}

@Composable
fun BrandsSection(navController: NavController, smartCollections: List<SmartCollectionsItem?>) {
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
            fontSize = 21.sp,
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
            text = brandName.capitalizeWords(),
            color = primary,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ForYouSection(
    products: List<ProductsItem>, navController: NavController,
    selectedCurrency: String,
    conversionRate: Float,
    currencySymbols: Map<String, String>
) {
    val favViewModel: FavouritesViewModel = viewModel(
        factory = FavouritesViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )
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
            fontSize = 21.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        AnimatedVisibility(
            visible = visible.value,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis = 600)
            ),
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 10.dp)
            ) {
                items(randomProducts.size) { index ->
                    val product = randomProducts[index]
                    val priceInUSD = product.variants?.get(0)?.price?.toDoubleOrNull() ?: 0.0
                    val convertedPrice = priceInUSD * conversionRate
                    val formattedPrice = String.format("%.2f", convertedPrice)
                    ProductCard(
                        productName = product.title.toString(),
                        productPrice = formattedPrice,
                        productImage = product.images?.get(0)?.src,
                        onClick = { navController.navigate("productDetails/${product.id}") },
                        currencySymbol = currencySymbols[selectedCurrency] ?: "$",
                        id = product.id!!,
                        viewModel = favViewModel,
                    )
                }
            }
        }
    }

}

@SuppressLint("NewApi", "SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProductCard(
    productName: String,
    productPrice: String,
    productImage: String?,
    currencySymbol: String,
    onClick: () -> Unit,
    viewModel: FavouritesViewModel,
    id: Long,
    inFav: Boolean = false,
    onClickDeleteFav: () -> Unit = {}, // Callback for the delete icon

) {
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    if (isLoading) {
        LaunchedEffect(isLoading) {
            delay(700)
            showDialog = false
            isLoading = false
        }
    }

    val favViewModel: FavouritesViewModel = viewModel(
        factory = FavouritesViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )
    val fav by favViewModel.productItems.collectAsState()
    Log.i("FavouritesViewModel", "ProductItems: $fav")
    val authViewModel: AuthViewModel = viewModel()
    val isLoggedIn = authViewModel.authState.collectAsState()

    val favItems by viewModel.productItems.collectAsState()
    LaunchedEffect(favItems) {
        // viewModel.getFavourites()
    }



    Card(
        modifier = Modifier
            .width(175.dp)
            .height(235.dp)
            .padding(4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFAEFEEEE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = rememberAsyncImagePainter(model = productImage),
                    contentDescription = productName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.FillBounds
                )
                Text(
                    text = productName.capitalizeWords(),
                    color = primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    modifier = Modifier.padding(top = 7.dp, start = 5.dp, end = 5.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "$productPrice $currencySymbol",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 7.dp, start = 5.dp, end = 5.dp)
                )
            }

            // Conditional delete icon on top right corner
            if (inFav) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .clickable { showDialog = true }
                )
            } else if (isLoggedIn.value == AuthState.Authenticated || isLoggedIn.value == AuthState.UnVerified) {
                var isFav by remember { mutableStateOf(false) }
                fav.let {
                    for (item in it) {
                        if (item.id == id) {
                            isFav = true
                            break
                        } else {
                            isFav = false
                        }
                    }
                }
                val context = LocalContext.current
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                val animatedSize by animateFloatAsState(if (isFav) 25f else 20f)

                Icon(
                    if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Add to Favorites",
                    tint = if (isFav) Color.Red else Color.Gray,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(animatedSize.dp) // Use the animated size
                        .clickable {
                            val vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                            vibrator.cancel()
                            vibrator.vibrate(vibrationEffect)
                            isFav = !isFav
                            if (isFav) {
                                viewModel.addFav(id)
                                Toast
                                    .makeText(context, "Adding to Favorites", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                viewModel.addFav(id) // Assuming you have a removeFav method
                                Toast
                                    .makeText(
                                        context,
                                        "Removing from Favorites",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        }
                )
            }

        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Remove from Favorites", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove $productName from favorites?") },
            confirmButton = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(30.dp)
                            .padding(top = 15.dp, end = 12.dp),
                        strokeWidth = 2.dp,
                        color = Color.Gray
                    )
                } else {
                    Button(onClick = {
                        onClickDeleteFav()
                        isLoading = true // Set loading state to true
                    }, colors = androidx.compose.material3.ButtonDefaults.buttonColors(primary)) {
                        Text("Yes", color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(grey)
                ) {
                    Text("No", color = Color.White)
                }
            },
            containerColor = Color.White,
        )
    }
}


fun String.capitalizeWords(): String {
    return this.split(" ")
        .joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
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

    val shoppingCartViewModel: ShoppingCartViewModel = viewModel(
        factory = ShoppingCartViewModelFactory(
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
    val orderViewModel: OrdersViewModel = viewModel(
        factory = OrdersViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )

    val authViewModel: AuthViewModel = viewModel()


    val smartCollectionsState by viewModel.smartCollections.collectAsState()
    val forYouProductsState by viewModel.forYouProducts.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLogged = authViewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getName()
        viewModel.getSmartCollections()
        viewModel.getForYouProducts()
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navControllerBottom, isLogged) }
    ) {
        NavHost(
            navController = navControllerBottom,
            startDestination = BottomNav.Home.route,
            modifier = Modifier.padding(it),
        ) {
            composable(BottomNav.Home.route) {
                HomeScreenDesign(
                    if (isLoading) "" else userName ?: "Guest",
                    {
                        if (userName != null) {
                            navController.navigate("favourites")
                        } else {
                            navController.navigate("login")
                        }
                        //navController.navigate("favourites")

                    },

                    smartCollectionsState,
                    forYouProductsState,
                    onRefresh = {
                        viewModel.refreshData()
                    },
                    navControllerBottom,
                    navController,
                )
            }
            composable(BottomNav.Categories.route) {

                CategoriesScreen(categoryViewModel, navController)

            }
            composable(BottomNav.Cart.route) {
                ShoppingCartScreen(navControllerBottom, shoppingCartViewModel, navController)
            }
            composable(BottomNav.orders.route) {
                Text(text = "Orders Screen")
            }
            composable(BottomNav.Profile.route) {
                ProfileScreen(
                    navControllerBottom,
                )
            }
            composable(BottomNav.orders.route) {
                OrderScreen(orderViewModel = orderViewModel, navController)
            }
//            composable("settings") { SettingsScreen(navControllerBottom) }
            composable("login") { LoginScreen(navControllerBottom) }
            composable(BottomNav.Profile.route) {
                ProfileScreen(navController)
            }
//            composable("settings") {
//                SettingsScreen(navControllerBottom)
//            }
            composable("checkout") {
                CheckoutScreen(navControllerBottom, shoppingCartViewModel)
            }
            composable("brand/{brandId}/{brandTitle}") { backStackEntry ->
                val brandId =
                    backStackEntry.arguments?.getString("brandId") ?: return@composable
                val brandTitle =
                    backStackEntry.arguments?.getString("brandTitle") ?: return@composable

                ProductsScreen(
                    brandId, brandTitle, navControllerBottom, productViewModel,
                    navController
                )
            }
        }
    }
}

//@Preview(showSystemUi = true)
//@Composable
//fun HomeScreenPreview() {
//    HomeScreenDesign(
//        "George",
//        {},
//        query = TextFieldValue(""),
//        onQueryChange = {},
//        smartCollectionsState = ApiState.Loading,
//        forYouProductsState = ApiState.Loading,
//        bottomNavController = rememberNavController(),
//        navController = navController
//    )
//}