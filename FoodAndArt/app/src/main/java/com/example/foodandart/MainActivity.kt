package com.example.foodandart

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.foodandart.data.models.Theme
import com.example.foodandart.ui.FoodAndArtNavGraph
import com.example.foodandart.ui.FoodAndArtRoute
import com.example.foodandart.ui.composable.NavBar
import com.example.foodandart.ui.screens.profile.ProfileViewModel
import com.example.foodart.ui.theme.FoodArtTheme
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = "foodandart_osmdroid"
        setContent {
            val navController = rememberNavController()
            val viewModel = koinViewModel<ProfileViewModel>()
            val state = viewModel.state
            FoodArtTheme(
                darkTheme = when (state) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                }
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute by remember {
                        derivedStateOf {
                            FoodAndArtRoute.routes.find {
                                it.route == backStackEntry?.destination?.route
                            } ?: FoodAndArtRoute.Splash
                        }
                    }
                    Scaffold(
                        bottomBar = {
                            Log.d("Route", currentRoute.route)
                            if (FoodAndArtRoute.mainRoutes.contains(currentRoute)) {
                                NavBar(navController, currentRoute)
                            }
                        },
                    ) { contentPadding ->
                        FoodAndArtNavGraph(
                            navController,
                            modifier = Modifier.padding(contentPadding),
                            viewModel
                        )
                    }
                }
            }
        }
    }
}