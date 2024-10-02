package com.example.shoparoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shoparoo.ui.theme.SettingsUi.ProfileScreen
import com.example.shoparoo.ui.theme.SettingsUi.SettingsScreen
import com.example.shoparoo.ui.theme.ShoparooTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoparooTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "profile") {
                        composable("profile") { ProfileScreen(navController) }
                        //composable("myOrders") { MyOrdersScreen(navController) }
                        // composable("wishlist") { WishlistScreen(navController) }
                        // composable("deliveryAddress") { DeliveryAddress(navController) }
                        composable("settings") { SettingsScreen(navController) }
                        composable("orderDetails/{orderId}") { backStackEntry ->
                            val orderId = backStackEntry.arguments?.getString("orderId")
                        }
                    }
                }

            }
        }
    }
}

