@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.shoparoo.ui.theme.homeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoparoo.R

@Composable
fun HeaderOfThePage(userName: String, onFavouriteClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 65.dp)
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
            IconButton(onClick = onFavouriteClick,
                modifier = Modifier.size(70.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_home_favourite),
                    contentDescription = null,
                )
            }
        }
    }
}

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

@Preview(showSystemUi = true)
@Composable
fun SearchBarPreview() {
    SearchBar(query = TextFieldValue(""), onQueryChange = {})
}


@Composable
fun HeaderAndSearch(userName: String, onFavouriteClick: () -> Unit, query: TextFieldValue, onQueryChange: (TextFieldValue) -> Unit){
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
    }
}

@Preview(showSystemUi = true)
@Composable
fun HeaderAndSearchPreview() {
    HeaderAndSearch("George", {}, query = TextFieldValue(""), onQueryChange = {})
}
