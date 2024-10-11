package com.example.shoparoo.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shoparoo.ui.Favourites.Favourites
import com.example.shoparoo.ui.search.Search
import com.example.shoparoo.ui.auth.view.LoginScreen
import com.example.shoparoo.ui.auth.view.Signup
import com.example.shoparoo.ui.homeScreen.view.MainScreen
import com.example.shoparoo.ui.productDetails.view.ProductDetails
import com.example.shoparoo.ui.splash.Splash

//import com.example.shoparoo.ui.settingsScreen.SettingsScreen

@Composable
@Preview
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "splash") {
        composable("splash") { Splash(navController = navController) }
        composable("login") { LoginScreen(navController = navController) }
        composable("signup") { Signup(navController = navController) }
        composable("home") { MainScreen(navController = navController) }

        composable("productDetails/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            ProductDetails(id = id, navController = navController)
        }
        composable("favourites") {
            Favourites(navController = navController)
        }
        composable("search") {
            Search(navController = navController)
        }
    }
}
