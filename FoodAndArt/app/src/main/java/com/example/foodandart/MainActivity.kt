package com.example.foodandart

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.foodart.ui.theme.FoodArtTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.foodandart.data.models.Theme
import com.example.foodandart.ui.FoodAndArtNavGraph
import com.example.foodandart.ui.FoodAndArtRoute
import com.example.foodandart.ui.composable.NavBar
import com.example.foodandart.ui.screens.home.position.LocationService
import com.example.foodandart.ui.screens.login.sign_up.utils.PermissionStatus
import com.example.foodandart.ui.screens.login.sign_up.utils.rememberPermission
import com.example.foodandart.ui.screens.profile.ProfileViewModel
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val appState = rememberAppState(navController)
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

@Composable
fun rememberAppState(navController: NavHostController) =
    remember(navController) {
        FoodAndArtAppState(navController)
    }

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "${name}",
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FoodArtTheme {
        Greeting("Android")
    }
}