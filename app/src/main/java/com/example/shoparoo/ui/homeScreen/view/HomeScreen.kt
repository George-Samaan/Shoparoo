@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.shoparoo.ui.homeScreen.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.shoparoo.R
import com.example.shoparoo.data.db.remote.RemoteDataSourceImpl
import com.example.shoparoo.data.db.repository.RepositoryImpl
import com.example.shoparoo.data.network.ApiClient
import com.example.shoparoo.data.network.ApiState
import com.example.shoparoo.model.SmartCollectionsItem
import com.example.shoparoo.ui.homeScreen.viewModel.HomeViewModel
import com.example.shoparoo.ui.homeScreen.viewModel.HomeViewModelFactory
import com.example.shoparoo.ui.nav.BottomNav
import com.example.shoparoo.ui.nav.BottomNavigationBar

/*@Composable
fun HomeScreenDesign(
    userName: String,
    onFavouriteClick: () -> Unit,
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Header(userName, onFavouriteClick)
        SearchBar(query, onQueryChange)
        CouponsSliderWithIndicator(
            imageList = listOf(
                R.drawable.fifty_off,
                R.drawable.nike_discount,
                R.drawable.twenty_discount
            )
        )
        val brandNames = listOf("Nike", "Adidas", "Puma", "Reebok")
        val brandImages = listOf(
            R.drawable.ic_watch,
            R.drawable.ic_watch,
            R.drawable.ic_watch,
            R.drawable.ic_watch
        )
        val productNames =
            listOf("Nike Air Max", "Adidas Superstar", "Puma Suede", "Reebok Classic")
        val productPrices = listOf("$199", "$149", "$119", "$99")

        BrandsSection(brandNames, brandImages)
        ForYouSection(productNames, productPrices, brandImages)
    }
}*/

@Composable
fun HomeScreenDesign(
    userName: String,
    onFavouriteClick: () -> Unit,
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    smartCollectionsState: ApiState
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Header(userName, onFavouriteClick)
        SearchBar(query, onQueryChange)
        CouponsSliderWithIndicator(
            imageList = listOf(
                R.drawable.fifty_off,
                R.drawable.nike_discount,
                R.drawable.twenty_discount
            )
        )

        // Handle smart collections based on the state
        when (smartCollectionsState) {
            is ApiState.Loading -> {}
            is ApiState.Failure -> {
                // Handle the error state, e.g., show an error message
                Text(text = "Error fetching brands", color = Color.Red)
            }

            is ApiState.Success -> {
                val smartCollections = (smartCollectionsState as ApiState.Success).data
                BrandsSection(smartCollections as List<SmartCollectionsItem?>) // Pass smart collections to BrandsSection
            }
        }
    }
}

/*@Composable
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
}*/

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
            Text("Hello!", fontSize = 16.sp)
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
        placeholder = { Text(text = "Search") },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = null
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 15.dp)
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color(0xFFF2F2F2),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun BrandsSection(smartCollections: List<SmartCollectionsItem?>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Brands",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyRow(
            modifier = Modifier.height(150.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(smartCollections.size) { index ->
                val collection = smartCollections[index]
                if (collection != null) {
                    CircularBrandCard(collection.title ?: "Unknown", collection.image?.src!!)
                }
            }
        }
    }
}

@Composable
fun CircularBrandCard(brandName: String, brandImage: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(6.dp)
    ) {
        Card(
            modifier = Modifier.size(110.dp),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Image(
                painter = rememberImagePainter(brandImage),
                contentDescription = brandName,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = brandName,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ForYouSection(
    productNames: List<String>,
    productPrices: List<String>,
    productImages: List<Int>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "For You",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            items(productNames.size) { index ->
                ProductCard(
                    productNames[index],
                    productPrices[index],
                    productImages[index]
                )
            }
        }
    }
}

@Composable
fun ProductCard(productName: String, productPrice: String, productImage: Int) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .height(240.dp)
            .padding(6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEEEE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = productImage),
                contentDescription = productName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = productName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 7.dp, start = 5.dp, end = 5.dp)
            )
            Text(
                text = productPrice,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 5.dp, start = 5.dp, end = 5.dp)
            )
        }
    }
}


@Composable
fun MainScreen(
    userName: String,
    onFavouriteClick: () -> Unit,
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit
) {
    val navController = rememberNavController()
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            repository = RepositoryImpl(
                remoteDataSource = RemoteDataSourceImpl(apiService = ApiClient.retrofit)
            )
        )
    )
    val smartCollectionsState by viewModel.smartCollections.collectAsState()

    // Call to fetch smart collections
    LaunchedEffect(Unit) {
        viewModel.getSmartCollections()
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) {
        NavHost(
            navController = navController,
            startDestination = BottomNav.Home.route,
            modifier = Modifier.padding(it)
        ) {
            composable(BottomNav.Home.route) {
                HomeScreenDesign(
                    userName,
                    onFavouriteClick,
                    query,
                    onQueryChange,
                    smartCollectionsState
                )
            }
            composable(BottomNav.Categories.route) { }
            composable(BottomNav.Cart.route) { }
            composable(BottomNav.Profile.route) { }
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
        smartCollectionsState = ApiState.Loading
    )
}