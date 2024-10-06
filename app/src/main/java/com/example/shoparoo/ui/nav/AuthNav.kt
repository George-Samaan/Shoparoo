package com.example.shoparoo.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shoparoo.ui.auth.view.LoginScreen
import com.example.shoparoo.ui.auth.view.Signup
import com.example.shoparoo.ui.homeScreen.view.MainScreen
import com.example.shoparoo.ui.productDetails.ProductDetails

@Composable
fun Navigation(isNetworkAvailable: State<Boolean>) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController = navController) }
        composable("signup") { Signup(navController = navController) }
        composable("home") {
            MainScreen(
                isNetworkAvailable = isNetworkAvailable,
                onFavouriteClick = {},
                query = TextFieldValue(""),
                onQueryChange = {},
                navController = navController
            )
        }
        composable("productDetails") { ProductDetails(id = "7653161992291") }
    }
}
