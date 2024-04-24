package com.example.foodandart.ui.screens.splash


import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.foodandart.service.AccountService
import com.example.foodandart.ui.FoodAndArtRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


class SplashViewModel ( private val accountService: AccountService ) : ViewModel() {

  fun onAppStart(navController : NavController) {
    if (accountService.hasUser()) navController.navigate(FoodAndArtRoute.Home.route) {
        navController.popBackStack()
    }
    else  navController.navigate(FoodAndArtRoute.SignIn.route) {
      navController.popBackStack()
    }
  }
}
