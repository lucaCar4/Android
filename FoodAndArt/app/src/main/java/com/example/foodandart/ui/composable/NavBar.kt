package com.example.foodandart.ui.composable

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.foodandart.R
import com.example.foodandart.ui.FoodAndArtRoute

@Composable
fun NavBar(
    navController: NavController,
    currentRoute: FoodAndArtRoute
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,

        ) {
        FoodAndArtRoute.mainRoutes.forEach { item ->
            NavigationBarItem(
                icon = {
                    if (item.title == currentRoute.title) {
                        if (item.navIconSelected != null) {
                            Icon(item.navIconSelected, contentDescription = item.title, tint = Color.Yellow)
                        }
                    } else {
                        if (item.navIcon != null) {
                            Icon(item.navIcon, contentDescription = item.title)
                        }
                    }
                },
                label = {
                    if (item.title == currentRoute.title) {
                        Text(getLabel(title = item.title), color = Color.Yellow)
                    } else {
                        Text(getLabel(title = item.title))
                    }
                                                          },

                selected = false,
                onClick = {
                    if (item.route != currentRoute.route) {
                        navController.navigate(item.route) {
                            popUpTo(FoodAndArtRoute.Home.route)
                            launchSingleTop = true
                        }

                    }
                }
            )
        }
    }
}

@Composable
private fun getLabel(title: String): String {
    return when (title) {
        "Home" -> stringResource(id = R.string.home)
        "Purchases" -> stringResource(id = R.string.purchases)
        "Favorites" -> stringResource(id = R.string.favorites)
        "Profile" -> stringResource(id = R.string.profile)
        else -> ""
    }
}