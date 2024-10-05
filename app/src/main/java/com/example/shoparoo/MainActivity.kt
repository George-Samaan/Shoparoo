package com.example.shoparoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shoparoo.ui.theme.ShoparooTheme
import com.example.shoparoo.ui.checkOut.CheckoutScreen
import com.example.shoparoo.db.local.SharedPreferencesImpl
import com.example.shoparoo.ui.settings.ProfileScreen
import com.example.shoparoo.ui.settings.SettingsScreen
import com.example.shoparoo.ui.settings.changeLanguage
import com.example.shoparoo.ui.shoppingCart.ShoppingCartScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

}


