package com.example.foodandart.ui.screens.login.sign_up

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.foodandart.service.AccountService
import com.example.foodandart.ui.FoodAndArtRoute
import com.example.foodandart.ui.screens.login.sign_in.SignInViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.InternalCoroutinesApi

class SignUpViewModel( private val accountService: AccountService) : ViewModel() {

    private val PASSWORD_LENGTH = 6

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")
    var isWrong by  mutableStateOf(false)
    var emailAlreadyUsed by  mutableStateOf(false)
    var passwordLength by  mutableStateOf(false)

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        confirmPassword.value = newConfirmPassword
    }

    private fun validate(exception : String) {
        Log.d("Login", exception)
        if (exception.contains("email")) {
            emailAlreadyUsed = true
            passwordLength = password.value.length < PASSWORD_LENGTH
        } else if (exception.contains("password")) {
            passwordLength = true
            emailAlreadyUsed = false
        }
    }


    @OptIn(InternalCoroutinesApi::class)
    fun onSignUpClick(navController: NavController) {
        viewModelScope.launch {
            isWrong = password.value != confirmPassword.value
            val result = accountService.signUp(email.value, password.value)
            if (result == "" && !isWrong ) {
                navController.navigate(FoodAndArtRoute.Home.route) {
                    navController.popBackStack()
                }
            } else {
                validate(result)
            }
        }
    }

}