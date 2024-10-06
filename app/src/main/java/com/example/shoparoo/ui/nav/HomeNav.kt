package com.example.shoparoo.ui.nav

import androidx.compose.foundation.layout.height
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.shoparoo.R

sealed class BottomNav(val route: String, val icon: Int, val label: String) {
    object Home : BottomNav("home", R.drawable.ic_home, "Home")
    object Categories : BottomNav("categories", R.drawable.ic_category, "Categories")
    object Cart : BottomNav("cart", R.drawable.ic_cart, "Cart")
    object Profile : BottomNav("profile", R.drawable.ic_profile, "Profile")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNav.Home,
        BottomNav.Categories,
        BottomNav.Cart,
        BottomNav.Profile,
    )

    BottomNavigation(
        modifier = Modifier.height(70.dp),
        backgroundColor = Color(0xFFEFEEEE)
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            // Determine if the item should be selected
            // Determine if the item should be selected
            val isSelected = currentRoute == item.route ||
                    (item == BottomNav.Cart && (currentRoute == "cart" || currentRoute == "checkout")) ||
                    (item == BottomNav.Profile && (currentRoute == "profile" || currentRoute == "settings")) || // Check for profile and settings
                    (currentRoute?.startsWith("brand/") == true && item == BottomNav.Home)


            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        tint = if (isSelected) Color.Black else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) Color.Black else Color.Gray
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}