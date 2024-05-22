package com.example.foodandart.ui.screens.splash


import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.foodandart.data.firestore.cloud_database.purchasesChangeListener
import com.example.foodandart.service.AccountService
import com.example.foodandart.ui.FoodAndArtRoute

class SplashViewModel ( private val accountService: AccountService ) : ViewModel() {

  fun onAppStart(navController : NavController) {
    if (accountService.hasUser()) {
        navController.navigate(FoodAndArtRoute.Home.route) {
            navController.popBackStack()
        }
        purchasesChangeListener()
    }
    else {
        navController.navigate(FoodAndArtRoute.SignIn.route) {
            navController.popBackStack()
        }
    }
  }
}
