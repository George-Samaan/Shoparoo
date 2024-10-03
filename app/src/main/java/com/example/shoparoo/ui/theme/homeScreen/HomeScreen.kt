@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.shoparoo.ui.theme.homeScreen

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shoparoo.R
import com.example.shoparoo.nav.BottomNav
import com.example.shoparoo.nav.BottomNavigationBar

@Composable
fun HeaderOfThePage(userName: String, onFavouriteClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Profile Image and Greeting in a Row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Profile Picture
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                // Greeting Message right after the image
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                ) {
                    Text("Hello!", fontSize = 16.sp)
                    Text(
                        text = userName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            // Favourite Button on the far right
            IconButton(
                onClick = onFavouriteClick,
                modifier = Modifier.size(70.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_home_favourite),
                    contentDescription = null,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit
) {
    // TextField for search
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
        colors = textFieldColors(
            containerColor = Color(0xFFF2F2F2),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun BrandsSection(
    brandNames: List<String>,
    brandImages: List<Int>
) {
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
            items(brandNames.size) { index ->
                CircularBrandCard(brandName = brandNames[index], brandImage = brandImages[index])
            }
        }
    }
}

@Composable
fun CircularBrandCard(brandName: String, brandImage: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // Center items horizontally
        modifier = Modifier.padding(6.dp)
    ) {
        // Circular Card with Image
        Card(
            modifier = Modifier
                .size(110.dp), // Fixed size for the card
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White // Background color for the card
            )
        ) {
            // Circular Image inside the Card
            Image(
                painter = painterResource(id = brandImage),
                contentDescription = brandName,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop // Crop the image to fit nicely inside the circle
            )
        }
        // Brand name below the card
        Text(
            text = brandName,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 8.dp)
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

        // LazyRow for displaying product items
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            items(productNames.size) { index ->
                ProductCard(
                    productName = productNames[index],
                    productPrice = productPrices[index],
                    productImage = productImages[index]
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
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEFEEEE),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = productImage),
                contentDescription = productName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop
            )
            // Product name below the image
            Text(
                text = productName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 7.dp, start = 5.dp, end = 5.dp)
            )

            // Product price below the name
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

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) {
        // Use NavHost to control screen navigation based on selected bottom nav item
        NavHost(
            navController = navController,
            startDestination = BottomNav.Home.route, // Default start screen
            modifier = Modifier.padding(it) // Ensure content is not covered by the BottomNav
        ) {
            // Define the different composable screens
            composable(BottomNav.Home.route) {
                HomeScreenDesign(
                    userName = userName,
                    onFavouriteClick = onFavouriteClick,
                    query = query,
                    onQueryChange = onQueryChange
                )
            }
            composable(BottomNav.Categories.route) {
            }
            composable(BottomNav.Cart.route) {
            }
            composable(BottomNav.Profile.route) {
            }
        }
    }
}

@Composable
fun HomeScreenDesign(
    userName: String,
    onFavouriteClick: () -> Unit,
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        HeaderOfThePage(userName = userName, onFavouriteClick = onFavouriteClick)
        SearchBar(
            query = query,
            onQueryChange = onQueryChange
        )
        CouponsSliderWithIndicator(
            listOf(
                R.drawable.fifty_off,
                R.drawable.nike_discount,
                R.drawable.twenty_discount,
            )
        )
        val brandNames = listOf(
            "Nike",
            "Adidas",
            "Puma",
            "Reebok",
        )
        val brandImages = listOf(
            R.drawable.ic_watch,
            R.drawable.ic_watch,
            R.drawable.ic_watch,
            R.drawable.ic_watch,
        )
        val productName = listOf(
            "Nike Air Max",
            "Adidas Superstar",
            "Puma Suede",
            "Reebok Classic",
        )
        BrandsSection(brandNames = brandNames, brandImages = brandImages)
        ForYouSection(
            productNames = productName,
            productPrices = productName,
            productImages = brandImages
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun HeaderAndSearchPreview() {
    HomeScreenDesign("George", {}, query = TextFieldValue(""), onQueryChange = {})
}
