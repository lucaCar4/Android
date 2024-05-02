package com.example.foodandart.ui.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.foodandart.ui.FoodAndArtRoute

@Composable
fun NavBar(
    navController: NavController,
    currentRoute: FoodAndArtRoute
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,

        ) {
        FoodAndArtRoute.mainRoutes.forEach{ item ->
            NavigationBarItem(
                icon = { if (item.title == currentRoute.title) {
                    if (item.navIconSelected != null) {
                        Icon(item.navIconSelected, contentDescription = item.title)
                    }
                }else {
                    if (item.navIcon != null) {
                        Icon(item.navIcon, contentDescription = item.title)
                    }
                }
                    },
                label = { Text(item.title) },
                selected = false,
                onClick = {
                    if (item.route != currentRoute.route) {
                        navController.navigate(item.route) {
                            popUpTo(FoodAndArtRoute.Home.route)
                            launchSingleTop = true
                    }

                } }
            )
        }
    }
}