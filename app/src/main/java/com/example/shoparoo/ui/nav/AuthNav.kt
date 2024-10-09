package com.example.shoparoo.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shoparoo.ui.Favourites.Favourites
import com.example.shoparoo.ui.auth.view.LoginScreen
import com.example.shoparoo.ui.auth.view.Signup
import com.example.shoparoo.ui.homeScreen.view.MainScreen
import com.example.shoparoo.ui.productDetails.ProductDetails

//import com.example.shoparoo.ui.settingsScreen.SettingsScreen

@Composable
@Preview
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController = navController) }
        composable("signup") { Signup(navController = navController) }
        composable("home") { MainScreen({}, query = TextFieldValue(""), onQueryChange = {},navController = navController) }

        composable("favourites") {
            Favourites(navController = navController)
        }
        composable("productDetails/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            ProductDetails(id = id, navController = navController)
        }

    }
}
