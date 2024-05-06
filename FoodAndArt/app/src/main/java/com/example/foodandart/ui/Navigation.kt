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
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.foodandart.ui.screens.cardDetails.CardDetailsScreen
import com.example.foodandart.ui.screens.cardDetails.CardDetailsViewModel
import com.example.foodandart.ui.screens.favorites.FavoritesViewModel
import com.example.foodandart.ui.screens.home.HomeViewModel
import com.example.foodandart.ui.screens.login.sign_in.SignInScreen
import com.example.foodandart.ui.screens.login.sign_in.SignInViewModel
import com.example.foodandart.ui.screens.splash.SplashScreen
import com.example.foodandart.ui.screens.splash.SplashViewModel
import com.example.foodandart.ui.screens.login.sign_up.SignUpScreen
import com.example.foodandart.ui.screens.login.sign_up.SignUpViewModel
import com.example.foodandart.ui.screens.profile.ProfileViewModel
import com.example.foodandart.ui.screens.purchases.PurchasesScreen
import com.example.foodandart.ui.screens.purchases.PurchasesViewModel
import com.example.foodandart.ui.screens.shopping_cart.BasketScreen
import com.example.foodandart.ui.screens.shopping_cart.BasketViewModel
import org.koin.androidx.compose.koinViewModel

sealed class FoodAndArtRoute(
    val route: String,
    val title: String,
    val navIcon: ImageVector?,
    val navIconSelected: ImageVector?,
    val arguments: List<NamedNavArgument> = emptyList(),
) {
    data object Home : FoodAndArtRoute(
        "Home",
        "Home",
        Icons.Outlined.Home,
        Icons.Filled.Home,
        listOf(navArgument("cardId") { type = NavType.StringType })
    )
    data object Basket : FoodAndArtRoute(
        "Basket",
        "Basket",
        null,
        null,
        listOf(navArgument("cardId") { type = NavType.StringType })
    )
    data object Purchases : FoodAndArtRoute(
        "Purchases",
        "Purchases",
        Icons.AutoMirrored.Outlined.ListAlt,
        Icons.AutoMirrored.Filled.ListAlt,
        listOf(navArgument("cardId") { type = NavType.StringType })
    )

    data object Favorites : FoodAndArtRoute(
        "Favorites",
        "Favorites",
        Icons.Outlined.StarBorder,
        Icons.Filled.Star,
        listOf(navArgument("cardId") { type = NavType.StringType })
    )

    data object Profile : FoodAndArtRoute(
        "Profile",
        "Profile",
        Icons.Outlined.AccountCircle,
        Icons.Filled.AccountCircle,
        listOf(navArgument("cardId") { type = NavType.StringType })
    )

    data object SignIn : FoodAndArtRoute(
        "SignIn",
        "SignIn",
        null,
        null,
        listOf(navArgument("cardId") { type = NavType.StringType })
    )

    data object CardDetails : FoodAndArtRoute(
        "cards/{cardId}",
        "CardDetails",
        null,
        null,
        listOf(navArgument("cardId") { type = NavType.StringType })
    ){
        fun buildRoute(cardId: String) = "cards/$cardId"
    }


    data object SignUp : FoodAndArtRoute(
        "SignUp",
        "SignUp",
        null,
        null,
        listOf(navArgument("cardId") { type = NavType.StringType })
    )

    data object Splash : FoodAndArtRoute(
        "Splash",
        "Splash",
        null,
        null,
        listOf(navArgument("cardId") { type = NavType.StringType })
    )

    companion object {
        val routes = setOf(Home, Purchases, Favorites, Profile, SignIn, SignUp,  Splash)
        val mainRoutes = setOf(Home, Purchases, Favorites, Profile)
    }
}

@Composable
fun FoodAndArtNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel

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

        with(FoodAndArtRoute.Purchases) {
            composable(route) {
                val purchasesViewModel = koinViewModel<PurchasesViewModel>()
                PurchasesScreen(navController , purchasesViewModel)
            }
        }

        with(FoodAndArtRoute.Basket) {
            composable(route) {
                val basketViewModel = koinViewModel<BasketViewModel>()
                BasketScreen(navController, basketViewModel)
            }
        }

        with(FoodAndArtRoute.Favorites) {
            composable(route) {
                val favoritesViewModel = koinViewModel<FavoritesViewModel>()
                FavoritesScreen(navController, favoritesViewModel)
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

        with(FoodAndArtRoute.CardDetails) {
            composable(route, arguments) { backStackEntry ->
                val cardDetailsViewModel = koinViewModel<CardDetailsViewModel>()
                CardDetailsScreen(navController ,backStackEntry.arguments?.getString("cardId") ?: "", cardDetailsViewModel)
            }
        }
    }
}
