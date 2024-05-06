package com.example.foodandart.ui.screens.login.sign_in

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.foodandart.service.AccountService
import com.example.foodandart.ui.FoodAndArtRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val accountService: AccountService
) : ViewModel() {
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    var emailIsCorrect by mutableStateOf(true)
    var generalCorrect by mutableStateOf(true)

    var passwordHide by mutableStateOf(true)

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun onSignInClick(navController: NavController) {
        viewModelScope.launch {
            val res = accountService.signIn(email.value, password.value)
            if (res == "") {
                navController.navigate(FoodAndArtRoute.Home.route) {
                    navController.popBackStack()
                }
            } else {
                validate(res)
            }
        }
    }

    private fun validate(exception: String) {
        Log.d("Login", exception)
        if (exception.contains("credential")) {
            emailIsCorrect = true
            generalCorrect = false
        } else if (exception.contains("email")) {
            emailIsCorrect = false
            generalCorrect = true
        }
    }
}