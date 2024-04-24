package com.example.foodandart.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.foodandart.ui.screens.favorites.FavoritesScreen
import com.example.foodandart.ui.screens.home.HomeScreen
import com.example.foodandart.ui.screens.profile.ProfileScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.foodandart.ui.screens.home.HomeViewModel
import com.example.foodandart.ui.screens.login.sign_in.SignInScreen
import com.example.foodandart.ui.screens.login.sign_in.SignInViewModel
import com.example.foodandart.ui.screens.splash.SplashScreen
import com.example.foodandart.ui.screens.splash.SplashViewModel
import com.notes.app.screens.sign_up.SignUpScreen
import com.example.foodandart.ui.screens.login.sign_up.SignUpViewModel
import com.example.foodandart.ui.screens.profile.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

sealed class FoodAndArtRoute(
    val route: String,
    val title: String,
    val navIcon: ImageVector,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    data object Home : FoodAndArtRoute(
        "Home",
        "Home",
        Icons.Outlined.Home,
        listOf(navArgument("travelId") { type = NavType.StringType })
    )

    data object Favorites : FoodAndArtRoute(
        "Favorites",
        "Favorites",
        Icons.Outlined.Star,
        listOf(navArgument("travelId") { type = NavType.StringType })
    )

    data object Profile : FoodAndArtRoute(
        "Profile",
        "Profile",
        Icons.Outlined.AccountCircle,
        listOf(navArgument("travelId") { type = NavType.StringType })
    )

    data object SignIn : FoodAndArtRoute(
        "SignIn",
        "SignIn",
        Icons.Outlined.AccountCircle,
        listOf(navArgument("travelId") { type = NavType.StringType })
    )


    data object SignUp : FoodAndArtRoute(
        "SignUp",
        "SignUp",
        Icons.Outlined.AccountCircle,
        listOf(navArgument("travelId") { type = NavType.StringType })
    )

    data object Splash : FoodAndArtRoute(
        "Splash",
        "Splash",
        Icons.Outlined.AccountCircle,
        listOf(navArgument("travelId") { type = NavType.StringType })
    )

    companion object {
        val routes = setOf(Home, Favorites, Profile, SignIn, SignUp,  Splash)
        val mainRoutes = setOf(Home, Favorites, Profile)
    }
}

@Composable
fun FoodAndArtNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    profileViewModel : ProfileViewModel
) {
    NavHost(
        navController = navController,
        startDestination = FoodAndArtRoute.Splash.route,
        modifier = modifier
    ) {

        with(FoodAndArtRoute.Home) {
            composable(route) {
                val homeViewModel = koinViewModel<HomeViewModel>()
                HomeScreen(navController, homeViewModel)
            }
        }

        with(FoodAndArtRoute.Favorites) {
            composable(route) {
                FavoritesScreen(navController)
            }
        }

        with(FoodAndArtRoute.Profile) {
            composable(route) {
                ProfileScreen(navController, profileViewModel)
            }
        }

        with(FoodAndArtRoute.SignIn) {
            composable(route) {
                val signInViewModel = koinViewModel<SignInViewModel>()
                SignInScreen(navController, viewModel = signInViewModel)
            }
        }

        with(FoodAndArtRoute.SignUp) {
            composable(route) {
                val signUpViewModel = koinViewModel<SignUpViewModel>()
                SignUpScreen(navController, viewModel = signUpViewModel)
            }
        }

        with(FoodAndArtRoute.Splash) {
            composable(route) {
                val splashViewModel = koinViewModel<SplashViewModel>()
                SplashScreen(navController, viewModel = splashViewModel)
            }
        }
    }
}
