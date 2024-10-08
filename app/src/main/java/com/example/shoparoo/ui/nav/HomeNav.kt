package com.example.shoparoo.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.shoparoo.R
import com.example.shoparoo.ui.theme.primary

sealed class BottomNav(val route: String, val icon: Int, val label: String) {
    object Home : BottomNav("home", R.drawable.ic_home, "Home")
    object Categories : BottomNav("categories", R.drawable.ic_category, "Categories")
    object Cart : BottomNav("cart", R.drawable.ic_cart, "Cart")
    object orders : BottomNav("orders", R.drawable.ic_orders, "Orders")
    object Profile : BottomNav("profile", R.drawable.ic_profile, "Profile")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNav.Categories,
        BottomNav.Cart,
        BottomNav.Home,
        BottomNav.orders,
        BottomNav.Profile,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            .shadow(
                8.dp,
                RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
            ) // Add shadow for elevation
    ) {
        BottomNavigation(
            modifier = Modifier
                .height(70.dp),
            backgroundColor = Color(0xFFEAEAEA)
        ) {
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
            items.forEach { item ->
                val isSelected = currentRoute == item.route ||
                        (item == BottomNav.Cart && (currentRoute == "cart" || currentRoute == "checkout")) ||
                        (item == BottomNav.Profile && (currentRoute == "profile" || currentRoute == "settings")) ||
                        (currentRoute?.startsWith("brand/") == true && item == BottomNav.Home)

                BottomNavigationItem(
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (isSelected) primary.copy(alpha = 0.2f) else Color.Transparent,
                                    shape = RoundedCornerShape(24.dp)
                                )
                        ) {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.label,
                                tint = if (isSelected) primary else Color.Gray,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 14.sp, // Increased font size for better visibility
                            color = if (isSelected) primary else Color.Gray
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
}