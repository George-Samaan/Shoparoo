package com.example.shoparoo.ui.nav

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.shoparoo.ui.theme.Purple40


sealed class BottomNav(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNav("home", Icons.Filled.Home, "Home")
    object Categories : BottomNav("categories", Icons.Filled.Category, "Categories")
    object Cart : BottomNav("cart", Icons.Filled.ShoppingCart, "Cart")
    object Profile : BottomNav("profile", Icons.Filled.Person, "Profile")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNav.Home, BottomNav.Categories, BottomNav.Cart, BottomNav.Profile
    )

    BottomNavigation(
        modifier = Modifier.height(60.dp), backgroundColor = Color(0xFFEFEEEE)
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            BottomNavigationItem(icon = {
                Icon(
                    imageVector = item.icon, contentDescription = null,
                    tint = if (isSelected) Purple40 else Color.Gray
                )
            }, label = {
                Text(
                    text = item.label, color = if (isSelected) Purple40 else Color.Gray
                )
            }, selected = isSelected, onClick = {
                navController.navigate(item.route) {
                    // Prevent reselecting the same item
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            })
        }
    }
}