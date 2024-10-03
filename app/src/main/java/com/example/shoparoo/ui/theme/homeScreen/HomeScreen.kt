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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.example.shoparoo.R

@Composable
fun HeaderOfThePage(userName: String, onFavouriteClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top =30.dp)
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
                        .padding(start = 10.dp) // Only 8dp space between the image and the text
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
            .padding(vertical = 16.dp)
            .padding(horizontal = 15.dp)
            .height(56.dp), // Adjust height if needed
        shape = RoundedCornerShape(28.dp), // Rounded corners
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color(0xFFF2F2F2),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun BrandsSection(
    brandNames: List<String>,
    brandImages: List<Int> // List of drawable resource IDs
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

        // LazyRow for displaying brand items
        LazyRow(
            modifier = Modifier.height(150.dp), // Fill available height to maintain uniformity
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between cards horizontally
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
            shape = CircleShape, // Circular shape for the card
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp), // Elevation for the card
            colors = CardDefaults.cardColors(
                containerColor = Color.White // Background color for the card
            )
        ) {
            // Circular Image inside the Card
            Image(
                painter = painterResource(id = brandImage),
                contentDescription = brandName,
                modifier = Modifier
                    .fillMaxSize() // Fill the entire card with the image
                    .clip(CircleShape), // Ensure the image is circular
                contentScale = ContentScale.Crop // Crop the image to fit nicely inside the circle
            )
        }

        // Brand name below the card
        Text(
            text = brandName,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center, // Center the text
            modifier = Modifier
                .padding(top = 8.dp) // Padding above the text
        )
    }
}


@Composable
fun ForYouSection(productNames: List<String>, productPrices: List<String>, productImages: List<Int>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 16.dp,end=16.dp)
    ) {
        Text(
            text = "For You",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // LazyRow for displaying product items
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Space between cards horizontally
            modifier = Modifier.fillMaxHeight() // Fill available height if needed
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
            .width(150.dp)
            .height(200.dp)
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Image(
                painter = painterResource(id = productImage),
                contentDescription = productName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            // Product name below the image
            Text(
                text = productName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center, // Center the text
                modifier = Modifier
                    .padding(vertical = 4.dp) // Padding around the text
                    .align(Alignment.CenterHorizontally) // Center the text horizontally
            )

            // Product price below the name
            Text(
                text = productPrice,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center, // Center the text
                modifier = Modifier
                    .padding(vertical = 4.dp) // Padding around the price text
                    .align(Alignment.CenterHorizontally) // Center the price text horizontally
            )
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
        // Header Section
        HeaderOfThePage(userName = userName, onFavouriteClick = onFavouriteClick)
        // Search Bar Section
        SearchBar(
            query = query,
            onQueryChange = onQueryChange
        )
        CouponsSliderWithIndicator()
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
        ForYouSection(productNames = productName, productPrices = productName, productImages = brandImages)
    }
}

@Preview(showSystemUi = true)
@Composable
fun HeaderAndSearchPreview() {
    HomeScreenDesign("George", {}, query = TextFieldValue(""), onQueryChange = {})
}
