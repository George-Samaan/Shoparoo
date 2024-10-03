package com.example.shoparoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.text.input.TextFieldValue
import com.example.shoparoo.ui.theme.homeScreen.HeaderAndSearch
import com.example.shoparoo.ui.theme.homeScreen.HeaderOfThePage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

//            HeaderOfThePage("George",{})
            HeaderAndSearch("George", {}, query = TextFieldValue(""), onQueryChange = {})
/*            ShoparooTheme {
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

            }*/
        }
    }
}

