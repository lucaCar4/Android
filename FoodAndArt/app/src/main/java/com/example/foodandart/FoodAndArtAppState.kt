package com.example.foodandart

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.foodandart.ui.FoodAndArtRoute

@Stable
class FoodAndArtAppState(val navController: NavHostController) {
    private val backStackEntry = navController.currentBackStackEntry
    private val currentRoute =  {
        derivedStateOf {
            FoodAndArtRoute.routes.find {
                it.route == backStackEntry?.destination?.route
            } ?: FoodAndArtRoute.Splash.route
        }
    }
    fun popUp() {
        navController.popBackStack()
    }

    fun clear() {
        val navBackStackEntryCount = currentRoute.toString()
        //val startDestinationId = navController.clearBackStack()

    }

    fun navigate(route: String) {
        Log.d("Nav", "Navigooov")
        navController.navigate(route) {
            launchSingleTop = true


        }
    }

    fun navigateAndPopUp(route: String, popUp: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(popUp) { inclusive = true }
        }
    }

    fun clearAndNavigate(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(0) { inclusive = true }
        }
    }
}
