package com.example.shoparoo.ui.productScreen.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun ProductsScreen(brandId: String) {
    // Products screen logic using brandId
    Text(text = "Products for brand ID: $brandId")
}