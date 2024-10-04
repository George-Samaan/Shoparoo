package com.example.shoparoo

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shoparoo.set3.LoginScreen
import com.example.shoparoo.set3.Signup

@Composable
@Preview
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "login") {
      composable("login") { LoginScreen(navController = navController) }
        composable("signup") { Signup(navController = navController)  }
    }
}